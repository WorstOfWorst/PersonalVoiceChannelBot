package com.github.wow.pvc

import com.github.wow.pvc.util.Constants
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceUpdateEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory

class EventListenerImpl(private val bot: Bot) : EventListener {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val preventDeleteMap: MutableMap<Long, Long> = mutableMapOf()

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
            is GuildJoinEvent -> onGuildJoin(event)
            is GuildVoiceJoinEvent -> onGuildVoiceJoin(event)
            is GuildVoiceLeaveEvent, is GuildVoiceMoveEvent -> onGuildVoiceUpdateEvent(event as GenericGuildVoiceUpdateEvent)
            is VoiceChannelDeleteEvent -> onVoiceChannelDelete(event)
        }
    }

    private fun onMessageReceived(event: MessageReceivedEvent) {
        logger.debug("messaged received: ${event.message.contentRaw}")

        val message = event.message
        if (event.author.isBot) return

        var content = message.contentRaw.trim()
        if (content.startsWith(Constants.commandPrefix)) {
            content = content.replace(Constants.commandPrefix, "").trim()
            val args = content.split(" ").toMutableList()
            val commandName = args.removeFirst()
            bot.onCommand(commandName, message, args)
        }
    }

    private fun onGuildJoin(event: GuildJoinEvent) {
        logger.debug("guild join: ${event.guild.idLong}")
        val guild = event.guild
        bot.getGuildDataOrSave(guild)
    }

    private fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val channel = event.channelJoined
        val guild = bot.getGuildDataOrSave(event.guild)

        if (guild.creationChannels.any { it.id == channel.idLong }) {
            bot.createChannels(event.guild, event.member, channel.parent)
        }
    }

    private fun onGuildVoiceUpdateEvent(event: GenericGuildVoiceUpdateEvent) {
        val guild = bot.getGuildDataOrSave(event.guild)

        if (event.channelLeft != null) {
            val channel = event.channelLeft!!
            if (preventDeleteMap.contains(event.member.idLong) && preventDeleteMap.getValue(event.member.idLong) == channel.idLong) {
                preventDeleteMap.remove(event.member.idLong)
                return
            }
            if (guild.voiceChannels.any { it.id == channel.idLong }) {
                logger.debug(channel.members.size.toString())
                if (channel.members.size <= 0) {
                    bot.deleteCreatedChannels(channel)
                }
            }
        }
        if (event.channelJoined != null) {
            val channel = event.channelJoined!!
            if (preventDeleteMap.contains(event.member.idLong) && preventDeleteMap.getValue(event.member.idLong) == channel.idLong) {
                preventDeleteMap.remove(event.member.idLong)
                return
            }
            if (guild.creationChannels.any { it.id == channel.idLong }) {
                bot.createChannels(event.guild, event.member, channel.parent)
            }
        }
    }

    private fun onVoiceChannelDelete(event: VoiceChannelDeleteEvent) {
        val channel = event.channel
        val guild = bot.getGuildDataOrSave(event.guild)

        if (guild.creationChannels.any { it.id == channel.idLong }) {
            channel.members.forEach {
                preventDeleteMap[it.idLong] = channel.idLong
            }
            bot.removeCreationChannel(event.guild, channel)
        }
    }
}
