package com.kursatmemis.e_ticaret_app.managers

import android.content.Context


object SharedPrefManager {

    // Veriyi kaydeder.
    fun setSharedPreference(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val edit  = sharedPref.edit()
        edit.putString(key, value)
        edit.apply()
    }

    // Daha önce kaydedilmiş veriyi getirir.
    fun getSharedPreference(context: Context, key: String?, defaultValue: String?): String? {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(key, defaultValue)
    }

    // Kaydedilen tüm verileri temizler.
    fun clearSharedPreference(context: Context) {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.clear()
        edit.apply()
    }

    // Kaydedilen özel bir veriyi temizler.
    fun removeSharedPreference(context: Context, key: String?) {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.remove(key)
        edit.apply()
    }
}