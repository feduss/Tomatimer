package com.feduss.tomato.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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

@Preview
@Composable
fun ChipView(
    @PreviewParameter(ChipProvider::class) chip: Chip, tag: Int = 0,
    fontSize: Float = 10f, chipHeight: Int = 64, onChipClicked: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier.height(chipHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    chip.shortTitle,
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