package com.kursatmemis.e_ticaret_app.nav_fragments

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
import com.kursatmemis.e_ticaret_app.models.Address
import com.kursatmemis.e_ticaret_app.models.UserProfileInfo
import com.kursatmemis.e_ticaret_app.models.UserDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

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
    private var userDetail: UserDetail? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val profileFragmentLayout = inflater.inflate(R.layout.fragment_profile, container, false)
        bindViews(profileFragmentLayout)
        setCollapsingToolbarTitleTypes()
        showProgressBar()

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            userDetail = getUserAllInfo()
            if (userDetail != null) {
                val userProfileInfo = getUserProfileInfo(userDetail!!)
                showUserInfoOnScreen(userProfileInfo)
                hideProgressBar()
            } else {
                Toast.makeText(
                    nameEditText.context,
                    "Unable to fetch data. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        saveButton.setOnClickListener {
            val userProfileInfo =getUserProfileInfoFromEditText()
            showProgressBar()
            updateUserInfo(userProfileInfo)
        }

        return profileFragmentLayout
    }

    private fun getUserProfileInfoFromEditText(): UserProfileInfo {
        val image = userDetail?.image
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val address = Address(
            addressEditText.text.toString(),
            cityEditText.text.toString(),
            userDetail?.address!!.coordinates,
            userDetail?.address!!.postalCode,
            userDetail?.address!!.state
        )
        val birthDate = birthDateEditText.text.toString()
        val gender = if (maleRadioButton.isChecked) {
            "male"
        } else {
            "female"
        }
        return UserProfileInfo(image!!, name, surname, email, phone, address, birthDate, gender)
    }

    private fun showUserInfoOnScreen(userProfileInfo: UserProfileInfo) {
        Glide.with(imageImageView.context).load(userProfileInfo.image).into(imageImageView)
        nameEditText.setText(userProfileInfo.firstName)
        surnameEditText.setText(userProfileInfo.lastName)
        emailEditText.setText(userProfileInfo.email)
        phoneEditText.setText(userProfileInfo.phone)
        cityEditText.setText(userProfileInfo.address.city)
        addressEditText.setText(userProfileInfo.address.address)

        if (userProfileInfo.gender == "male") {
            maleRadioButton.isChecked = true
        } else {
            femaleRadioButton.isChecked = true
        }
    }

    private fun getUserProfileInfo(userResponse: UserDetail): UserProfileInfo {
        val image = userResponse.image
        val firstName = userResponse.firstName
        val lastName = userResponse.lastName
        val email = userResponse.email
        val phone = userResponse.phone
        val address = Address(
            userResponse.address.address,
            userResponse.address.city,
            userResponse.address.coordinates,
            userResponse.address.postalCode,
            userResponse.address.state
        )
        val birthDate = userResponse.birthDate
        val gender = userResponse.gender
        return UserProfileInfo(image, firstName, lastName, email, phone, address, birthDate, gender)
    }

    private fun updateUserInfo(
        userProfileInfo: UserProfileInfo,
        userId: Long = MainActivity.userId
    ) {
        MainActivity.dummyService.updateUserInfo(userId, userProfileInfo)
            .enqueue(object : Callback<UserDetail> {
                override fun onResponse(
                    call: Call<UserDetail>,
                    response: Response<UserDetail>
                ) {
                    val body = response.body()
                    if (body != null) {
                        Log.w("mKm - updateProfile", "Body: $body")
                    } else {
                        Log.w("mKm - updateProfile", "Body is null.")
                    }
                    hideProgressBar()
                }

                override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                    Log.w("mKm - updateProfile", "onFailure: $t")
                    hideProgressBar()
                }
            })
    }

    private suspend fun getUserAllInfo(): UserDetail? = withContext(Dispatchers.IO) {
        try {
            val response = MainActivity.dummyService.getUserInfos().execute()
            if (response.isSuccessful) {
                Log.w("mKm - getUserInfos", response.body().toString())
                response.body()
            } else {
                Log.w("mKm - getUserInfos", "Request failed with code: ${response.code()}")
                null
            }
        } catch (e: IOException) {
            Log.w("mKm - getUserInfos", "Request failed: ${e.message}")
            null
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun setCollapsingToolbarTitleTypes() {
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