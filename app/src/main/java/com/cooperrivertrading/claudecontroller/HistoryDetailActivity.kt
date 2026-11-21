package com.cooperrivertrading.claudecontroller

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cooperrivertrading.claudecontroller.databinding.ActivityHistoryDetailBinding

class HistoryDetailActivity : AppCompatActivity() {

    private val saver = SaveToFile(this)

    companion object { const val EXTRA_ID = "history_id" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }

        val store = HistoryStore(SqliteHistoryStorage(this))
        val e = store.get(intent.getLongExtra(EXTRA_ID, -1)) ?: run { finish(); return }
        b.toolbar.title = HistoryActivity.jobTitle(e.jobType)
        b.toolbar.subtitle = HistoryActivity.formatTime(e.timestampMillis)
        b.detailText.text = (if (e.param.isNotEmpty()) "Input: ${e.param}\n\n" else "") + e.output

        b.btnDetailCopy.setOnClickListener {
            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("job_output", e.output))
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
        b.btnDetailSave.setOnClickListener { saver.save(HistoryActivity.jobTitle(e.jobType), e.output) }
        b.btnDetailShare.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, e.output)
                putExtra(Intent.EXTRA_SUBJECT, HistoryActivity.jobTitle(e.jobType))
            }
            startActivity(Intent.createChooser(i, "Share output"))
        }
        b.btnDetailDelete.setOnClickListener {
            store.delete(e.id)
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
