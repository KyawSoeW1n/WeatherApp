package com.kyawsoewin.weatherapp.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager


object CommonUtils {
    fun newInstance(context: Context) {
        val inputMethodManager =context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}