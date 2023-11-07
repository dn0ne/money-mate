package com.dn0ne.moneymate.app.extensions

fun Float.toStringWithScale(scale: Int): String {
    var result = this.toString()

    var scaleDifference = result.takeLastWhile { it != '.' }.length - scale
    val smallerScaleCase = scaleDifference < 0
    val greaterScaleCase = scaleDifference > 0
    val noPointCase = result.dropLastWhile { it != '.' }.isEmpty()

    when {
        noPointCase -> {
            result += ".00"
        }
        smallerScaleCase -> {
            while (scaleDifference < 0) {
                result += "0"
                scaleDifference++
            }
        }
        greaterScaleCase -> {
            result = result.dropLast(scaleDifference)
        }
    }

    return result
}