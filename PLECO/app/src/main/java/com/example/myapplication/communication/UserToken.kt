package com.example.myapplication.communication

import java.io.Serializable

class UserToken (
    var token: String? = null,
    var success: Boolean = false,
    var pleeSize: Long? = null,
    var message: String? = null
): Serializable