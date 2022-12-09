package com.feduss.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@Preview
@Composable
fun SetupView(@PreviewParameter(ChipListProvider::class) chips: List<Chip>,
              onChipClicked: (String) -> Unit = {},
              onPlayIconClicked: () -> Unit = {}) {
    Column(
        Modifier.padding(32.dp, 16.dp, 32.dp, 8.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { chip ->
                ChipView(
                    chip = chip,
                    tag = chip.type.tag,
                    fontSize = 10f,
                    onChipClicked = onChipClicked
                )
            }
        }
        IconButton(
            modifier = Modifier
                .width(16.dp)
                .aspectRatio(1f)
                .background(
                    color = Color(("#E3BAFF".toColorInt())),
                    shape = CircleShape
                ),
            content = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_play_24dp),
                    contentDescription = "Play icon",
                    tint = Color.Black
                )
            },
            onClick = {
                onPlayIconClicked()
            }
        )
    }
}