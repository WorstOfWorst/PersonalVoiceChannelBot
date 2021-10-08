package com.github.wow.pvc.model

import io.requery.CascadeAction
import io.requery.Entity
import io.requery.Key
import io.requery.OneToMany
import io.requery.Persistable
import io.requery.Table

@Entity
@Table(name = "guilds")
interface IGuild : Persistable {
    @get:Key
    var id: Long

    @get:OneToMany(cascade = [CascadeAction.SAVE, CascadeAction.DELETE])
    val voiceChannels: MutableList<IVoiceChannel>

    @get:OneToMany(cascade = [CascadeAction.SAVE, CascadeAction.DELETE])
    val creationChannels: MutableList<ICreationChannel>
}
