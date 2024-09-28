package com.minorproject.librarymanagementapp

data class Reservation(
    val isbn: String? = null,
    val bname: String? = null,
    val sid: String? = null,
    val requestTime: Long? = null,
    val status: String? = null,
    val beenIssued: String? = null
)