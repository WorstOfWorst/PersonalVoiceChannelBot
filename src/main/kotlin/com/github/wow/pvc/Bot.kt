package com.github.wow.pvc

import com.github.wow.pvc.command.Command
import com.github.wow.pvc.command.PvcSetCommand
import com.github.wow.pvc.model.CreationChannel
import com.github.wow.pvc.model.Guild
import com.github.wow.pvc.model.IGuild
import com.github.wow.pvc.model.IVoiceChannel
import com.github.wow.pvc.model.Models
import com.github.wow.pvc.model.VoiceChannel
import com.github.wow.pvc.util.BotConfig
import com.github.wow.pvc.util.Constants
import io.requery.kotlin.eq
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.Category
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.postgresql.ds.PGSimpleDataSource
import java.util.concurrent.TimeUnit
import javax.sql.DataSource
import net.dv8tion.jda.api.entities.Guild as JDAGuild
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel as JDAAudioChannel

class Bot(private val config: BotConfig) {
    val jda: JDA
    private val listener: EventListener
    private val dataSource: DataSource
    private val requeryConfig: KotlinConfiguration
    private val guildDataStore: KotlinEntityDataStore<IGuild>
    private val voiceChannelDataStore: KotlinEntityDataStore<IVoiceChannel>

    private val commandMap: MutableMap<String, Command> = mutableMapOf()

    init {
        dataSource = PGSimpleDataSource().apply {
            serverNames = arrayOf(config.database.host)
            portNumbers = intArrayOf(config.database.port)
            user = config.database.username
            password = config.database.password
            databaseName = config.database.database
        }
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        requeryConfig = KotlinConfiguration(Models.DEFAULT, dataSource)
        guildDataStore = KotlinEntityDataStore(requeryConfig)
        voiceChannelDataStore = KotlinEntityDataStore(requeryConfig)
        listener = EventListenerImpl(this)

        jda = JDABuilder.createDefault(config.token)
            .enableIntents(Constants.intents)
            .setActivity(Constants.activity)
            .enableCache(CacheFlag.VOICE_STATE)
            .enableCache(CacheFlag.MEMBER_OVERRIDES)
            .addEventListeners(listener)
            .build()

        jda.awaitReady()

        registerCommands(
            PvcSetCommand(this)
        )
    }

    fun getGuildDataOrSave(guild: JDAGuild): Guild {
        val where = Guild::id eq guild.idLong
        val result = guildDataStore.select(Guild::class) where (where) limit 1
        return result.get().firstOrNull() ?: guildDataStore.insert(Guild().apply { id = guild.idLong })
    }

    fun setCreationChannel(guild: JDAGuild, channelId: Long) {
        val guildData = getGuildDataOrSave(guild)
        guildData.creationChannels.add(
            CreationChannel().apply {
                id = channelId
                this.guild = guildData
            }
        )
        guildDataStore.update(guildData)
    }

    fun createChannels(guild: JDAGuild, owner: Member, category: Category?) {
        val channel = guild.createVoiceChannel("${owner.effectiveName}의 통화방", category)
            .setUserlimit(99)

        if (category != null) {
            channel.syncPermissionOverrides()
        }
        channel
            .addMemberPermissionOverride(
                guild.selfMember.idLong,
                listOf(Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL),
                listOf()
            )
            .addMemberPermissionOverride(owner.idLong, listOf(Permission.MANAGE_CHANNEL), listOf())

        channel.queue { voiceChannel ->
            guild.createTextChannel("${owner.effectiveName}의 채팅방", category)
                .addMemberPermissionOverride(
                    owner.idLong,
                    listOf(Permission.MANAGE_CHANNEL, Permission.VIEW_CHANNEL),
                    listOf()
                )
                .addMemberPermissionOverride(
                    guild.selfMember.idLong,
                    listOf(Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL),
                    listOf()
                )
                .setNSFW(false)
                .addPermissionOverride(guild.publicRole, listOf(), listOf(Permission.VIEW_CHANNEL))
                .queue { textChannel ->
                    guild.moveVoiceMember(owner, voiceChannel).queue()

                    guildDataStore.invoke {
                        val where = Guild::id eq guild.idLong
                        val result = select(Guild::class) where (where) limit 1
                        result.get().firstOrNull()?.let { guildData ->
                            guildData.voiceChannels.add(
                                VoiceChannel().apply {
                                    this.id = voiceChannel.idLong
                                    this.textChannel = textChannel.idLong
                                    this.guild = guildData
                                }
                            )
                            update(guildData)
                        }
                    }
                }
        }
    }

    fun applyJoin(member: Member, channel: JDAAudioChannel) {
        val guildData = getGuildDataOrSave(channel.guild)
        val voiceChannel = guildData.voiceChannels.find { it.id == channel.idLong } ?: return

        val textChannel = channel.guild.getTextChannelById(voiceChannel.textChannel) ?: return

        textChannel.upsertPermissionOverride(member)
            .setAllowed(listOf(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY)).setDenied(listOf())
            .queue(null) {}
    }

    fun applyQuit(member: Member, channel: JDAAudioChannel) {
        val guildData = getGuildDataOrSave(channel.guild)
        val voiceChannel = guildData.voiceChannels.find { it.id == channel.idLong } ?: return

        val textChannel = channel.guild.getTextChannelById(voiceChannel.textChannel) ?: return

        textChannel.upsertPermissionOverride(member).setAllowed(listOf())
            .setDenied(listOf(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY))
            .queue(null) {}
    }

    fun deleteCreatedChannels(channel: JDAAudioChannel) {
        val guildData = getGuildDataOrSave(channel.guild)
        val voiceChannel = guildData.voiceChannels.find { it.id == channel.idLong } ?: return

        channel.guild.getVoiceChannelById(channel.idLong)!!.delete().reason("빈 개인 통화방").queue(null) {}

        val textChannel = channel.guild.getTextChannelById(voiceChannel.textChannel)
        textChannel?.delete()?.reason("빈 개인 채팅방")?.queue(null) {}

        guildData.voiceChannels.remove(voiceChannel)
        guildDataStore.update(guildData)
        voiceChannelDataStore.delete(listOf(voiceChannel))
    }

    fun onCommand(commandName: String, message: Message, args: List<String>) {
        commandMap[commandName]?.let { command ->
            if (command.testPermission(message) && !command.execute(message, args)) {
                message.reply("사용법: ${command.usage}").queue {
                    it.delete().queueAfter(5, TimeUnit.SECONDS, null) {}
                    message.delete().queueAfter(5, TimeUnit.SECONDS, null) {}
                }
            }
        }
    }

    private fun registerCommand(command: Command) {
        commandMap[command.name] = command

        if (command.aliases.isNotEmpty()) {
            command.aliases.map { commandMap[it] = command }
        }
    }

    private fun registerCommands(vararg commands: Command) = commands.forEach(::registerCommand)

    fun stop() {
        jda.shutdown()
        guildDataStore.close()
    }
}
