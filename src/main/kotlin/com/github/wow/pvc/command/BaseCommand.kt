package com.github.wow.pvc.command

import com.github.wow.pvc.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import java.util.concurrent.TimeUnit

abstract class BaseCommand(
    override val name: String,
    override val description: String,
    override val usage: String,
    override val aliases: List<String>,
    override val bot: Bot
) : Command {
    fun replyWithEmbed(message: Message, embed: EmbedBuilder) {
        message.replyEmbeds(embed.build())
    }

    fun replyAndDelete(message: Message, content: String) {
        message.reply(content).queue {
            it.delete().queueAfter(5, TimeUnit.SECONDS)
            message.delete().queueAfter(5, TimeUnit.SECONDS)
        }
    }

    override fun testPermission(message: Message): Boolean {
        if (!message.guild.selfMember.hasPermission(
                Permission.VIEW_CHANNEL,
                Permission.MANAGE_CHANNEL,
                Permission.MESSAGE_MANAGE,
                Permission.VOICE_MOVE_OTHERS
            )
        ) {
            replyAndDelete(message, "이 명령어를 실행하기 위해서 봇에 `채널 보기`, `채널 관리`, `메시지 관리`, `멤버 이동` 권한이 필요해요.")
            return false
        }
        if (message.member?.hasPermission(Permission.MANAGE_CHANNEL) == false) {
            replyAndDelete(message, "이 명령어를 실행할 권한이 부족해요. (부족한 권한: `채널 관리`)")
            return false
        }
        return true
    }
}
