package com.github.wow.pvc.model

import io.requery.Column
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne
import io.requery.Persistable
import io.requery.Table

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
