package com.assistancefin.tpaofinassistance.data.repository

import com.assistancefin.tpaofinassistance.data.db.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllTransaction(): Flow<List<TransactionEntity>>

    suspend fun insertTransaction(item: TransactionEntity)

    suspend fun deleteTransaction(item: TransactionEntity)

}