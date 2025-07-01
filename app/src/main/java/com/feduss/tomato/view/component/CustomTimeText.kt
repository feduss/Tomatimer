package com.feduss.tomato.view.component

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.curvedText
import com.feduss.tomato.uistate.extension.PurpleCustom
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.scrollAway
import java.util.Locale

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun CustomTimeText(columnState: ScalingLazyColumnState, endCurvedText: String) {
    val timeSource = TimeTextDefaults.timeSource(
        DateFormat.getBestDateTimePattern(Locale.getDefault(), "HH:mm")
    )

    val textColor = Color.PurpleCustom

    val modifier = Modifier.scrollAway(columnState)

    if (endCurvedText.isNotEmpty()) {
        ResponsiveTimeText(
            modifier = modifier,
            timeSource = timeSource,
            endCurvedContent = {
                curvedText(
                    text = endCurvedText,
                    color = textColor
                )
            }
        )
    } else {
        ResponsiveTimeText(
            modifier = modifier,
            timeSource = timeSource,
        )
    }
}