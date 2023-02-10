package com.feduss.tomato.models

import com.feduss.tomato.enums.ChipType

class Chip(val shortTitle: String, val fullTitle: String, var value: String, val unit: String = "", val type: ChipType)

