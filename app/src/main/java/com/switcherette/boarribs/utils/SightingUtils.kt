package com.switcherette.boarribs.utils

import java.util.*

class TimeHelper {
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}

class IdHelper {
    fun randomId(): String = UUID.randomUUID().toString()
}