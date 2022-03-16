package com.base.utils

fun removeExtraSlashOfUrl(url:String):String {
    if (url.isEmpty()) {
        return url
    }
    return url.replace("(?<!(http:|https:))/+".toRegex(), "/")
}

