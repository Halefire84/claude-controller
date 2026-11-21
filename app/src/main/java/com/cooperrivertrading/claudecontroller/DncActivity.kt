package com.cooperrivertrading.claudecontroller

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cooperrivertrading.claudecontroller.databinding.ActivityDncBinding

class DncActivity : AppCompatActivity() {
    private lateinit var b: ActivityDncBinding
    private var names = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDncBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }
        names = DncPrefs.load(this)
        b.btnDncAdd.setOnClickListener {
            update(DoNotContact.add(names, b.dncInput.text.toString()))
            b.dncInput.setText("")
        }
        b.dncList.setOnItemClickListener { _, _, pos, _ ->
            val n = names[pos]
            update(DoNotContact.remove(names, n))
            Toast.makeText(this, "Removed $n", Toast.LENGTH_SHORT).show()
        }
        refresh()
    }

    private fun update(list: List<String>) { names = list; DncPrefs.save(this, list); refresh() }

    private fun refresh() {
        b.dncList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
    }
}
