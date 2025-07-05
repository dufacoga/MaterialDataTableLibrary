package com.example.materialdatatable

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun MaterialDataTableC(
    headers: List<String>,
    dataLoader: suspend (page: Int, pageSize: Int) -> List<List<String>>?,
    onEdit: (rowIndex: Int) -> Unit,
    onDelete: (rowIndex: Int) -> Unit,
    columnSizeAdaptive: Boolean,
    columnWidth: Dp,
    editOption: Boolean,
    deleteOption: Boolean,
    horizontalDividers: Boolean,
    verticalDividers: Boolean,
    childState: LazyListState,
    width: Dp,
    height: Dp,
    totalItems: Int
) {
    var currentPage by remember { mutableStateOf(1) }
    var pageSize by remember { mutableStateOf(10) }
    var isLoading by remember { mutableStateOf(true) }
    var rows by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var maxColumnLengths: List<Int>? by remember { mutableStateOf(null) }
    var spacerWidth = 16.dp

    val scrollStateHorizontal = rememberScrollState()

    val optionColumnWidth = remember(editOption, deleteOption) {
        when {
            editOption && deleteOption -> 140.dp
            editOption || deleteOption -> 100.dp
            else -> 0.dp
        }
    }

    LaunchedEffect(currentPage, pageSize) {
        isLoading = true
        rows = dataLoader(currentPage, pageSize) ?: emptyList()
        isLoading = false
    }

    LaunchedEffect(headers, totalItems, columnSizeAdaptive) {
        if (columnSizeAdaptive && maxColumnLengths == null) {
            maxColumnLengths = calculateMaxColumnLengths(
                headers = headers,
                dataLoader = dataLoader,
                totalItems = totalItems,
                pageSize = pageSize
            )
        } else if (!columnSizeAdaptive) {
            maxColumnLengths = null
        }
    }

    val characterToDpFactor = 8f
    val internalPaddingDp = 8f // 4.dp padding on each side for Text
    val dividerThicknessDp = 1f

    val currentMaxColumnLengths = maxColumnLengths

    val totalContentWidth: Float = remember(currentMaxColumnLengths, headers.size, optionColumnWidth, verticalDividers, columnSizeAdaptive, columnWidth) {
        if (columnSizeAdaptive && currentMaxColumnLengths != null && currentMaxColumnLengths.isNotEmpty()) {
            var calculatedWidth = 0f
            currentMaxColumnLengths.forEachIndexed { index, length ->
                val columnTextWidthFloat = length * characterToDpFactor
                calculatedWidth += (columnTextWidthFloat + internalPaddingDp + spacerWidth.value)
                if (verticalDividers && (index < headers.size - 1 || (editOption || deleteOption))) {
                    calculatedWidth += dividerThicknessDp
                }
            }
            calculatedWidth += optionColumnWidth.value
            calculatedWidth
        } else {
            (headers.size * (columnWidth.value + spacerWidth.value)) +
                    (if (verticalDividers && headers.size > 0) (headers.size - 1) * dividerThicknessDp else 0f) +
                    optionColumnWidth.value
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (isLoading) {
                    Box(
                        Modifier
                            .height(height)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        state = childState,
                        modifier = Modifier
                            .height(height)
                            .fillMaxWidth()
                            .nestedScroll(
                                rememberNestedScrollInteropConnection()
                            )
                    ){
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                Row(
                                    modifier = Modifier.horizontalScroll(scrollStateHorizontal)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .width(totalContentWidth.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(IntrinsicSize.Min)
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            headers.forEachIndexed { index, header ->
                                                val colWidthDp =
                                                    if (columnSizeAdaptive && currentMaxColumnLengths != null) {
                                                        ((currentMaxColumnLengths.getOrNull(index)?.toFloat() ?: 0f) * characterToDpFactor + internalPaddingDp).dp
                                                    } else {
                                                        columnWidth
                                                    }
                                                Spacer(modifier = Modifier.width(spacerWidth))
                                                Text(
                                                    text = header,
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .width(colWidthDp),
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                                if (verticalDividers) {
                                                    VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                                }
                                            }
                                        }

                                        if (horizontalDividers) {
                                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                        }

                                        rows.forEachIndexed { rowIndex, row ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(IntrinsicSize.Min)
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                row.forEachIndexed { colIndex, cell ->
                                                    val colWidthDp =
                                                        if (columnSizeAdaptive && currentMaxColumnLengths != null) {
                                                            ((currentMaxColumnLengths.getOrNull(colIndex)?.toFloat() ?: 0f) * characterToDpFactor + internalPaddingDp).dp
                                                        } else {
                                                            columnWidth
                                                        }
                                                    Spacer(modifier = Modifier.width(spacerWidth))
                                                    Text(
                                                        text = cell,
                                                        modifier = Modifier
                                                            .padding(4.dp)
                                                            .width(colWidthDp),
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    if (verticalDividers) {
                                                        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                                    }
                                                }
                                                if (editOption || deleteOption) {
                                                    Row(
                                                        modifier = Modifier
                                                            .width(optionColumnWidth)
                                                            .padding(4.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (editOption){
                                                            IconButton(onClick = { onEdit(rowIndex) }) {
                                                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                                                            }
                                                        }
                                                        if (deleteOption){
                                                            IconButton(onClick = { onDelete(rowIndex) }) {
                                                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (horizontalDividers) {
                                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (horizontalDividers) {
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }

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
                                Icon(Icons.Default.ArrowForward, contentDescription = "Last Page")
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

@Composable
fun <T> dataLoaderFromListWithDelay(
    sourceProvider: () -> List<T>,
    rowMapper: (T) -> List<String>
): suspend (Int, Int) -> List<List<String>> = { page, pageSize ->
    while (sourceProvider().isEmpty()) {
        delay(50)
    }

    val sourceList = sourceProvider()
    val fromIndex = (page - 1) * pageSize
    val toIndex = (fromIndex + pageSize).coerceAtMost(sourceList.size)

    if (fromIndex >= sourceList.size) emptyList()
    else sourceList.subList(fromIndex, toIndex).map(rowMapper)
}

suspend fun calculateMaxColumnLengths(
    headers: List<String>,
    dataLoader: suspend (page: Int, pageSize: Int) -> List<List<String>>?,
    totalItems: Int,
    pageSize: Int
): List<Int> {
    val maxLengths = MutableList(headers.size) { index -> headers[index].length }

    val totalPages = (totalItems + pageSize - 1) / pageSize

    for (page in 1..totalPages) {
        val currentPageData = dataLoader(page, pageSize)

        currentPageData?.forEach { row ->
            row.forEachIndexed { colIndex, cell ->
                if (colIndex < maxLengths.size) {
                    if (cell.length > maxLengths[colIndex]) {
                        maxLengths[colIndex] = cell.length
                    }
                }
            }
        }
    }
    return maxLengths
}