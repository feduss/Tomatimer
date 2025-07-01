package com.feduss.tomatimer.entity.models

import com.feduss.tomatimer.entity.enums.ChipType

class Chip(val shortTitle: String, val fullTitle: String, var value: String, val unit: String = "",
           val type: ChipType
)

