package com.github.wow.pvc.util

import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent

object Constants {
    val intents = listOf(
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_VOICE_STATES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_MESSAGES
    )
    val activity = Activity.playing("!!방생성채널설정")
    val commandPrefix = "!!"
}
