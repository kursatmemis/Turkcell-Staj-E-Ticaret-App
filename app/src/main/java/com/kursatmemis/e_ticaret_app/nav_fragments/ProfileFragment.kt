package com.kursatmemis.e_ticaret_app.nav_fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.databinding.FragmentProfileBinding
import com.kursatmemis.e_ticaret_app.models.Address
import com.kursatmemis.e_ticaret_app.models.ControlResult
import com.kursatmemis.e_ticaret_app.models.Coordinates
import com.kursatmemis.e_ticaret_app.models.Date
import com.kursatmemis.e_ticaret_app.models.UserAllData
import com.kursatmemis.e_ticaret_app.models.UserProfileData
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar


class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private lateinit var context: Context
    private var userProfileData: UserProfileData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        context = inflater.context
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setCollapsingToolbarAttriutes()

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val message = "Failed to fetch data."
            val type = FancyToast.ERROR
            showProgressBar()
            if (MainActivity.isServiceLogin) {
                val userAllData = getUserAllDataFromService()
                if (userAllData == null) {
                    showFancyToast(message, type)
                } else {
                    userProfileData = getUserProfileData(userAllData)
                }
            } else {
                userProfileData = getUserProfileDataFromFirebase()
            }
            hideProgressBar()
            if (userProfileData != null) {
                showUserDataOnScreen(userProfileData)
            } else {
                showFancyToast(message, type)
            }
        }

        binding?.birthDateEditText?.setOnClickListener {
            val parsedDate: Date
            val date = binding?.birthDateEditText?.text.toString()
            if (date.isEmpty()) {
                val currentDate = getCurrentDate()
                parsedDate = parseDate(currentDate)
            } else {
                parsedDate = parseDate(date)
            }
            val year = parsedDate.year
            val month = parsedDate.month
            val day = parsedDate.day
            setDataPickerDialog(year, month, day)
        }

        binding?.saveButton?.setOnClickListener {
            val userProfileData = getUserProfileDataFromEditText()
            val controlResult = controlEmptyField(userProfileData)

            if (!controlResult.result) {
                val message = controlResult.message
                val fancyToastType = FancyToast.WARNING
                showFancyToast(message, fancyToastType)
                return@setOnClickListener
            }

            if (MainActivity.isServiceLogin) {
                showProgressBar()
                updateUserProfileAndShowResult(userProfileData)
            } else {
                showProgressBar()
                saveToDatabaseAndShowResult(userProfileData)
            }

        }

        return binding!!.root
    }

    private fun updateUserProfileAndShowResult(userProfileData: UserProfileData) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val isUpdated = RetrofitManager.updateUserProfile(userProfileData)
            val message = if (isUpdated) "Your profile is updated."
            else "An error occurred while updating the profile. Please try again."
            val fancyToastType = if (isUpdated) FancyToast.SUCCESS else FancyToast.WARNING
            showFancyToast(message, fancyToastType)
            hideProgressBar()
        }
    }

    private suspend fun getUserProfileDataFromFirebase(): UserProfileData? {
        return FirebaseManager.getProfileData()
    }

    private suspend fun getUserAllDataFromService(): UserAllData? {
        return RetrofitManager.getUserAllData()
    }

    private fun saveToDatabaseAndShowResult(userProfileData: UserProfileData) {
        val database = Firebase.database.reference
        database.child("profile").child(MainActivity.userId.toString()).setValue(userProfileData)
            .addOnSuccessListener {
                showFancyToast("Your profile is updated.", FancyToast.SUCCESS)
                hideProgressBar()
            }
            .addOnFailureListener {
                val message = "An error occurred while updating the profile. Please try again."
                showFancyToast(message, FancyToast.WARNING)
                hideProgressBar()
            }
    }

    private fun controlEmptyField(userProfileData: UserProfileData): ControlResult {

        if (userProfileData.firstName?.isEmpty() == true) {
            return ControlResult("Please enter a valid name.", false)
        }

        if (userProfileData.lastName?.isEmpty() == true) {
            return ControlResult("Please enter a valid surname.", false)
        }

        if (userProfileData.email?.isEmpty() == true || userProfileData.email?.contains("@") == false) {
            return ControlResult("Please enter a valid email.", false)
        }

        if (userProfileData.phone?.isEmpty() == true) {
            return ControlResult("Please enter a valid phone.", false)
        }

        if (userProfileData.address?.address?.isEmpty() == true) {
            return ControlResult("Please enter a valid address.", false)
        }

        if (userProfileData.address?.city?.isEmpty() == true) {
            return ControlResult("Please enter a valid city.", false)
        }

        return ControlResult("", true)
    }

    private fun showFancyToast(message: String, fancyToastType: Int) {
        FancyToast.makeText(
            context,
            message,
            FancyToast.LENGTH_LONG,
            fancyToastType,
            false
        ).show()
    }

    private fun setDataPickerDialog(year: Int?, month: Int?, day: Int?) {
        val datePickerDialog = DatePickerDialog(
            layoutInflater.context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                binding
                    ?.birthDateEditText?.setText(selectedDate)
            },
            year!!,
            month!! - 1,
            day!!
        )

        datePickerDialog.show()
    }

    private fun parseDate(date: String?): Date {
        val parsedDate = date?.split("-")
        val year = parsedDate?.get(0)?.toInt()
        val month = parsedDate?.get(1)?.toInt()
        val day = parsedDate?.get(2)?.toInt()
        return Date(day, month, year)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        return dateFormat.format(currentDate)
    }

    private fun getUserProfileDataFromEditText(): UserProfileData {
        var image: String? = null
        if (MainActivity.isServiceLogin) {
            image = userProfileData?.image.toString()
        } else {
            // Database'den giriş yapan kullanıcıların default olarak bir profil fotosu var.
        }
        val name = binding?.nameEditText?.text.toString()
        val surname = binding?.surnameEditText?.text.toString()
        val email = binding?.emailEditText?.text.toString()
        val phone = binding?.phoneEditText?.text.toString()
        val address = Address(
            binding?.addressEditText?.text.toString(),
            binding?.cityEditText?.text.toString(),
            Coordinates(0.0, 0.0),
            "",
            ""
        )
        val birthDate = binding?.birthDateEditText?.text.toString()
        val gender = if (binding?.maleRadioButton?.isChecked!!) {
            "male"
        } else {
            "female"
        }
        if (image == null) {
            image = ""
        }
        return UserProfileData(image, name, surname, email, phone, address, birthDate, gender)
    }

    private fun showUserDataOnScreen(userProfileData: UserProfileData?) {
        if (userProfileData!!.image?.isNotEmpty() == true) {
            Glide.with(context).load(userProfileData.image).into(binding?.imageImageView!!)
        } else {
            binding?.imageImageView?.setImageResource(R.drawable.ic_profile)
        }
        binding?.nameEditText?.setText(userProfileData.firstName)
        binding?.surnameEditText?.setText(userProfileData.lastName)
        binding?.emailEditText?.setText(userProfileData.email)
        binding?.phoneEditText?.setText(userProfileData.phone)
        binding?.cityEditText?.setText(userProfileData.address?.city)
        binding?.addressEditText?.setText(userProfileData.address?.address)
        binding?.birthDateEditText?.setText(userProfileData.birthDate)
        if (userProfileData.gender == "male") {
            binding?.maleRadioButton?.isChecked = true
        } else {
            binding?.femaleRadioButton?.isChecked = true
        }
    }

    private fun getUserProfileData(userAllData: UserAllData?): UserProfileData {
        val image = userAllData!!.image
        val firstName = userAllData.firstName
        val lastName = userAllData.lastName
        val email = userAllData.email
        val phone = userAllData.phone
        val address = Address(
            userAllData.address.address,
            userAllData.address.city,
            userAllData.address.coordinates,
            userAllData.address.postalCode,
            userAllData.address.state
        )
        val birthDate = userAllData.birthDate
        val gender = userAllData.gender
        return UserProfileData(image, firstName, lastName, email, phone, address, birthDate, gender)
    }

    private fun hideProgressBar() {
        binding?.progressBar?.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding?.progressBar?.visibility = View.VISIBLE
    }

    private fun setCollapsingToolbarAttriutes() {
        binding?.collapsingToolbarLayout?.setExpandedTitleTypeface(
            Typeface.create(
                binding?.collapsingToolbarLayout?.expandedTitleTypeface,
                Typeface.BOLD
            )
        )

        binding?.collapsingToolbarLayout?.setCollapsedTitleTypeface(
            Typeface.create(
                binding?.collapsingToolbarLayout?.expandedTitleTypeface,
                Typeface.BOLD
            )
        )

        binding?.collapsingToolbarLayout?.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.backgroundColor
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

