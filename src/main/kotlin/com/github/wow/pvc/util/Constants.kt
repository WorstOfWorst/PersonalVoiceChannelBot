package com.github.wow.pvc.util

import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent

object Constants {
    val intents = listOf(
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_WEBHOOKS,
        GatewayIntent.GUILD_MESSAGE_TYPING,
        GatewayIntent.DIRECT_MESSAGE_TYPING,
        GatewayIntent.GUILD_VOICE_STATES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_EMOJIS,
        GatewayIntent.GUILD_MESSAGES,
    )
    val activity = Activity.playing("!!방생성채널추가, !!방생성채널제거, !!방생성채널목록")
    val commandPrefix = "!!"
}
