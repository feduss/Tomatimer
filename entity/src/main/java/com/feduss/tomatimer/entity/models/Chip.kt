package com.feduss.tomatimer.entity.models

import com.feduss.tomatimer.entity.enums.ChipType
import java.util.UUID

class Chip(
    val shortTitle: String,
    val fullTitle: String,
    var value: String,
    val unit: String = "",
    val type: ChipType,
    val uuid: UUID = UUID.randomUUID()
)

