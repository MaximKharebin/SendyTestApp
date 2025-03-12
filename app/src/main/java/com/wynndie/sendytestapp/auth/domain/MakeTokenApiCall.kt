package com.wynndie.sendytestapp.auth.domain

import com.wynndie.sendytestapp.core.domain.Error
import land.sendy.pfe_sdk.model.pfe.response.BResponse
import land.sendy.pfe_sdk.model.types.ApiCallback
import land.sendy.pfe_sdk.model.types.LoaderError

class MakeTokenApiCall {
    operator fun invoke(
        apiCall: (ApiCallback) -> LoaderError?,
        onCallbackError: (Error) -> Unit,
        onCallbackSuccess: () -> Unit
    ) {

        val callback = object : ApiCallback() {
            override fun <T : BResponse?> onSuccess(data: T?) {
                if (data == null) {
                    onCallbackError(Error.EMPTY_RESPONSE)
                    return
                }

                if (this.errNo != 0) {
                    onCallbackError(Error.Companion.of(this.errNo))
                    return
                }

                onCallbackSuccess()
            }

            override fun onFail(error: LoaderError?) {
                error?.let {
                    onCallbackError(Error.Companion.of(it.errNo))
                } ?: onCallbackError(Error.UNKNOWN)
            }
        }

        val runResult = apiCall(callback)

        if (runResult != null && runResult.hasError()) {
            onCallbackError(Error.REQUEST_WAS_NOT_SENT)
        }
    }
}