package com.wynndie.sendytestapp.domain.usecase

import com.wynndie.sendytestapp.domain.util.Error
import land.sendy.pfe_sdk.model.types.ApiCallback
import land.sendy.pfe_sdk.model.types.LoaderError

class MakePhoneApiCall {
    operator fun invoke(
        apiCall: (ApiCallback) -> LoaderError?,
        onCallbackError: (Error) -> Unit,
        onCallbackSuccess: () -> Unit
    ) {

        val callback = object : ApiCallback() {
            override fun onCompleted(res: Boolean) {
                if (!res || errNo != 0) {
                    onCallbackError(Error.of(this.errNo))
                    return
                }

                onCallbackSuccess()
            }
        }

        val runResult = apiCall(callback)

        if (runResult != null && runResult.hasError()) {
            onCallbackError(Error.REQUEST_WAS_NOT_SENT)
        }
    }
}