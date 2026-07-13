package com.example.unitedpoultry

import android.content.SharedPreferences

private const val KEY_USER_INFO = "userInfo"
private const val TOKEN = "token"
class SessionManager(private val sharedPreferences: SharedPreferences) {


    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString(KEY_USER_INFO, null) != null
    }

//    fun logout() {
//        val editor = sharedPreferences.edit()
//        editor.clear()
//        editor.apply()
//    }

    fun logout() {
        val editor = sharedPreferences.edit()
        // Remove only session specific keys instead of wiping the entire sharedPreferences file
        editor.remove(KEY_USER_INFO)
        editor.remove(TOKEN)
        editor.apply()
    }

    fun userInfo(userInfo: String?) {
        sharedPreferences.edit().putString(KEY_USER_INFO, userInfo).apply()
    }

    fun getUserInfo() : String? {
        return sharedPreferences.getString(KEY_USER_INFO, null)
    }

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString(TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }

    // Inside your SessionManager class
    fun isFirstTime(): Boolean {
        // Default to true if the key doesn't exist yet
        return sharedPreferences.getBoolean("KEY_IS_FIRST_TIME", true)
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        sharedPreferences.edit().putBoolean("KEY_IS_FIRST_TIME", isFirstTime).apply()
    }



}