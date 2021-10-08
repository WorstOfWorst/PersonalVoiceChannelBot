package com.github.wow.pvc

import com.github.wow.pvc.model.CreationChannel
import com.github.wow.pvc.model.Guild
import com.github.wow.pvc.model.Models
import com.github.wow.pvc.model.VoiceChannel
import io.requery.kotlin.eq
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DatabaseTest {

    val dataSource = PGSimpleDataSource().apply {
        user = System.getenv("DB_USERNAME")
        password = System.getenv("DB_PASSWORD")
        databaseName = System.getenv("DB_DATABASE")
    }

    val requeryConfig = KotlinConfiguration(Models.DEFAULT, dataSource)
    val guildDataStore = KotlinEntityDataStore<Guild>(requeryConfig)

    @BeforeEach
    fun beforeEach() {
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.DROP_CREATE)
    }

    @Test
    fun `insert guild data test`() {
        guildDataStore.invoke {
            val entity = Guild().apply { id = 1 }
            insert(entity)
        }
    }

    @Test
    fun `select guild data test`() {
        guildDataStore.invoke {
            val where = Guild::id eq 1
            val result = select(Guild::class) where (where) limit 1
            assertNull(result.get().firstOrNull())
        }

        guildDataStore.invoke {
            val entity = Guild().apply { id = 1 }
            val where = Guild::id eq 1
            insert(entity)
            val result = select(Guild::class) where (where) limit 1
            assertEquals(1, result.get().firstOrNull()?.id)
        }
    }

    @Test
    fun `relationship test`() {
        guildDataStore.invoke {
            val entity = Guild().apply {
                id = 1
                val guild = this
                voiceChannels.add(VoiceChannel().apply {
                    id = 1
                    textChannel = -1
                    this.guild = guild
                })
                creationChannels.add(CreationChannel().apply {
                    id = 1
                    this.guild = guild
                })
            }
            insert(entity)

            val where = Guild::id eq 1
            val result = select(Guild::class) where (where) limit 1

            val found = result.get().firstOrNull()

            assertNotNull(found)
            assertEquals(1, found.id)
            assertEquals(1, found.voiceChannels.size)
            assertEquals(1, found.creationChannels.size)
        }
    }
}
