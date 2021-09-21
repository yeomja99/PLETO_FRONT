package com.example.myapplication.model

import android.graphics.RectF
import android.util.Log

class Result(val id: String?, val title: String?, val confidence: Float?, private var location: RectF?) {
    override fun toString(): String {
        var resultString = ""
        if (title != null) resultString += title
        Log.d("분류 결과", title)
        Log.d("분류 확률", confidence.toString())
//        if (confidence != null) resultString += confidence.toString() // 몇 %의 확률인지 출력되는 부분
        return resultString
    }
}