package com.example.unitedpoultry.util

import android.app.Dialog
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel

object AppConstants {


    var URL = "http://202.166.170.246/united-poultry/"
    var ImageURL = URL

    var BASE_URL = URL + "api/"
    var ASSET_BASE_URL = URL + "/"


    var AUTH_TOKEN = ""
    var Bearer = "Bearer"
    const val SHARED_PREF_NAME = "publication_shared_prefs"

    var progressLoader: Dialog? = null

    var userData: LoginResponseModel? = null
    var TYPE: String? =""

}