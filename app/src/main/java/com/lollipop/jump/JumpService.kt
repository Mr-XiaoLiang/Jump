package com.lollipop.jump

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.lollipop.jump.impl.DefaultJumpImpl

/**
 * @author lollipop
 * @date 2021/11/23 20:54
 */
class JumpService : AccessibilityService() {

    companion object {
        private val IMPL_CLASS: Array<Class<Jump>> = arrayOf(

        )
        private val DEFAULT_IMPL = DefaultJumpImpl::class.java

        private const val ACTION_CONFIG_CHANGED = "com.lollipop.jump.ACTION_CONFIG_CHANGED"

        fun notifyConfigChanged(context: Context) {
            context.sendBroadcast(Intent(ACTION_CONFIG_CHANGED))
        }
    }

    private val jumpList = ArrayList<Jump>()

    private val configChangedListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CONFIG_CHANGED) {
                onConfigChanged()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(configChangedListener, IntentFilter(ACTION_CONFIG_CHANGED))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(configChangedListener)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        checkJumpImpl()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        jumpList.forEach {
            it.destroy()
            it.context = null
        }
        jumpList.clear()
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        checkJumpImpl()
    }

    private fun checkJumpImpl() {
        if (jumpList.isEmpty()) {
            IMPL_CLASS.forEach { clazz ->
                jumpList.add(createJumpImpl(clazz))
            }
            // 默认的在最后一个
            jumpList.add(createJumpImpl(DEFAULT_IMPL))
        }
    }

    private fun createJumpImpl(clazz: Class<out Jump>): Jump {
        val impl = clazz.getConstructor().newInstance()
        impl.context = this
        impl.onCreate()
        return impl
    }

    private fun onConfigChanged() {
        jumpList.forEach {
            it.onConfigChanged()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val source = event.source ?: return
        val packageName = source.packageName?.toString() ?: return
        if (packageName.isEmpty()) {
            return
        }
        jumpList.forEach {
            if (it.isSupport(packageName)) {
                it.doJump(event, source)
            }
        }
    }

    override fun onInterrupt() {
        jumpList.forEach {
            it.onInterrupt()
        }
    }

    abstract class Jump {

        var context: Context? = null

        open fun onCreate() {}

        open fun onConfigChanged() {}

        abstract fun isSupport(pkg: String): Boolean

        abstract fun doJump(
            event: AccessibilityEvent,
            source: AccessibilityNodeInfo
        )

        abstract fun destroy()

        abstract fun onInterrupt()

    }
}