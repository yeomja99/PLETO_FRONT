package com.example.myapplication.utils

import java.io.Serializable

class GrowPleeData(
    var pleeName: String? = null,
    var ecoCount: Long? = null
) : Serializable

class PleeStateData(
    var pleeName: String? = null,
    var completeCount: Int? = null
) : Serializable

class PleeDictData(
    var pleeList: Array<String>? = null
) : Serializable

class SendPleeStatus(
    var email: String? = null,
    var ecoName: String? = null,
    var pleeName: String? = null
) : Serializable