package com.automation.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.automation.data.database.dao.MessagesDao
import com.automation.data.database.entity.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 1
)
abstract class KeeperDatabase : RoomDatabase() {
    companion object {
        const val SMS_KEEPER_DB = "pay_pints_auto_keeper_database"
    }

    abstract fun messagesDao(): MessagesDao
}