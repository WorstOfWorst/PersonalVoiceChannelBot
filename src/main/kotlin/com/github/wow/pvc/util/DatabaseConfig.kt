package com.github.wow.pvc.util

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val password: String,
    val username: String
)
