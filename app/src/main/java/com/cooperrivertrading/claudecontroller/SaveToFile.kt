package com.cooperrivertrading.claudecontroller

import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/** Storage Access Framework save (ACTION_CREATE_DOCUMENT). Call register() before onStart. */
class SaveToFile(private val activity: ComponentActivity) {
    private var pending: String = ""

    private val launcher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult
            val ok = try {
                activity.contentResolver.openOutputStream(uri)?.use { it.write(pending.toByteArray(Charsets.UTF_8)) } != null
            } catch (e: Exception) { false }
            Toast.makeText(activity, if (ok) "Saved" else "Could not save file", Toast.LENGTH_SHORT).show()
        }

    fun save(title: String, text: String) {
        pending = text
        launcher.launch(ExportName.fileName(title, System.currentTimeMillis()))
    }
}
