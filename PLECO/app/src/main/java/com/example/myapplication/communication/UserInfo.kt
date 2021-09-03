package com.example.myapplication.communication

import java.io.Serializable

class UserInfo(
    var email: String? = null,
    var password: String? = null
): Serializable