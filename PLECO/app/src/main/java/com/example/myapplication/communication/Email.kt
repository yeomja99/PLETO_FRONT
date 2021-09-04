package com.example.myapplication.communication

import java.io.Serializable

class Email (
    var errorMessage: String? = null,
    var success: Boolean = false
): Serializable