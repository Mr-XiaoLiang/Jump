package com.lollipop.jump.impl

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.lollipop.jump.log
import java.util.*

/**
 * @author lollipop
 * @date 2021/11/23 21:21
 */
class DefaultJumpImpl : BaseJump() {

    override fun isSupport(pkg: String): Boolean {
        // 默认的全都支持
        log("isSupport：$pkg")
        return true
    }

    override fun doJump(event: AccessibilityEvent, source: AccessibilityNodeInfo) {
        doTask {
            clickButton(source.findAccessibilityNodeInfosByText("跳过"))
            jumpBytedance(source)
        }
    }

    private fun clickButton(jumpList: List<AccessibilityNodeInfo>): Boolean {
        jumpList.forEach {
            it.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
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
        val infoArray = LinkedList<AccessibilityNodeInfo>()
        infoArray.addLast(source)
        while (infoArray.isNotEmpty()) {
            val info = infoArray.removeFirst()
            if (info.className?.contains("TTCountdownView") == true) {
                clickButton(listOf(info))
                return true
            }
            val childCount = info.childCount
            for (index in 0 until childCount) {
                infoArray.addLast(info.getChild(index))
            }
        }
//        clickButton(source.findAccessibilityNodeInfosByViewId("tt_splash_skip_btn"))
        return false
    }

}