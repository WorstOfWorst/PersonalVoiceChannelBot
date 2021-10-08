package com.github.wow.pvc.util

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonIgnoreProperties(ignoreUnknown = true)
data class BotConfig(
    val token: String,
    @JsonDeserialize(contentAs = DatabaseConfig::class)
    val database: DatabaseConfig
)
