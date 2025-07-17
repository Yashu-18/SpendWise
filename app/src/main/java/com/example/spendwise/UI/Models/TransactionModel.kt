package com.example.spendwise.UI.Models

import java.util.Date

data class TransactionModel(
        var image : Int,
        var date : Date,
        var amount : Int,
        var category : String
)