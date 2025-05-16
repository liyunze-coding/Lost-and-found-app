package com.deakin.lostandfound.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.deakin.lostandfound.model.Item
import com.deakin.lostandfound.util.Util
import java.text.SimpleDateFormat
import java.util.Locale

class DatabaseHelper(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, Util.DATABASE_NAME, factory, Util.DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val CREATE_USER_TABLE = """
            CREATE TABLE ${Util.TABLE_NAME} (
                ${Util.ITEM_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Util.NAME} TEXT,
                ${Util.LOST_OR_FOUND} TEXT,
                ${Util.PHONE} TEXT,
                ${Util.DESCRIPTION} TEXT,
                ${Util.DATE} TEXT,
                ${Util.LOCATION} TEXT,
                ${Util.LATITUDE} REAL,
                ${Util.LONGITUDE} REAL
            )
        """.trimIndent()

        sqLiteDatabase.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS ${Util.TABLE_NAME}"

        sqLiteDatabase.execSQL(dropTable)
        onCreate(sqLiteDatabase)
    }

    fun insertItem(item: Item): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues: ContentValues = ContentValues().apply {
            put(Util.NAME, item.name)
            put(Util.LOST_OR_FOUND, item.lostOrFound)
            put(Util.PHONE, item.phone)
            put(Util.DESCRIPTION, item.description)

            // store date as string
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(item.date)
            put(Util.DATE, dateString)

            put(Util.LOCATION, item.location)

            put(Util.LATITUDE, item.latitude)
            put(Util.LONGITUDE, item.longitude)
        }

        val newRowId: Long = db.insert(Util.TABLE_NAME, null, contentValues)
        db.close()

        return newRowId
    }

    fun getItems(): List<Item> {
        val db: SQLiteDatabase = this.readableDatabase
        var cursor: Cursor? = null
        val items: MutableList<Item> = mutableListOf()

        try {
            cursor = db.query(
                Util.TABLE_NAME,
                arrayOf(Util.ITEM_ID, Util.NAME, Util.LOST_OR_FOUND, Util.PHONE, Util.DESCRIPTION, Util.DATE, Util.LOCATION, Util.LATITUDE, Util.LONGITUDE),
                null,
                null,
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                do {
                    val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Util.ITEM_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(Util.NAME))
                    val lostOrFound = cursor.getString(cursor.getColumnIndexOrThrow(Util.LOST_OR_FOUND))
                    val phone = cursor.getString(cursor.getColumnIndexOrThrow(Util.PHONE))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(Util.DESCRIPTION))
                    val dateString = cursor.getString(cursor.getColumnIndexOrThrow(Util.DATE))
                    val location = cursor.getString(cursor.getColumnIndexOrThrow(Util.LOCATION))
                    val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Util.LATITUDE))
                    val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Util.LONGITUDE))

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val date = dateFormat.parse(dateString)!!

                    val item = Item(
                        id = itemId,
                        name = name,
                        lostOrFound = lostOrFound,
                        phone = phone,
                        description = description,
                        date = date,
                        location = location,
                        latitude = latitude,
                        longitude = longitude
                    )
                    items.add(item)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
            db.close()
        }

        return items
    }

    fun deleteItem(itemId: Int): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        var result = false

        try {
            val rowsDeleted = db.delete(Util.TABLE_NAME, "${Util.ITEM_ID} = ?", arrayOf(itemId.toString()))
            result = rowsDeleted > 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting item", e)
        } finally {
            db.close()
        }

        return result
    }
}
