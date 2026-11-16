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
            } else false
        }

        wireJob(JobCardBinding.bind(b.cardFindProspects), Jobs.FIND_PROSPECTS)
        wireJob(JobCardBinding.bind(b.cardDraftEmail), Jobs.DRAFT_EMAIL)
        wireJob(JobCardBinding.bind(b.cardCheckInbox), Jobs.CHECK_INBOX)
        wireJob(JobCardBinding.bind(b.cardBuildDemo), Jobs.BUILD_DEMO)
        wireJob(JobCardBinding.bind(b.cardAuditSite), Jobs.AUDIT_SITE)
        wireJob(JobCardBinding.bind(b.cardRunRunbook), Jobs.RUN_RUNBOOK)
        wireJob(JobCardBinding.bind(b.cardCustomJob), Jobs.CUSTOM)
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
