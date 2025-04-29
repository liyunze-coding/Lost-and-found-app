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
                ${Util.LOCATION} TEXT
            )
        """.trimIndent()

        sqLiteDatabase.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS ${Util.TABLE_NAME}"

        sqLiteDatabase.execSQL(dropTable)

        Log.d("DatabaseHelper", "onUpgrade: Database upgraded")

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
                arrayOf(Util.NAME, Util.LOST_OR_FOUND, Util.PHONE, Util.DESCRIPTION, Util.DATE, Util.LOCATION),
                null,
                null,
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(Util.NAME))
                    val lostOrFound = cursor.getString(cursor.getColumnIndexOrThrow(Util.LOST_OR_FOUND))
                    val phone = cursor.getString(cursor.getColumnIndexOrThrow(Util.PHONE))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(Util.DESCRIPTION))
                    val dateString = cursor.getString(cursor.getColumnIndexOrThrow(Util.DATE))
                    val location = cursor.getString(cursor.getColumnIndexOrThrow(Util.LOCATION))

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val date = dateFormat.parse(dateString)!!

                    val item = Item(
                        name = name,
                        lostOrFound = lostOrFound,
                        phone = phone,
                        description = description,
                        date = date,
                        location = location
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

}
