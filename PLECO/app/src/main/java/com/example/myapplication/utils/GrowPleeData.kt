package com.example.myapplication.utils

import java.io.Serializable

class GrowPleeData(
    var pleeName: String? = null,
    var ecoCount: Long? = null
) : Serializable

class PleeStateData(
    var pleeName: String? = null,
    var completeCount: Long? = null
) : Serializable

class PleeId(
    var pleeId: Long? = null
) : Serializable

class PleeDictData(
    var pleeList: List<String>? = null
) : Serializable

class SendPleeStatus(
    var email: String? = null,
    var ecoName: String? = null
) : Serializable

class PleeStatus(
    var pleeName: String? = null,
    var pleeStatus: String? = null
) : Serializable