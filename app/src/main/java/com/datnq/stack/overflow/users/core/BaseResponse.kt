package com.datnq.stack.overflow.users.core

import com.datnq.stack.overflow.users.core.service.Error
import com.google.gson.annotations.SerializedName

class BaseResponse {

    @SerializedName("status")
    var status: String? = null
    @SerializedName("data")
    var data: Any? = null
    @SerializedName("error")
    var error: Error? = null
}