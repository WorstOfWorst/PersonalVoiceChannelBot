package com.github.wow.pvc.model

import io.requery.*

@Entity
@Table(name = "voice_channels")
interface IVoiceChannel : Persistable {
    @get:Key
    var id: Long

    @get:Column
    var textChannel: Long

    @get:ManyToOne
    var guild: IGuild
}
