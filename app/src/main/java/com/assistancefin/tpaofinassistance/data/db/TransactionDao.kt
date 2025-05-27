package com.assistancefin.tpaofinassistance.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(item: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(list: List<TransactionEntity>)

    @Query("SELECT * FROM transaction_table")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Delete
    suspend fun deleteTransaction(item: TransactionEntity)

}
