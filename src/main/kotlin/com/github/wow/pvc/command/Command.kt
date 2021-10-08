package com.github.wow.pvc.command

import com.github.wow.pvc.Bot
import net.dv8tion.jda.api.entities.Message

interface Command {
    val name: String
    val description: String
    val usage: String
    val aliases: List<String>
    val bot: Bot
    fun testPermission(message: Message): Boolean
    fun execute(message: Message, args: List<String>): Boolean
}
