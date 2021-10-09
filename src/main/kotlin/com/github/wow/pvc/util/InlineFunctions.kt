package com.github.wow.pvc.util

fun String.isNumber() =
    this.toIntOrNull() != null || this.toFloatOrNull() != null || this.toLongOrNull() != null

const val VERSION = "0.0.1"

const val IS_DEVELOPMENT_BUILD = false
