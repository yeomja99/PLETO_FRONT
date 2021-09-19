package com.example.myapplication.model

import android.graphics.RectF
import android.util.Log

class Result(val id: String?, val title: String?, val confidence: Float?, private var location: RectF?) {
    override fun toString(): String {
        var resultString = ""

        if (title != null && (confidence!!*100).toInt() >= 85 && (title.equals("에코백") ||  title.equals("텀블러"))){
            // 분류 결과가 null이 아니고, 에코백/텀블러 중 하나이며, 확률이 85%가 넘는 경우(threshold)
                resultString += title
        }
        else{
            resultString+="미션 실패"
        }

        Log.d("분류 결과", title)
        Log.d("분류 확률", (confidence!!*100).toInt().toString()+"%")

//        if (confidence != null) resultString += confidence.toString() // 몇 %의 확률인지 출력되는 부분
        return resultString
    }
}