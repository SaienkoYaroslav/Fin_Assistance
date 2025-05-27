package com.assistancefin.tpaofinassistance.data.repository

import com.assistancefin.tpaofinassistance.data.db.TransactionDao
import com.assistancefin.tpaofinassistance.data.db.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
): Repository {

    override fun getAllTransaction(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun insertTransaction(item: TransactionEntity) {
        transactionDao.insertTransaction(item)
    }


    override suspend fun deleteTransaction(item: TransactionEntity) {
        transactionDao.deleteTransaction(item)
    }

}