package com.example.gaddarquiz.utils

import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager

object AccessibilityManager {
    
    // Announce text for TalkBack
    fun announce(context: Context, text: String) {
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (manager.isEnabled) {
            val event = if (android.os.Build.VERSION.SDK_INT >= 30) {
                AccessibilityEvent()
            } else {
                @Suppress("DEPRECATION")
                AccessibilityEvent.obtain()
            }
            event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
            event.text.add(text)
            manager.sendAccessibilityEvent(event)
        }
    }

    // Check if TalkBack is enabled
    fun isAccessibilityEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return manager.isEnabled && manager.isTouchExplorationEnabled
    }
}
