package com.wynndie.sendytestapp.di

import com.wynndie.sendytestapp.auth.domain.FormatPhone
import com.wynndie.sendytestapp.auth.domain.FormatToken
import com.wynndie.sendytestapp.auth.domain.MakePhoneApiCall
import com.wynndie.sendytestapp.auth.domain.MakeTermsApiCall
import com.wynndie.sendytestapp.auth.domain.MakeTokenApiCall
import com.wynndie.sendytestapp.auth.domain.ValidatePhone
import com.wynndie.sendytestapp.auth.domain.ValidateToken

class AppModule {

    val validatePhone: ValidatePhone by lazy {
        ValidatePhone()
    }

    val makePhoneApiCall: MakePhoneApiCall by lazy {
        MakePhoneApiCall()
    }

    val makeTermsApiCall: MakeTermsApiCall by lazy {
        MakeTermsApiCall()
    }

    val formatPhone: FormatPhone by lazy {
        FormatPhone()
    }


    val validateToken: ValidateToken by lazy {
        ValidateToken()
    }

    val makeTokenApiCall: MakeTokenApiCall by lazy {
        MakeTokenApiCall()
    }

    val formatToken: FormatToken by lazy {
        FormatToken()
    }
}