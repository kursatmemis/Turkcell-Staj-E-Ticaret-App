package com.kursatmemis.e_ticaret_app.nav_fragments

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.configs.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.Address
import com.kursatmemis.e_ticaret_app.models.ControlResult
import com.kursatmemis.e_ticaret_app.models.Date
import com.kursatmemis.e_ticaret_app.models.UserAllData
import com.kursatmemis.e_ticaret_app.models.UserProfileData
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var saveButton: Button
    private lateinit var imageImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var maleRadioButton: RadioButton
    private lateinit var femaleRadioButton: RadioButton
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var toolbar: Toolbar
    private var userAllData: UserAllData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val profileFragmentLayout = inflater.inflate(R.layout.fragment_profile, container, false)
        bindViews(profileFragmentLayout)
        setCollapsingToolbarTitleType()
        showProgressBar()

        var userProfileData: UserProfileData

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            userAllData = RetrofitManager.getUserAllData()
            if (userAllData != null) {
                userProfileData = getUserProfileData()
                showUserDataOnScreen(userProfileData)
                hideProgressBar()
            } else {
                val message = "Unable to fetch data. Please try again later."
                val fancyToastType = FancyToast.ERROR
                showFancyToast(message, fancyToastType)
            }
        }

        birthDateEditText.setOnClickListener {
            val date = birthDateEditText.text.toString()
            val parsedDate = parseDate(date)
            val year = parsedDate.year
            val month = parsedDate.month
            val day = parsedDate.day
            setDataPickerDialog(year, month, day)
        }

        saveButton.setOnClickListener {
            var message: String
            var fancyToastType: Int
            userProfileData = getUserProfileDataFromEditText()
            val controlResult = control(userProfileData)
            if (controlResult.result) {
                showProgressBar()
                scope.launch {
                    val isUpdated = RetrofitManager.updateUserProfile(userProfileData)
                    if (isUpdated) {
                        message = "Your profile is updated."
                        fancyToastType = FancyToast.SUCCESS
                        showFancyToast(message, fancyToastType)
                    } else {
                        message = "An error occurred while updating the profile. Please try again."
                        fancyToastType = FancyToast.WARNING
                        showFancyToast(message, fancyToastType)
                    }
                    hideProgressBar()
                }
            } else {
                message = controlResult.message
                fancyToastType = FancyToast.WARNING
                showFancyToast(message, fancyToastType)
            }

        }

        return profileFragmentLayout
    }

    private fun control(userProfileData: UserProfileData): ControlResult {

        if (userProfileData.firstName.isEmpty()) {
            return ControlResult("Please enter a valid name.", false)
        }

        if (userProfileData.lastName.isEmpty()) {
            return ControlResult("Please enter a valid surname.", false)
        }

        if (userProfileData.email.isEmpty() || !userProfileData.email.contains("@")) {
            return ControlResult("Please enter a valid email.", false)
        }

        if (userProfileData.phone.isEmpty()) {
            return ControlResult("Please enter a valid phone.", false)
        }

        if (userProfileData.address.address.isEmpty()) {
            return ControlResult("Please enter a valid address.", false)
        }

        if (userProfileData.address.city.isEmpty()) {
            return ControlResult("Please enter a valid city.", false)
        }

        return ControlResult("", true)
    }

    private fun showFancyToast(message: String, fancyToastType: Int) {
        FancyToast.makeText(
            nameEditText.context,
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
                val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
                birthDateEditText.setText(selectedDate)
            },
            year!!,
            month!!,
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

    private fun getUserProfileDataFromEditText(): UserProfileData {
        val image = userAllData?.image
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val address = Address(
            addressEditText.text.toString(),
            cityEditText.text.toString(),
            userAllData?.address!!.coordinates,
            userAllData?.address!!.postalCode,
            userAllData?.address!!.state
        )
        val birthDate = birthDateEditText.text.toString()
        val gender = if (maleRadioButton.isChecked) {
            "male"
        } else {
            "female"
        }
        return UserProfileData(image!!, name, surname, email, phone, address, birthDate, gender)
    }

    private fun showUserDataOnScreen(userProfileData: UserProfileData) {
        Glide.with(imageImageView.context).load(userProfileData.image).into(imageImageView)
        nameEditText.setText(userProfileData.firstName)
        surnameEditText.setText(userProfileData.lastName)
        emailEditText.setText(userProfileData.email)
        phoneEditText.setText(userProfileData.phone)
        cityEditText.setText(userProfileData.address.city)
        addressEditText.setText(userProfileData.address.address)
        birthDateEditText.setText(userProfileData.birthDate)
        if (userProfileData.gender == "male") {
            maleRadioButton.isChecked = true
        } else {
            femaleRadioButton.isChecked = true
        }
    }

    private fun getUserProfileData(): UserProfileData {
        val image = userAllData!!.image
        val firstName = userAllData!!.firstName
        val lastName = userAllData!!.lastName
        val email = userAllData!!.email
        val phone = userAllData!!.phone
        val address = Address(
            userAllData!!.address.address,
            userAllData!!.address.city,
            userAllData!!.address.coordinates,
            userAllData!!.address.postalCode,
            userAllData!!.address.state
        )
        val birthDate = userAllData!!.birthDate
        val gender = userAllData!!.gender
        return UserProfileData(image, firstName, lastName, email, phone, address, birthDate, gender)
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun setCollapsingToolbarTitleType() {
        collapsingToolbarLayout.setExpandedTitleTypeface(
            Typeface.create(
                collapsingToolbarLayout.expandedTitleTypeface,
                Typeface.BOLD
            )
        )

        collapsingToolbarLayout.setCollapsedTitleTypeface(
            Typeface.create(
                collapsingToolbarLayout.expandedTitleTypeface,
                Typeface.BOLD
            )
        )
    }

    private fun bindViews(profileFragmentLayout: View) {
        progressBar = profileFragmentLayout.findViewById(R.id.progressBar)
        saveButton = profileFragmentLayout.findViewById(R.id.saveButton)
        imageImageView = profileFragmentLayout.findViewById(R.id.imageImageView)
        nameEditText = profileFragmentLayout.findViewById(R.id.nameEditText)
        surnameEditText = profileFragmentLayout.findViewById(R.id.surnameEditText)
        emailEditText = profileFragmentLayout.findViewById(R.id.emailEditText)
        phoneEditText = profileFragmentLayout.findViewById(R.id.phoneEditText)
        cityEditText = profileFragmentLayout.findViewById(R.id.cityEditText)
        addressEditText = profileFragmentLayout.findViewById(R.id.addressEditText)
        birthDateEditText = profileFragmentLayout.findViewById(R.id.birthDateEditText)
        maleRadioButton = profileFragmentLayout.findViewById(R.id.maleRadioButton)
        femaleRadioButton = profileFragmentLayout.findViewById(R.id.femaleRadioButton)
        collapsingToolbarLayout = profileFragmentLayout.findViewById(R.id.collapsingToolbarLayout)
        toolbar = profileFragmentLayout.findViewById(R.id.toolbar)
    }

}

