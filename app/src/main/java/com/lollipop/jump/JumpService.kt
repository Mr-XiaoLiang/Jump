package com.lollipop.jump

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.lollipop.jump.impl.DefaultJumpImpl

/**
 * @author lollipop
 * @date 2021/11/23 20:54
 * 有点搞不定了，决定参考https://github.com/zfdang/Android-Touch-Helper
 * 还有https://github.com/LGH1996/AccessibilityTool
 * 明天加班，什么都不想写
 * 难受，敷衍的更新
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
    override fun onCreate() {
        super.onCreate()
        registerReceiver(configChangedListener, IntentFilter(ACTION_CONFIG_CHANGED))
        checkJumpImpl()
        Toast.makeText(applicationContext, "服务起来啦", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(configChangedListener)
        jumpList.forEach {
            it.destroy()
            it.context = null
        }
        jumpList.clear()
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