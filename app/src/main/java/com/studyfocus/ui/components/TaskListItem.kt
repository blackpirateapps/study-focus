package com.studyfocus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.studyfocus.data.model.TaskWithDetails
import com.studyfocus.ui.theme.Dimens

@Composable
fun TaskListItem(
    taskWithDetails: TaskWithDetails,
    projectColor: Color? = null,
    onTaskClick: () -> Unit,
    onCheckClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskWithDetails.task

    val checkColor by animateColorAsState(
        targetValue = if (task.isCompleted) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        label = "checkColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(Dimens.RadiusMd),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.SpacingLg, vertical = Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(Dimens.CheckboxSize)
                    .clip(CircleShape)
                    .border(
                        width = if (task.isCompleted) 0.dp else 1.5.dp,
                        color = checkColor,
                        shape = CircleShape
                    )
                    .background(
                        color = if (task.isCompleted) checkColor else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable(onClick = onCheckClick),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.SpacingMd))

            // Project color dot
            if (projectColor != null) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(projectColor)
                )
                Spacer(modifier = Modifier.width(Dimens.SpacingSm))
            }

            // Title & metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtask count & pomodoro info
                val metaItems = mutableListOf<String>()
                if (taskWithDetails.subtasks.isNotEmpty()) {
                    val completed = taskWithDetails.subtasks.count { it.isCompleted }
                    metaItems.add("${completed}/${taskWithDetails.subtasks.size} subtasks")
                }
                if (task.pomodoroCount > 0) {
                    metaItems.add("🍅 ${task.completedPomodoros}/${task.pomodoroCount}")
                }
                if (metaItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.SpacingXxs))
                    Text(
                        text = metaItems.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tag chips (first 2)
            taskWithDetails.tags.take(2).forEach { tag ->
                Spacer(modifier = Modifier.width(Dimens.SpacingXs))
                TagChip(tagName = tag.name, colorHex = tag.colorHex)
            }

            // Play button
            if (!task.isCompleted && task.pomodoroCount > 0) {
                Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier.size(Dimens.PlayButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Pomodoro",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconMd)
                    )
                }
            }
        }
    }
}

@Composable
fun TagChip(
    tagName: String,
    colorHex: String,
    modifier: Modifier = Modifier
) {
    val color = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.RadiusFull),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = tagName,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = Dimens.SpacingSm, vertical = Dimens.SpacingXxs)
        )
    }
}
