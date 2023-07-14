package com.kursatmemis.e_ticaret_app.nav_fragments

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.kursatmemis.e_ticaret_app.MainActivity

import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.models.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val profileFragmentLayout = inflater.inflate(R.layout.fragment_profile, container, false)
        bindViews(profileFragmentLayout)
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.create(collapsingToolbarLayout.expandedTitleTypeface, Typeface.BOLD));
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.create(collapsingToolbarLayout.expandedTitleTypeface, Typeface.BOLD))
        getUserInfos(MainActivity.userId)
        return profileFragmentLayout
    }

    private fun getUserInfos(userId: String) {
        MainActivity.dummyService.getUserInfo(userId).enqueue(object :
            Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val body = response.body()
                if (body != null) {
                    setValues(body)
                    Log.w("mKm - getUserInfos", "Body: $body")
                } else {
                    Log.w("mKm - getUserInfos", "Body is null.")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.w("mKm - getUserInfos", "onFailure: $t")
            }

        })
    }

    private fun setValues(userInfo: UserResponse) {
        Glide.with(imageImageView.context).load(userInfo.image).into(imageImageView)
        nameEditText.setText(userInfo.firstName)
        surnameEditText.setText(userInfo.lastName)
        emailEditText.setText(userInfo.email)
        phoneEditText.setText(userInfo.phone)
        cityEditText.setText(userInfo.address.city)
        addressEditText.setText(userInfo.address.address)
        val birthDate = parseBirthDate(userInfo.birthDate) // Kullanıcı bilgilerinden doğum tarihini alın ve işleyin

        // Doğum tarihini uygun formata dönüştürün ve birthDateEditText'e yerleştirin
        val formattedBirthDate = formatBirthDate(birthDate!!)
        birthDateEditText.setText(formattedBirthDate)

        // birthDateEditText üzerine tıklanıldığında doğum tarihini başlatan bir tıklama dinleyicisi ekleme
        birthDateEditText.setOnClickListener {
            showDatePickerDialog(birthDate)
        }

        if (userInfo.gender == "male") {
            maleRadioButton.isChecked = true
        } else {
            femaleRadioButton.isChecked = true
        }
    }

    private fun parseBirthDate(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(dateString)
    }

    private fun formatBirthDate(birthDate: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(birthDate)
    }

    private fun showDatePickerDialog(initialDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = initialDate

        val datePickerDialog = DatePickerDialog(nameEditText.context,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val formattedDate = formatBirthDate(selectedDate.time)
                birthDateEditText.setText(formattedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    private fun bindViews(profileFragmentLayout: View) {
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