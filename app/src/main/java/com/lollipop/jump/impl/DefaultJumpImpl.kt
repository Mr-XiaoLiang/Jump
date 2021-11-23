package com.lollipop.jump.impl

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * @author lollipop
 * @date 2021/11/23 21:21
 */
class DefaultJumpImpl : BaseJump() {

    override fun isSupport(pkg: String): Boolean {
        // 默认的全都支持
        return true
    }

    override fun doJump(event: AccessibilityEvent, source: AccessibilityNodeInfo) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            return
        }
        doTask {
            clickButton(source.findAccessibilityNodeInfosByText("跳过"))
        }
    }

    private fun clickButton(jumpList: List<AccessibilityNodeInfo>): Boolean {
        jumpList.forEach {
            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            // 父容器也点一下
            it.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        val notEmpty = jumpList.isNotEmpty()
        if (notEmpty) {
            showJumpToast()
        }
        return notEmpty
    }

    private fun jumpBytedance(source: AccessibilityNodeInfo): Boolean {
        return false
    }

}