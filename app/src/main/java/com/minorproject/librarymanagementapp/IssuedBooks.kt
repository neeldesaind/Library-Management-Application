package com.minorproject.librarymanagementapp

data class IssuedBooks(
    val isbn: String? = null,
    val bname: String? = null,
    val sid: String? = null,
    val issue_date: Long? = null,
    val return_date: Long? = null,
    val fine: Long = 0
)
