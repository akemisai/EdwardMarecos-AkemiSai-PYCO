package com.pyco.app.screens.home.components.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor

@Composable
fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        filters.forEach { filter ->
            FilterChipItem(
                text = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(text = text)
        },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = customColor,
            selectedLabelColor = Color.White,
            containerColor = Color.LightGray,
            labelColor = Color.Black
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RequestsFilterSection(
    filters: List<String>,
    selectedFilters: List<String>,
    onFilterRemoved: (String) -> Unit,
    onClearAll: () -> Unit,
    onShowTagsPopup: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Tags" filter chip (unselected state just triggers popup)
            FilterChip(
                elevation = FilterChipDefaults.filterChipElevation(
                    elevation = 6.dp
                ),
                modifier = Modifier
                    .height(26.dp),
                selected = false,
                onClick = { onShowTagsPopup() },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.leading_icon),
                            contentDescription = "Tags",
                            tint = customColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tags",
                            color = customColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = backgroundColor,
                    labelColor = customColor
                ),
                shape = RoundedCornerShape(8.dp)
            )
            // Clear All
            if (selectedFilters.isNotEmpty()) {
                Text(
                    text = "Clear all",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable { onClearAll() },
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 12.sp
                )
            }
        }
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Display Selected Chips
            selectedFilters.forEach { filter ->
                FilterChipWithClose(
                    text = filter,
                    onRemove = { onFilterRemoved(filter) }
                )
            }
        }
    }
}

@Composable
fun FilterChipWithClose(
    text: String,
    onRemove: () -> Unit
) {

    FilterChip(
        elevation = FilterChipDefaults.filterChipElevation(
            elevation = 6.dp
        ),
        modifier = Modifier
            .height(26.dp)
            .padding(3.dp),
        selected = true,
        onClick = { onRemove() }, // Entire chip click removes filter, simpler than focusing on x
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    color = Color(0xff6A6A6A),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.tag_out),
                    contentDescription = "Remove",
                    tint = Color(0xff6A6A6A),
                    modifier = Modifier.size(14.dp)
                )
            }
        },
        shape = RoundedCornerShape(6.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = customColor,
            selectedLabelColor = Color.Gray,
            containerColor = customColor,
            labelColor = Color.Gray
        )
    )
}

@Composable
fun TagsPopupDialog(
    availableTags: List<String>,
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Tags",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                availableTags.forEach { tag ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTagSelected(tag) }
                            .padding(8.dp)
                    ) {
                        Text(text = tag, style = MaterialTheme.typography.bodyMedium)
                        if (selectedTags.contains(tag)) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = customColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}