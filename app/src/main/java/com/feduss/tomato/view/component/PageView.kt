package com.feduss.tomato.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.PositionIndicator
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun PageView(
    columnState: ScalingLazyColumnState = rememberColumnState(),
    endCurvedText: String,
    content: @Composable BoxScope.(ScalingLazyColumnState) -> Unit
) {

    ScreenScaffold(
        timeText = {
            CustomTimeText(
                columnState = columnState,
                endCurvedText = endCurvedText
            )
        },
        scrollState = columnState.state,
        positionIndicator = { PositionIndicator(scalingLazyListState = columnState.state) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        content(columnState)
    }
}
