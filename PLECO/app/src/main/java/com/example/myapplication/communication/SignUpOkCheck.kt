package com.example.myapplication.communication

import java.io.Serializable

class SignUpOkCheck (
    var userId: Long? = null,
    var success: Boolean = false
): Serializable