package com.example.midterm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Note::class],
    version = 2
)
abstract class NotesDatabase: RoomDatabase() {
    abstract val dao: NoteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Thêm cột mới
                database.execSQL("ALTER TABLE note ADD COLUMN imageUri TEXT")
            }
        }
    }
}