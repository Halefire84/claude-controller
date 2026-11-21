package com.cooperrivertrading.claudecontroller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cooperrivertrading.claudecontroller.databinding.ActivityHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var b: ActivityHistoryBinding
    private var entries: List<HistoryEntry> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }
        b.historyList.setOnItemClickListener { _, _, pos, _ ->
            startActivity(Intent(this, HistoryDetailActivity::class.java)
                .putExtra(HistoryDetailActivity.EXTRA_ID, entries[pos].id))
        }
    }

    override fun onResume() {
        super.onResume()
        entries = HistoryStore(SqliteHistoryStorage(this)).list()
        b.emptyText.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
        b.historyList.adapter = object : BaseAdapter() {
            override fun getCount() = entries.size
            override fun getItem(position: Int) = entries[position]
            override fun getItemId(position: Int) = entries[position].id
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val tv = (convertView as? TextView) ?: TextView(this@HistoryActivity).apply {
                    setPadding(48, 32, 48, 32)
                    setTextColor(getColor(R.color.ink))
                    textSize = 14f
                }
                val e = entries[position]
                tv.text = "${formatTime(e.timestampMillis)}  ${jobTitle(e.jobType)}" +
                    (if (e.param.isNotEmpty()) " — ${e.param}" else "") +
                    "\n" + HistoryStore.preview(e.output)
                tv.contentDescription = "History entry: " + tv.text.toString().replace("\n", ". ")
                return tv
            }
        }
    }

    companion object {
        fun formatTime(ms: Long): String =
            SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US).format(Date(ms))

        fun jobTitle(type: String): String =
            Jobs.ALL.find { it.id == type }?.title ?: Queues.ALL.find { it.id == type }?.title ?: type
    }
}
