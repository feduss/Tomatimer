package com.feduss.tomato.models

import com.feduss.tomato.enums.ChipType

class Chip(val title: String, var value: String, val unit: String = "", val type: ChipType)

