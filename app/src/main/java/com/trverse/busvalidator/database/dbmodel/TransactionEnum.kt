package com.trverse.busvalidator.database.dbmodel

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "transaction_enum")
data class TransactionEnum(@PrimaryKey(autoGenerate = true) val id : Int, val Name: String)
