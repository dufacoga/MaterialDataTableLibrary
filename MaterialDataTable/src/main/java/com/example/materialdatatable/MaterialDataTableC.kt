package com.example.materialdatatable

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MaterialDataTableC(
    headers: List<String>,
    rows: List<List<String>>,
    isLoading: Boolean,
    currentPage: Int,
    pageSize: Int,
    totalItems: Int,
    onEdit: (rowIndex: Int) -> Unit,
    onDelete: (rowIndex: Int) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit
) {
    val scrollState = rememberScrollState()
    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(verticalScroll)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        headers.forEach { header ->
                            Text(
                                text = header,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .widthIn(min = 120.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(100.dp))
                    }

                    HorizontalDivider()

                    rows.forEachIndexed { index, row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            row.forEach { cell ->
                                Text(
                                    text = cell,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .widthIn(min = 120.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier.width(100.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(onClick = { onEdit(index) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { onDelete(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousPage,
                    enabled = currentPage > 1
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Page")
                }

                Text(
                    text = "Page $currentPage",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                IconButton(
                    onClick = onNextPage,
                    enabled = currentPage * pageSize < totalItems
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Page")
                }
            }
        }
    }
}