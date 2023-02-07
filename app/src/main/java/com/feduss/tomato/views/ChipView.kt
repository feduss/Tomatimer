package com.feduss.tomato.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.*
import androidx.core.graphics.toColorInt
import com.feduss.tomato.models.Chip
import com.feduss.tomato.provider.ChipProvider

@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun ChipView(@PreviewParameter(ChipProvider::class) chip: Chip, tag: Int = 0,
             fontSize: Float = 10f, onChipClicked: (String) -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Button(
            modifier = Modifier.aspectRatio(1f),
            shape = CircleShape,
            border = BorderStroke(
                Dp(1f),
                Color("#C26EFA".toColorInt())
            ),
            contentPadding = PaddingValues(0.dp),
            onClick = {
                onChipClicked(tag.toString())
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(
                    chip.title,
                    color = Color(("#E3BAFF".toColorInt())),
                    fontSize = TextUnit(fontSize, TextUnitType.Sp),
                    textAlign = TextAlign.Center
                )

                Text(
                    chip.value,
                    color = Color(("#E3BAFF".toColorInt())),
                    fontSize = TextUnit(fontSize, TextUnitType.Sp)
                )
                if (chip.unit != "") {
                    Text(
                        chip.unit,
                        color = Color(("#E3BAFF".toColorInt())),
                        fontSize = TextUnit(fontSize, TextUnitType.Sp)
                    )
                }
            }
        }
    }
}