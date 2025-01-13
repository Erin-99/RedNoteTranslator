package com.example.rednotetranslator

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.example.rednotetranslator.overlay.OverlayManager
import com.example.rednotetranslator.translation.TranslationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TranslationAccessibilityService : AccessibilityService() {
    private lateinit var translationManager: TranslationManager
    private lateinit var overlayManager: OverlayManager
    private val serviceScope = CoroutineScope(Job() + Dispatchers.Main)
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        translationManager = TranslationManager(this)
        overlayManager = OverlayManager(this)
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName != "com.xingin.xhs") return
        val rootNode = rootInActiveWindow ?: return
        findAndProcessTextNodes(rootNode)
    }

    private fun findAndProcessTextNodes(node: AccessibilityNodeInfo) {
        if (node.text != null) {
            val text = node.text.toString()
            if (text.isNotEmpty() && isChineseText(text)) {
                translateText(text)
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findAndProcessTextNodes(child)
                child.recycle()
            }
        }
    }

    private fun translateText(text: String) {
        serviceScope.launch {
            try {
                val translation = translationManager.translate(text)
                overlayManager.showTranslation(text, translation)
            } catch (e: Exception) {
                Toast.makeText(this@TranslationAccessibilityService, "Translation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isChineseText(text: String): Boolean {
        return text.any { it.toInt() in 0x4E00..0x9FA5 }
    }

    override fun onInterrupt() {
        overlayManager.hideOverlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.hideOverlay()
    }
} 