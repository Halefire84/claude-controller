package com.cooperrivertrading.claudecontroller

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cooperrivertrading.claudecontroller.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var b: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }

        b.apiKeyInput.setText(Prefs.getApiKey(this))
        b.forgeUrlInput.setText(Prefs.getForgeUrl(this))

        b.btnSave.setOnClickListener {
            val key = b.apiKeyInput.text?.toString()?.trim() ?: ""
            val url = b.forgeUrlInput.text?.toString()?.trim() ?: ""

            if (key.isNotEmpty() && !key.startsWith("sk-ant-")) {
                b.apiKeyLayout.error = "Must start with sk-ant-"
                return@setOnClickListener
            }

            Prefs.saveApiKey(this, key)
            Prefs.saveForgeUrl(this, url.ifBlank { "http://localhost:8780" })
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
