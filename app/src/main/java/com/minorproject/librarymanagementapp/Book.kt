package com.minorproject.librarymanagementapp

data class Book(
    val isbn:String? = null,
    val name: String? = null,
    val author: String? = null,
    val publication: String? = null,
    var stock: Int? = 0,
    var reservedBy: MutableList<String>? = ArrayList()
)
