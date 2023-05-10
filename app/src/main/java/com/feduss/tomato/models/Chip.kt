package com.feduss.tomato.models

import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.utils.UIText

class Chip(val shortTitle: UIText, val fullTitle: UIText, var value: String, val unit: String = "",
           val type: ChipType)

