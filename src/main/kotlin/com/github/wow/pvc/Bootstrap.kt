package com.github.wow.pvc

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.wow.pvc.util.BotConfig
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Scanner
import kotlin.system.exitProcess

fun main() {
    val logger = LoggerFactory.getLogger({}.javaClass)
    val file = File("config.json")
    val config = JsonMapper().registerKotlinModule().readValue(
        file.apply {
            if (!exists()) {
                createNewFile()
                appendBytes({}.javaClass.getResourceAsStream("/config.json")!!.readBytes())
            }
        }.inputStream(),
        BotConfig::class.java
    )
    val bot = Bot(config)

    val scanner = Scanner(System.`in`)
    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
        when (line) {
            "stop" -> {
                bot.stop()
                exitProcess(0)
            }
            "servers" -> {
                logger.info("Servers: " + bot.jda.guilds.size)
            }
        }
    }
}
