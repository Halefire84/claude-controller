package com.cooperrivertrading.claudecontroller

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** On-device SQLite history. No network, no new dependencies. */
class SqliteHistoryStorage(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "history.db", null, 1), HistoryStorage {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT, ts INTEGER NOT NULL, " +
                "job_type TEXT NOT NULL, param TEXT NOT NULL, output TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun insert(timestampMillis: Long, jobType: String, param: String, output: String): Long {
        val v = ContentValues().apply {
            put("ts", timestampMillis); put("job_type", jobType); put("param", param); put("output", output)
        }
        return writableDatabase.insert("history", null, v)
    }

    override fun all(): List<HistoryEntry> =
        readableDatabase.rawQuery("SELECT id, ts, job_type, param, output FROM history ORDER BY ts DESC, id DESC", null)
            .use { c -> generateSequence { if (c.moveToNext()) row(c) else null }.toList() }

    override fun get(id: Long): HistoryEntry? =
        readableDatabase.rawQuery(
            "SELECT id, ts, job_type, param, output FROM history WHERE id = ?", arrayOf(id.toString())
        ).use { c -> if (c.moveToFirst()) row(c) else null }

    override fun delete(id: Long) {
        writableDatabase.delete("history", "id = ?", arrayOf(id.toString()))
    }

    private fun row(c: Cursor) =
        HistoryEntry(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getString(4))
}
