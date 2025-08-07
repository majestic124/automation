package com.automation.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationsProvider {
//    private val migration_1_2: Migration
//        get() = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("")
//            }
//        }


    val migrations: Array<Migration> get() = arrayOf()
}