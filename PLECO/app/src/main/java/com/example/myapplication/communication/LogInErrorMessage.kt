package com.example.myapplication.communication

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class LogInErrorMessage {
    @SerializedName("message")
    @Expose
    private var message: String? = null

    fun getErrorMessage(): String? {
        return message
    }

    fun setErrorMessage(errorMessage: String?) {
        this.message = errorMessage
    }
}