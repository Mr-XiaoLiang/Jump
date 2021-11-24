package com.lollipop.jump.impl

import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.lollipop.jump.*
import java.util.*

/**
 * @author lollipop
 * @date 2021/11/23 22:16
 */
abstract class BaseJump : JumpService.Jump() {

    private val delayTaskList = LinkedList<CommonUtil.Task<BaseJump>>()

    var taskDelay = Preferences.DEF_JUMP_DELAY
        private set

    var jumpToast: String = ""
        private set

    override fun onCreate() {
        super.onCreate()
        updateConfig()
    }

    override fun onConfigChanged() {
        super.onConfigChanged()
        updateConfig()
    }

    private fun updateConfig() {
        taskDelay = context?.jumpDelay ?: Preferences.DEF_JUMP_DELAY
        jumpToast = context?.jumpToast ?: ""
    }

    override fun destroy() {
        cleanTask()
    }

    override fun onInterrupt() {
        cleanTask()
    }

    private fun cleanTask() {
        delayTaskList.forEach {
            it.cancel()
        }
        delayTaskList.clear()
    }

    protected fun doTask(delay: Long = taskDelay, callback: () -> Unit) {
        delayTaskList.add(
            delay(delay) { t ->
                callback()
                delayTaskList.remove(t)
            }
        )
    }

    protected fun showJumpToast() {
        val toastValue = jumpToast
        if (toastValue.isEmpty()) {
            return
        }
        context?.let {
            Toast.makeText(it, jumpToast, Toast.LENGTH_SHORT).show()
        }
    }

}