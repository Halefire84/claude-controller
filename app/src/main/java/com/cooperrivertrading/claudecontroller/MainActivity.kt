package com.cooperrivertrading.claudecontroller

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cooperrivertrading.claudecontroller.databinding.ActivityMainBinding
import com.cooperrivertrading.claudecontroller.databinding.JobCardBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        b.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            } else if (item.itemId == R.id.action_history) {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            } else if (item.itemId == R.id.action_dnc) {
                startActivity(Intent(this, DncActivity::class.java))
                true
            } else false
        }

        wireQueue(b.cardQueueOutreach, Queues.OUTREACH)
        wireQueue(b.cardQueueReceptionist, Queues.RECEPTIONIST_PITCH)
        wireQueue(b.cardQueueSiteBuild, Queues.SITE_BUILD)
        wireQueue(b.cardQueueDailyOps, Queues.DAILY_OPS)

        wireJob(b.cardFindProspects, Jobs.FIND_PROSPECTS)
        wireJob(b.cardDraftEmail, Jobs.DRAFT_EMAIL)
        wireJob(b.cardCheckInbox, Jobs.CHECK_INBOX)
        wireJob(b.cardBuildDemo, Jobs.BUILD_DEMO)
        wireJob(b.cardAuditSite, Jobs.AUDIT_SITE)
        wireJob(b.cardRunRunbook, Jobs.RUN_RUNBOOK)
        wireJob(b.cardCustomJob, Jobs.CUSTOM)
        wireJob(b.cardReceptionistBuildProfile, Jobs.RECEPTIONIST_BUILD_PROFILE)
        wireJob(b.cardReceptionistDraftReply, Jobs.RECEPTIONIST_DRAFT_REPLY)
        wireJob(b.cardReceptionistTest, Jobs.RECEPTIONIST_TEST)
        wireJob(b.cardReceptionistPitch, Jobs.RECEPTIONIST_PITCH)
        wireJob(b.cardReceptionistOnboard, Jobs.RECEPTIONIST_ONBOARD)
    }

    private fun wireQueue(card: JobCardBinding, queue: JobQueue) {
        card.jobIcon.text = queue.icon
        card.jobTitle.text = queue.title
        card.jobSubtitle.text = queue.subtitle
        card.root.setOnClickListener {
            if (!Prefs.hasApiKey(this)) {
                Toast.makeText(this, getString(R.string.no_api_key), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SettingsActivity::class.java))
                return@setOnClickListener
            }
            startActivity(Intent(this, RunQueueActivity::class.java).apply {
                putExtra(RunQueueActivity.EXTRA_QUEUE_ID, queue.id)
            })
        }
    }

    private fun wireJob(card: JobCardBinding, job: Job) {
        card.jobIcon.text = job.icon
        card.jobTitle.text = job.title
        card.jobSubtitle.text = job.subtitle
        card.root.setOnClickListener {
            if (!Prefs.hasApiKey(this)) {
                Toast.makeText(this, getString(R.string.no_api_key), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SettingsActivity::class.java))
                return@setOnClickListener
            }
            startActivity(Intent(this, RunJobActivity::class.java).apply {
                putExtra(RunJobActivity.EXTRA_JOB_ID, job.id)
            })
        }
    }
}
