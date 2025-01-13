package com.example.xiaohongshutranslator

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TranslationAccessibilityService : AccessibilityService() {
    
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 只处理小红书应用的事件
        if (event.packageName != "com.xingin.xhs") return
        
        // 获取根节点
        val rootNode = rootInActiveWindow ?: return
        
        // 递归查找文本节点
        findAndProcessTextNodes(rootNode)
    }

    private fun findAndProcessTextNodes(node: AccessibilityNodeInfo) {
        // 如果节点包含文本，处理该文本
        if (node.text != null) {
            val text = node.text.toString()
            if (text.isNotEmpty() && isChineseText(text)) {
                // TODO: 实现翻译逻辑
                // TODO: 显示悬浮窗
            }
        }

        // 递归处理子节点
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findAndProcessTextNodes(child)
                child.recycle()
            }
        }
    }

    private fun isChineseText(text: String): Boolean {
        return text.any { it.toInt() in 0x4E00..0x9FA5 }
    }

    override fun onInterrupt() {
        // 服务中断时的处理
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // 服务连接时的初始化
    }
} 