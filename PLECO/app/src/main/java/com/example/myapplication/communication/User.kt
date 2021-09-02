package com.example.myapplication.communication

import java.io.Serializable

class User(
    var username: String? = null,
    var password: String? = null
): Serializable