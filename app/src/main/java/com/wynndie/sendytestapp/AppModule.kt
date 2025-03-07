package com.wynndie.sendytestapp

import android.content.Context
import com.wynndie.sendytestapp.domain.usecase.MakePhoneApiCall
import com.wynndie.sendytestapp.domain.usecase.MakeTokenApiCall
import com.wynndie.sendytestapp.domain.usecase.ValidatePhone
import com.wynndie.sendytestapp.domain.usecase.ValidateToken

class AppModule(
    private val appContext: Context
) {

    val validatePhone: ValidatePhone by lazy {
        ValidatePhone()
    }

    val makePhoneApiCall: MakePhoneApiCall by lazy {
        MakePhoneApiCall()
    }

    val validateToken: ValidateToken by lazy {
        ValidateToken()
    }

    val makeTokenApiCall: MakeTokenApiCall by lazy {
        MakeTokenApiCall()
    }
}