package com.assistancefin.tpaofinassistance.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_table"
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val description: String,
    val sum: String,
    val date: String,
    val type: String,
    val categoryImgId: Int,
)
