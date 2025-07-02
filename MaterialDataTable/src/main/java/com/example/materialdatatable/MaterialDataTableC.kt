package com.example.materialdatatable

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun MaterialDataTableC(
    headers: List<String>,
    dataLoader: suspend (page: Int, pageSize: Int) -> List<List<String>>?,
    onEdit: (rowIndex: Int) -> Unit,
    onDelete: (rowIndex: Int) -> Unit,
    width: Dp,
    height: Dp
) {
    var currentPage by remember { mutableStateOf(1) }
    var pageSize by remember { mutableStateOf(10) }
    val totalItems = 100
    var isLoading by remember { mutableStateOf(true) }
    var rows by remember { mutableStateOf<List<List<String>>>(emptyList()) }

    val scrollStateHorizontal = rememberScrollState()
    val scrollStateVertical = rememberScrollState()

    LaunchedEffect(currentPage, pageSize) {
        isLoading = true
        rows = dataLoader(currentPage, pageSize) ?: emptyList()
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .width(width)
                .height(height),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollStateVertical)
                    ) {
                        Row(
                            modifier = Modifier.horizontalScroll(scrollStateHorizontal)
                        ) {
                            val totalContentWidth = (headers.size * 150).dp + 150.dp

                            Column(modifier = Modifier.width(totalContentWidth)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Spacer(modifier = Modifier.width(16.dp))

                                    headers.forEach { header ->
                                        Text(
                                            text = header,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .width(150.dp),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(100.dp))
                                }

                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                                rows.forEachIndexed { index, row ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Spacer(modifier = Modifier.width(16.dp))

                                        row.forEach { cell ->
                                            Text(
                                                text = cell,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .width(150.dp),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Row(
                                            modifier = Modifier
                                                .width(150.dp)
                                                .padding(4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(onClick = { onEdit(index) }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                                            }
                                            IconButton(onClick = { onDelete(index) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                                            }
                                        }
                                    }

                                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp,5.dp,10.dp,5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rows:", style = MaterialTheme.typography.bodyMedium)
                            DropdownMenuBox(
                                options = listOf(5, 10, 25, 50),
                                selected = pageSize,
                                onSelectedChange = { pageSize = it },
                                width = 50.dp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val firstItem = (currentPage - 1) * pageSize + 1
                            val lastItem = minOf(currentPage * pageSize, totalItems)
                            val totalPages = (totalItems + pageSize - 1) / pageSize

                            Text("$firstItem–$lastItem of $totalItems", style = MaterialTheme.typography.bodyMedium)

                            IconButton(
                                onClick = { currentPage = 1 },
                                enabled = currentPage > 1
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "First Page")
                            }

                            IconButton(
                                onClick = { if (currentPage > 1) currentPage-- },
                                enabled = currentPage > 1
                            ) {
                                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Page")
                            }

                            IconButton(
                                onClick = { if (currentPage < totalPages) currentPage++ },
                                enabled = currentPage < totalPages
                            ) {
                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Page")
                            }

                            IconButton(
                                onClick = { currentPage = totalPages },
                                enabled = currentPage < totalPages
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Last Page") // You can use another icon if you want
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    options: List<Int>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    width: Dp
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.width(width)
    ) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "$selected",
                    maxLines = 1
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text("$it") },
                    onClick = {
                        onSelectedChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}