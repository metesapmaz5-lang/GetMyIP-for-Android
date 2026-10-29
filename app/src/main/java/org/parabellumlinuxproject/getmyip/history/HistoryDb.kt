package org.parabellumlinuxproject.getmyip.history

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.parabellumlinuxproject.getmyip.Snapshot
import org.parabellumlinuxproject.getmyip.core.Family
import org.parabellumlinuxproject.getmyip.core.IpInfo

data class HistoryEntry(
    val id: Long,
    val time: Long,
    val family: Family,
    val ip: String,
    val isp: String?,
    val asn: String?,
    val country: String?,
    val city: String?,
    val provider: String,
    val vpn: Boolean,
    val tor: Boolean?,
)

data class IpChange(val family: Family, val previous: String, val current: String)

/** IP history, stored only on this device. */
class HistoryDb private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                time INTEGER NOT NULL,
                family TEXT NOT NULL,
                ip TEXT NOT NULL,
                isp TEXT,
                asn TEXT,
                country TEXT,
                city TEXT,
                provider TEXT NOT NULL,
                vpn INTEGER NOT NULL,
                tor INTEGER
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    private fun lastIp(family: Family): String? =
        readableDatabase.rawQuery(
            "SELECT ip FROM $TABLE WHERE family = ? ORDER BY _id DESC LIMIT 1",
            arrayOf(family.name),
        ).use { c -> if (c.moveToFirst()) c.getString(0) else null }

    private fun insert(time: Long, family: Family, info: IpInfo, provider: String, vpn: Boolean, tor: Boolean?) {
        val values = ContentValues().apply {
            put("time", time)
            put("family", family.name)
            put("ip", info.ip)
            put("isp", info.isp)
            put("asn", info.asn)
            put("country", info.country)
            put("city", info.city)
            put("provider", provider)
            put("vpn", if (vpn) 1 else 0)
            if (tor == null) putNull("tor") else put("tor", if (tor) 1 else 0)
        }
        val db = writableDatabase
        db.insert(TABLE, null, values)
        db.execSQL(
            "DELETE FROM $TABLE WHERE _id NOT IN (SELECT _id FROM $TABLE ORDER BY _id DESC LIMIT $MAX_ENTRIES)"
        )
    }

    /**
     * Saves every address that differs from the last saved one.
     * Returns the changes. The very first address is saved but is not a change.
     */
    @Synchronized
    fun record(snapshot: Snapshot, provider: String, now: Long = System.currentTimeMillis()): List<IpChange> {
        val changes = mutableListOf<IpChange>()
        for ((family, info) in listOf(Family.V4 to snapshot.v4, Family.V6 to snapshot.v6)) {
            if (info == null) continue
            val last = lastIp(family)
            if (last == info.ip) continue
            val tor = if (family == Family.V4) snapshot.tor else null
            insert(now, family, info, provider, snapshot.net.isVpn, tor)
            if (last != null) changes += IpChange(family, last, info.ip)
        }
        return changes
    }

    fun all(): List<HistoryEntry> =
        readableDatabase.rawQuery(
            "SELECT _id, time, family, ip, isp, asn, country, city, provider, vpn, tor FROM $TABLE ORDER BY _id DESC",
            null,
        ).use { c ->
            buildList {
                while (c.moveToNext()) {
                    add(
                        HistoryEntry(
                            id = c.getLong(0),
                            time = c.getLong(1),
                            family = Family.valueOf(c.getString(2)),
                            ip = c.getString(3),
                            isp = c.textOrNull(4),
                            asn = c.textOrNull(5),
                            country = c.textOrNull(6),
                            city = c.textOrNull(7),
                            provider = c.getString(8),
                            vpn = c.getInt(9) == 1,
                            tor = if (c.isNull(10)) null else c.getInt(10) == 1,
                        )
                    )
                }
            }
        }

    @Synchronized
    fun clear() {
        writableDatabase.delete(TABLE, null, null)
    }

    private fun Cursor.textOrNull(index: Int): String? = if (isNull(index)) null else getString(index)

    companion object {
        private const val DB_NAME = "history.db"
        private const val DB_VERSION = 1
        private const val TABLE = "history"
        private const val MAX_ENTRIES = 500

        @Volatile private var instance: HistoryDb? = null

        fun get(context: Context): HistoryDb =
            instance ?: synchronized(this) {
                instance ?: HistoryDb(context.applicationContext).also { instance = it }
            }
    }
}
