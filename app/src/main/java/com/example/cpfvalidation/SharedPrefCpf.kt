package com.example.cpfvalidation

import android.app.Application
import android.content.Context
import java.lang.IllegalArgumentException

class SharedPrefCpf : Application() {

    private val sharedPref = AppUtil.appContext?.getSharedPreferences("DOCS", Context.MODE_PRIVATE)
        ?: throw IllegalArgumentException("Shared Preferences Error!")

    fun saveNumberCpf(id: String, string: String) {
        sharedPref.edit()?.putString(id, string)?.apply()
    }

    fun readNumberCpf(id: String): String {
        return sharedPref.getString(id, "") ?: ""
    }

    companion object {
        val instance: SharedPrefCpf by lazy { SharedPrefCpf() }
    }
}
