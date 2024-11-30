package com.pyco.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pyco.app.R

@Composable
fun HomeTopSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Name
            Text(
                text = "PYCO",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = customColor
            )

            // Chat and Notification Icons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mis_chat), // chat icon
                    contentDescription = "Chat",
                    tint = customColor,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 2.dp)
                        .padding(end = 4.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.mis_alert), // notification icon
                    contentDescription = "Notification",
                    tint = customColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Request Tabs
        TabRow(
            selectedTabIndex = 0, // This should be dynamic based on which tab is selected
            containerColor = backgroundColor,
            contentColor = customColor
        ) {
            Tab(
                selected = true,
                onClick = { /*TODO: Update selected tab*/ },
                text = { Text("Requests") }
            )
            Tab(
                selected = false,
                onClick = { /*TODO: Update selected tab*/ },
                text = { Text("Top Outfits") }
            )
            Tab(
                selected = false,
                onClick = { /*TODO: Update selected tab*/ },
                text = { Text("Responses") }
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone",
    backgroundColor = 0xFF1F2123
)
@Composable
fun HomeTopSectionPreview() {
    HomeTopSection(
    )
}
