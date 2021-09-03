package com.example.myapplication.communication

import java.io.Serializable

class UserToken (
    var email: String? = null,
    var password: String? = null,
    var token: String = ""
): Serializable