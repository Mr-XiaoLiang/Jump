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

    private var currentPage = ""

    override fun doJump(event: AccessibilityEvent, source: AccessibilityNodeInfo) {
        val lastPage = currentPage
        currentPage = event.className.toString()
//        if (lastPage != currentPage) {
//            doTask {
////                clickButton(source.findAccessibilityNodeInfosByText("跳过"))
//                jumpBytedance(source)
//            }
//        }
        doTask {
            jumpBytedance(source)
        }
    }

    private fun clickButton(jumpList: List<AccessibilityNodeInfo>): Boolean {
        jumpList.forEach {
            click(it)
        }
        val notEmpty = jumpList.isNotEmpty()
        if (notEmpty) {
            showJumpToast()
        }
        return notEmpty
    }

    private fun jumpBytedance(source: AccessibilityNodeInfo): Boolean {
        clickButton(source.findAccessibilityNodeInfosByViewId("tt_splash_skip_btn"))
        return false
    }

}