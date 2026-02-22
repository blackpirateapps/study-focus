package com.studyfocus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.studyfocus.ui.theme.Dimens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember

@Composable
fun ProjectCard(
    name: String,
    colorHex: String,
    taskCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .width(160.dp)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        shape = RoundedCornerShape(Dimens.RadiusLg),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingLg)
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(Dimens.RadiusSm))
                    .background(color)
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingMd))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXxs))

            Text(
                text = if (taskCount == 1) "1 task" else "$taskCount tasks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
