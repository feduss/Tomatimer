package com.feduss.tomato.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.feduss.tomato.uistate.R
import com.feduss.tomato.uistate.extension.PurpleCustom
import com.feduss.tomato.uistate.viewmodel.ChipUiState

@Composable
fun ChipView(
    chipUiState: ChipUiState,
    tag: Int = 0,
    onChipClicked: (String) -> Unit = {}
) {
    val purpleColor = Color.PurpleCustom
    val fontSize = TextUnit(10f, TextUnitType.Sp)

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, purpleColor),
        onClick = {
            onChipClicked(tag.toString())
        }
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    chipUiState.fullTitle,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize
                )

                Text(
                    "${chipUiState.value}${chipUiState.unit}",
                    textAlign = TextAlign.Center,
                    fontSize = fontSize
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .width(1.dp),
                    color = Color.White
                )

                Icon(
                    modifier = Modifier.height(24.dp),
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.ic_edit
                    ),
                    contentDescription = "ic_edit",
                    tint = purpleColor
                )
            }
        }
    }
}