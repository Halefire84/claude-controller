package com.cooperrivertrading.claudecontroller

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cooperrivertrading.claudecontroller.databinding.ActivityRunQueueBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RunQueueActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QUEUE_ID = "queue_id"
    }

    private lateinit var b: ActivityRunQueueBinding
    private var queueDef: JobQueue? = null
    private var runJob: Job? = null
    private val fullOutput = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRunQueueBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra(EXTRA_QUEUE_ID) ?: run { finish(); return }
        queueDef = Queues.ALL.find { it.id == id } ?: run { finish(); return }
        val queue = queueDef!!

        b.toolbar.title = queue.title
        b.queueSubtitle.text = queue.steps.size.toString() + " steps"

        b.btnRunQueue.setOnClickListener { startQueue() }
        b.btnCopyQueue.setOnClickListener { copyOutput() }
        b.btnShareQueue.setOnClickListener { shareOutput() }
        b.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun startQueue() {
        val queue = queueDef ?: return
        val apiKey = Prefs.getApiKey(this)

        fullOutput.clear()
        b.outputText.text = ""
        b.btnCopyQueue.visibility = View.GONE
        b.btnShareQueue.visibility = View.GONE
        b.btnRunQueue.isEnabled = false

        runJob = lifecycleScope.launch {
            var previousOutput = ""

            queue.steps.forEachIndexed { index, step ->
                val jobDef = Jobs.ALL.find { it.id == step.jobId } ?: return@forEachIndexed
                val stepNum = index + 1
                val total = queue.steps.size

                appendOutput("\n━━━ Step $stepNum/$total: ${jobDef.title} ━━━\n\n")
                b.statusText.text = "Running step $stepNum/$total…"

                val param = step.paramOverride
                    ?: if (previousOutput.isNotBlank()) previousOutput.takeLast(600) else null

                val userPrompt = jobDef.buildPrompt(param)

                val systemWithContext = if (previousOutput.isNotBlank() && step.paramOverride == null)
                    jobDef.systemPrompt + "\n\nContext from previous step:\n" + previousOutput.takeLast(800)
                else
                    jobDef.systemPrompt

                val stepOutput = StringBuilder()
                ClaudeApi.stream(apiKey, systemWithContext, userPrompt)
                    .catch { e -> appendOutput("\n[Error] ${e.message}\n") }
                    .collect { token ->
                        stepOutput.append(token)
                        appendOutput(token)
                    }

                previousOutput = stepOutput.toString()
                appendOutput("\n")
            }

            b.statusText.text = "Done — ${queue.steps.size} steps complete"
            b.statusDot.setBackgroundResource(R.drawable.dot_done)
            b.btnRunQueue.isEnabled = true
            b.btnCopyQueue.visibility = View.VISIBLE
            b.btnShareQueue.visibility = View.VISIBLE
        }
    }

    private fun appendOutput(text: String) {
        fullOutput.append(text)
        b.outputText.text = fullOutput.toString()
        b.outputScroll.post { b.outputScroll.fullScroll(View.FOCUS_DOWN) }
    }

    private fun copyOutput() {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("queue_output", fullOutput.toString()))
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun shareOutput() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, fullOutput.toString())
            putExtra(Intent.EXTRA_SUBJECT, queueDef?.title ?: "Queue Output")
        }
        startActivity(Intent.createChooser(intent, "Share output"))
    }

    override fun onDestroy() {
        runJob?.cancel()
        super.onDestroy()
    }
}
