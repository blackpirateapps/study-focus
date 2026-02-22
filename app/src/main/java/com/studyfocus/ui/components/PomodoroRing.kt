package com.studyfocus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.studyfocus.ui.theme.Dimens

@Composable
fun PomodoroRing(
    progress: Float,
    timeText: String,
    sessionText: String,
    taskName: String = "",
    ringSize: Dp = Dimens.PomodoroRingSize,
    strokeWidth: Dp = Dimens.PomodoroRingStroke,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.outlineVariant,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "pomodoroProgress"
    )

    Box(
        modifier = modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        // Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweep = 360f * animatedProgress
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val padding = strokeWidth.toPx() / 2

            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                topLeft = Offset(padding, padding),
                size = Size(size.width - strokeWidth.toPx(), size.height - strokeWidth.toPx())
            )

            // Progress
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = stroke,
                topLeft = Offset(padding, padding),
                size = Size(size.width - strokeWidth.toPx(), size.height - strokeWidth.toPx())
            )
        }

        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 48.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXs))

            Text(
                text = sessionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (taskName.isNotBlank()) {
                Spacer(modifier = Modifier.height(Dimens.SpacingXs))
                Text(
                    text = taskName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Dimens.SpacingHuge)
                )
            }
        }
    }
}
