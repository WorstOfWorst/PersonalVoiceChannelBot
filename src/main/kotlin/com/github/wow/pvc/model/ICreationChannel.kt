package com.github.wow.pvc.model

import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne
import io.requery.Persistable
import io.requery.Table

@Entity
@Table(name = "creation_channels")
interface ICreationChannel : Persistable {
    @get:Key
    var id: Long

    @get:ManyToOne
    var guild: IGuild
}
