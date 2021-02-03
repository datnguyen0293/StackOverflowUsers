package com.datnq.stack.overflow.users.core.service

import com.datnq.stack.overflow.users.core.Constants
import com.google.gson.annotations.SerializedName

class Error {
    @SerializedName("code")
    var code: String? = Constants.EMPTY_STRING
    @SerializedName("message")
    var message: String? = Constants.EMPTY_STRING
}