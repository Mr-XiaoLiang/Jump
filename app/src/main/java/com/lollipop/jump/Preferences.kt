package com.lollipop.jump

import android.content.Context
import android.content.SharedPreferences

/**
 * @author lollipop
 * @date 2021/11/23 21:48
 */
object Preferences {

    private const val JUMP_DELAY = "JUMP_DELAY"
    private const val JUMP_TOAST = "JUMP_TOAST"

    const val DEF_JUMP_DELAY = 500L

    private const val DEF_JUMP_TOAST = R.string.jump_toast

    fun get(context: Context): SharedPreferences {
        return context.getSharedPreferences("Lollipop", Context.MODE_PRIVATE)
    }

    fun getJumpDelay(context: Context): Long {
        return get(context).getLong(JUMP_DELAY, DEF_JUMP_DELAY)
    }

    fun setJumpDelay(context: Context, value: Long) {
        get(context).edit().putLong(JUMP_DELAY, value).apply()
    }

    fun getJumpToast(context: Context): String {
        val toast = get(context).getString(JUMP_TOAST, "") ?: ""
        if (toast.isEmpty()) {
            return getDefaultJumpToast(context)
        }
        return toast
    }

    fun setJumpToast(context: Context, value: String) {
        get(context).edit().putString(JUMP_TOAST, value).apply()
    }

    private fun getDefaultJumpToast(context: Context): String {
        return context.getString(DEF_JUMP_TOAST)
    }

}

var Context.jumpDelay: Long
    get() {
        return Preferences.getJumpDelay(this)
    }
    set(value) {
        Preferences.setJumpDelay(this, value)
    }

var Context.jumpToast: String
    get() {
        return Preferences.getJumpToast(this)
    }
    set(value) {
        Preferences.setJumpToast(this, value)
    }