package com.github.wow.pvc.model

import io.requery.*

@Entity
@Table(name = "creation_channels")
interface ICreationChannel : Persistable {
    @get:Key
    var id: Long

    @get:ManyToOne
    var guild: IGuild
}
