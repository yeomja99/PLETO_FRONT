package com.example.myapplication.communication

import java.io.Serializable

class UserToken (
    var token: String = "",
    var success: Boolean = false,
    var pleeSize: Long = 0,
    var errorMessage: String? = null
): Serializable