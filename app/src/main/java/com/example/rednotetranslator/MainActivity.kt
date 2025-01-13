package com.example.rednotetranslator

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        val overlayEnabled = Settings.canDrawOverlays(this)

        if (!accessibilityEnabled) {
            showAccessibilityDialog()
        }

        if (!overlayEnabled) {
            showOverlayDialog()
        }

        if (accessibilityEnabled && overlayEnabled) {
            showSuccessMessage()
        }
    }

    private fun showAccessibilityDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Accessibility Service")
            .setMessage("RedNote Translator needs accessibility service to detect text. Would you like to enable it now?")
            .setPositiveButton("Enable") { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            .setNegativeButton("Later", null)
            .show()
    }

    private fun showOverlayDialog() {
        AlertDialog.Builder(this)
            .setTitle("Allow Overlay Permission")
            .setMessage("RedNote Translator needs overlay permission to show translations. Would you like to enable it now?")
            .setPositiveButton("Enable") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
            .setNegativeButton("Later", null)
            .show()
    }

    private fun showSuccessMessage() {
        AlertDialog.Builder(this)
            .setTitle("Setup Complete!")
            .setMessage("All permissions granted. You can now open XiaoHongShu app and start using the translator!")
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        ) == 1

        if (accessibilityEnabled) {
            val serviceString = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return serviceString?.contains("${packageName}/${TranslationAccessibilityService::class.java.name}") == true
        }
        return false
    }
} 