package com.cooperrivertrading.claudecontroller

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cooperrivertrading.claudecontroller.databinding.ActivityRunJobBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RunJobActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_JOB_ID = "job_id"
    }

    private lateinit var b: ActivityRunJobBinding
    private var jobDef: com.cooperrivertrading.claudecontroller.Job? = null
    private var runJob: Job? = null
    private val output = StringBuilder()
    private var lastParam: String? = null
    private var saved = false
    private val saver = SaveToFile(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRunJobBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra(EXTRA_JOB_ID) ?: run { finish(); return }
        jobDef = Jobs.ALL.find { it.id == id } ?: run { finish(); return }
        val job = jobDef!!

        b.toolbar.title = job.title

        if (job.paramLabel != null) {
            b.paramsLayout.visibility = View.VISIBLE
            b.paramsLabel.text = job.paramLabel
            b.paramInput.hint = job.paramHint
        }

        b.btnRun.setOnClickListener { startRun() }
        b.btnCopy.setOnClickListener { copyOutput() }
        b.btnShare.setOnClickListener { shareOutput() }
        b.btnSave.setOnClickListener { saver.save(jobDef?.title ?: "output", output.toString()) }
        b.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun startRun(skipDncCheck: Boolean = false) {
        val job = jobDef ?: return
        val param = b.paramInput.text?.toString()?.trim()

        if (!skipDncCheck && job.id in DoNotContact.CHECKED_JOBS) {
            val hit = DoNotContact.match(param, DncPrefs.load(this))
            if (hit != null) {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Do not contact")
                    .setMessage("\"${hit.trim()}\" is on your do-not-contact list. Draft anyway?")
                    .setPositiveButton("Draft anyway") { _, _ -> startRun(skipDncCheck = true) }
                    .setNegativeButton("Cancel", null)
                    .show()
                return
            }
        }

        if (job.paramLabel != null && param.isNullOrBlank() && job.id != "check_inbox") {
            b.paramInput.error = "Required"
            return
        }

        output.clear()
        lastParam = param
        saved = false
        b.outputText.text = ""
        setStatus(Status.RUNNING)

        val apiKey = Prefs.getApiKey(this)
        val userPrompt = job.buildPrompt(param)

        runJob = lifecycleScope.launch {
            ClaudeApi.stream(apiKey, job.systemPrompt, userPrompt)
                .catch { e -> appendOutput("\n[Error] ${e.message}") }
                .collect { token ->
                    appendOutput(token)
                }
            setStatus(Status.DONE)
        }
    }

    private fun appendOutput(text: String) {
        output.append(text)
        b.outputText.text = output.toString()
        b.outputScroll.post { b.outputScroll.fullScroll(View.FOCUS_DOWN) }
    }

    private fun copyOutput() {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("job_output", output.toString()))
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun shareOutput() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, output.toString())
            putExtra(Intent.EXTRA_SUBJECT, jobDef?.title ?: "Job Output")
        }
        startActivity(Intent.createChooser(intent, "Share output"))
    }

    private enum class Status { RUNNING, DONE, STOPPED }

    private fun setStatus(s: Status) {
        when (s) {
            Status.RUNNING -> {
                b.statusText.text = getString(R.string.running)
                b.statusDot.setBackgroundResource(R.drawable.dot_running)
                b.btnRun.text = getString(R.string.stop)
                b.btnRun.setOnClickListener { stopRun() }
                b.btnCopy.visibility = View.GONE
                b.btnSave.visibility = View.GONE
                b.btnShare.visibility = View.GONE
                b.paramsLayout.visibility = View.GONE
            }
            Status.DONE -> {
                saveHistory()
                b.statusText.text = "Done — ${output.length} chars"
                b.statusDot.setBackgroundResource(R.drawable.dot_done)
                b.btnRun.text = getString(R.string.run)
                b.btnRun.setOnClickListener { startRun() }
                b.btnCopy.visibility = View.VISIBLE
                b.btnSave.visibility = View.VISIBLE
                b.btnShare.visibility = View.VISIBLE
            }
            Status.STOPPED -> {
                saveHistory()
                b.statusText.text = getString(R.string.stopped)
                b.statusDot.setBackgroundResource(R.drawable.dot_stopped)
                b.btnRun.text = getString(R.string.run)
                b.btnRun.setOnClickListener { startRun() }
                val hasOutput = output.isNotEmpty()
                b.btnCopy.visibility = if (hasOutput) View.VISIBLE else View.GONE
                b.btnShare.visibility = if (hasOutput) View.VISIBLE else View.GONE
                b.btnSave.visibility = if (hasOutput) View.VISIBLE else View.GONE
            }
        }
    }

    private fun saveHistory() {
        val job = jobDef ?: return
        if (saved) return
        saved = true
        HistoryStore(SqliteHistoryStorage(this)).record(System.currentTimeMillis(), job.id, lastParam, output.toString())
    }

    private fun stopRun() {
        runJob?.cancel()
        setStatus(Status.STOPPED)
    }

    override fun onDestroy() {
        runJob?.cancel()
        super.onDestroy()
    }
}
