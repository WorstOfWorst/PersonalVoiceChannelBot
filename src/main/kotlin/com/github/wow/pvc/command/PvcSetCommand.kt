package com.github.wow.pvc.command

import com.github.wow.pvc.Bot
import com.github.wow.pvc.util.Constants
import com.github.wow.pvc.util.isNumber
import net.dv8tion.jda.api.entities.Message

class PvcSetCommand(bot: Bot) : BaseCommand(
    "pvcset",
    "개인통화방 생성 채널을 설정합니다.",
    "${Constants.commandPrefix}pvcset <Channel ID>",
    listOf("방생성채널설정"),
    bot
) {
    override fun execute(message: Message, args: List<String>): Boolean {
        if (args.isEmpty() || !args[0].isNumber()) return false

        val channelId = args[0].toLong()

        message.guild.getVoiceChannelById(channelId)?.let { channel ->
            bot.setCreationChannel(message.guild, channelId)
            replyAndDelete(message, "개인 통화방 생성 채널에 ${channel.asMention} 채널을 추가했어요.")
        } ?: replyAndDelete(message, "채널을 찾을 수 없어요.")
        return true
    }
}
