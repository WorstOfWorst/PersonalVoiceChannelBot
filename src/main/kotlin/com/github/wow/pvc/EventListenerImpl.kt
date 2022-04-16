package com.github.wow.pvc

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.github.wow.pvc.util.Constants
import com.github.wow.pvc.util.IS_DEVELOPMENT_BUILD
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.StageChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
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

    init {
        (logger as Logger).level = if (IS_DEVELOPMENT_BUILD) Level.DEBUG else Level.INFO
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
            is GuildJoinEvent -> onGuildJoin(event)
            is GuildVoiceJoinEvent -> onGuildVoiceJoin(event)
            is GuildVoiceLeaveEvent, is GuildVoiceMoveEvent -> onGuildVoiceUpdateEvent(event as GenericGuildVoiceUpdateEvent)
            is ChannelDeleteEvent -> onChannelDelete(event)
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
            if (channel is VoiceChannel) {
                bot.createChannels(event.guild, event.member, channel.parentCategory)
            } else if (channel is StageChannel) {
                bot.createChannels(event.guild, event.member, channel.parentCategory)
            }
        } else if (guild.voiceChannels.any { it.id == channel.idLong }) {
            bot.applyJoin(event.member, channel)
        }
    }

    private fun onGuildVoiceUpdateEvent(event: GenericGuildVoiceUpdateEvent) {
        val guild = bot.getGuildDataOrSave(event.guild)

        if (event.channelLeft != null) {
            val channel = event.channelLeft!!
            if (guild.voiceChannels.any { it.id == channel.idLong }) {
                logger.debug(channel.members.size.toString())
                if (channel.members.size <= 0) {
                    bot.deleteCreatedChannels(channel)
                } else {
                    bot.applyQuit(event.member, channel)
                }
            }
        }
        if (event.channelJoined != null) {
            val channel = event.channelJoined!!
            if (guild.creationChannels.any { it.id == channel.idLong }) {
                if (channel is VoiceChannel) {
                    bot.createChannels(event.guild, event.member, channel.parentCategory)
                } else if (channel is StageChannel) {
                    bot.createChannels(event.guild, event.member, channel.parentCategory)
                }
            } else if (guild.voiceChannels.any { it.id == channel.idLong }) {
                bot.applyJoin(event.member, channel)
            }
        }
    }

    private fun onChannelDelete(event: ChannelDeleteEvent) {
        val channel = event.channel
        if (!event.channelType.isAudio || channel !is AudioChannel) return
        val guild = bot.getGuildDataOrSave(event.guild)

        if (guild.creationChannels.any { it.id == channel.idLong }) {
            bot.deleteCreatedChannels(channel)
        }
    }
}
