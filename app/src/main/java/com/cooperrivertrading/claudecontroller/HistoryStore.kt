package com.cooperrivertrading.claudecontroller

/** One finished job. id is assigned by storage. */
data class HistoryEntry(
    val id: Long,
    val timestampMillis: Long,
    val jobType: String,
    val param: String,
    val output: String
)

/** Storage backend; SQLite on device, in-memory in unit tests. */
interface HistoryStorage {
    fun insert(timestampMillis: Long, jobType: String, param: String, output: String): Long
    fun all(): List<HistoryEntry>
    fun get(id: Long): HistoryEntry?
    fun delete(id: Long)
}

/** Pure history logic: no Android classes. */
class HistoryStore(private val storage: HistoryStorage) {

    /** Saves a finished job. Blank output is not stored. Returns the new id or null. */
    fun record(timestampMillis: Long, jobType: String, param: String?, output: String): Long? {
        if (output.isBlank()) return null
        return storage.insert(timestampMillis, jobType, param?.trim().orEmpty(), output)
    }

    /** Newest first; ties broken by higher id. */
    fun list(): List<HistoryEntry> =
        storage.all().sortedWith(compareByDescending<HistoryEntry> { it.timestampMillis }.thenByDescending { it.id })

    fun get(id: Long): HistoryEntry? = storage.get(id)

    fun delete(id: Long) = storage.delete(id)

    companion object {
        const val PREVIEW_LENGTH = 200

        /** First ~200 characters, whitespace collapsed, with an ellipsis when cut. */
        fun preview(output: String): String {
            val flat = output.trim().replace(Regex("\\s+"), " ")
            return if (flat.length <= PREVIEW_LENGTH) flat else flat.substring(0, PREVIEW_LENGTH) + "…"
        }
    }
}
