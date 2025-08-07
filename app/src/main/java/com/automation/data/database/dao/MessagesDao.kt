package com.automation.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.automation.data.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {
    @Insert
    fun insertAll(vararg messages: MessageEntity)

    @Query("UPDATE messages SET dispatch_date = :dispatchDate WHERE id = :messageId")
    suspend fun updateDispatchDate(messageId: Long, dispatchDate: String)

    @Query("SELECT * FROM messages")
    fun getAll(): List<MessageEntity>

    @Query("SELECT * FROM messages")
    fun getFlowAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE is_failed = 1")
    fun getFailedMessages(): List<MessageEntity>

    @Query("DELETE FROM messages")
    fun deleteAll()

    @Delete
    fun deleteMessage(msg: MessageEntity)

    @Update
    fun updateMessages(vararg messages: MessageEntity)
}