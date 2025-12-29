package com.example.unitedpoultry.AdminReportModule.CollectionReport.model

data class CollectionRecordModel(
    val title: String,          // Shop name
    val description: String,    // Time + person
    val paymentType: String,    // Cash / Credit / Cheque
    val invoiceNumber: String,
    val amount: String,
    val date: String
)