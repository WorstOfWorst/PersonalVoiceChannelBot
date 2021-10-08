package com.github.wow.pvc

import com.github.wow.pvc.util.BotConfig
import com.github.wow.pvc.util.DatabaseConfig

class BotTest {

    val bot: Bot = Bot(
        BotConfig(
            token = System.getenv("BOT_TOKEN"),
            database = DatabaseConfig(
                host = "127.0.0.1",
                port = 5432,
                username = System.getenv("DB_USERNAME"),
                password = System.getenv("DB_PASSWORD"),
                database = System.getenv("DB_DATABASE"),
            )
        )
    )
}
