package com.example.materialdatatable

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource


@Composable
fun <T> MaterialDataTableC(
    headers: List<String>,
    dataLoader: suspend (page: Int, pageSize: Int) -> List<T>?,
    rowMapper: (T) -> List<String>,
    onEdit: (item: T) -> Unit,
    onDelete: (item: T) -> Unit,
    onMore: (item: T) -> Unit,
    columnSizeAdaptive: Boolean,
    columnWidth: Dp,
    moreOption: Boolean,
    editOption: Boolean,
    deleteOption: Boolean,
    horizontalDividers: Boolean,
    verticalDividers: Boolean,
    paginationRowFixed: Boolean,
    childState: LazyListState,
    width: Dp,
    height: Dp,
    totalItems: Int
) {
    var currentPage by remember { mutableIntStateOf(1) }
    var pageSize by remember { mutableIntStateOf(10) }
    var isLoading by remember { mutableStateOf(true) }
    var rows by remember { mutableStateOf<List<T>>(emptyList()) }
    var maxColumnLengths: List<Int>? by remember { mutableStateOf(null) }
    val spacerWidth = 16.dp

    var sortColumn by remember { mutableStateOf<Int?>(null) }
    var sortMode by remember { mutableStateOf(SortMode.Asc) }
    var allItemsCache by remember { mutableStateOf<List<T>>(emptyList()) }
    var allItemsLoaded by remember { mutableStateOf(false) }

    val scrollStateHorizontal = rememberScrollState()

    val optionColumnWidth = remember(editOption, deleteOption, moreOption) {
        val optionsCount = listOf(editOption, deleteOption, moreOption).count { it }
        when (optionsCount) {
            1 -> 88.dp
            2 -> 136.dp
            3 -> 184.dp
            else -> 32.dp
        }
    }

    val hasOptionColumn = editOption || deleteOption || moreOption

    suspend fun loadAllItems(): List<T> {
        if (allItemsLoaded && allItemsCache.isNotEmpty()) return allItemsCache
        val pages = (totalItems + pageSize - 1) / pageSize
        val acc = mutableListOf<T>()
        for (p in 1..pages) {
            val chunk = dataLoader(p, pageSize) ?: emptyList()
            acc += chunk
        }
        allItemsCache = acc
        allItemsLoaded = true
        return acc
    }

    fun naturalCompare(a: String, b: String): Int {
        val re = Regex("\\d+|\\D+")
        val ta = re.findAll(a).map { it.value }.toList()
        val tb = re.findAll(b).map { it.value }.toList()
        val n = minOf(ta.size, tb.size)

        for (i in 0 until n) {
            val sa = ta[i]
            val sb = tb[i]
            val numA = sa.first().isDigit()
            val numB = sb.first().isDigit()

            val cmp = if (numA && numB) {
                val za = sa.trimStart('0')
                val zb = sb.trimStart('0')
                when {
                    za.length != zb.length -> za.length.compareTo(zb.length)
                    else -> {
                        val r = za.compareTo(zb)
                        if (r != 0) r else sa.length.compareTo(sb.length)
                    }
                }
            } else {
                sa.compareTo(sb, ignoreCase = true)
            }
            if (cmp != 0) return cmp
        }
        return ta.size.compareTo(tb.size)
    }

    LaunchedEffect(currentPage, pageSize, sortColumn, sortMode) {

        isLoading = true
        val doSort = sortColumn != null && sortMode != SortMode.Neutral

        if (!doSort) {
            rows = dataLoader(currentPage, pageSize) ?: emptyList()
        } else {
            val all = loadAllItems()
            val idx = sortColumn!!
            val sorted = all.sortedWith { t1, t2 ->
                val c1 = rowMapper(t1).getOrNull(idx) ?: ""
                val c2 = rowMapper(t2).getOrNull(idx) ?: ""
                val base = naturalCompare(c1, c2)
                if (sortMode == SortMode.Asc) base else -base
            }
            val from = ((currentPage - 1) * pageSize).coerceAtLeast(0)
            val to = (from + pageSize).coerceAtMost(sorted.size)
            rows = if (from < to) sorted.subList(from, to) else emptyList()
        }
        isLoading = false
    }

    LaunchedEffect(headers, totalItems, columnSizeAdaptive) {
        if (columnSizeAdaptive && maxColumnLengths == null) {
            maxColumnLengths = calculateMaxColumnLengths(
                headers = headers,
                dataLoader = dataLoader,
                rowMapper = rowMapper,
                totalItems = totalItems,
                pageSize = pageSize
            )
        } else if (!columnSizeAdaptive) {
            maxColumnLengths = null
        }
    }

    val characterToDpFactor = 8f
    val internalPaddingDp = 8f
    val dividerThicknessDp = 1f
    val sortButtonDp = 34f

    val currentMaxColumnLengths = maxColumnLengths

    val totalContentWidth: Float = remember(currentMaxColumnLengths, headers.size, optionColumnWidth, verticalDividers, columnSizeAdaptive, columnWidth) {
        if (columnSizeAdaptive && !currentMaxColumnLengths.isNullOrEmpty()) {
            var calculatedWidth = 0f
            currentMaxColumnLengths.forEachIndexed { index, length ->
                val baseWidth = (length * characterToDpFactor) + internalPaddingDp
                val minForSort = (headers[index].length * characterToDpFactor) + sortButtonDp
                val effective = maxOf(baseWidth, minForSort)
                calculatedWidth += (effective + spacerWidth.value)
                if (verticalDividers && (index < headers.size - 1 || (editOption || deleteOption || moreOption))) {
                    calculatedWidth += dividerThicknessDp
                }
            }
            calculatedWidth += optionColumnWidth.value
            calculatedWidth
        } else {
            var calculatedWidth = 0f
            headers.forEachIndexed { index, _ ->
                val minForSort = (headers[index].length * characterToDpFactor) + sortButtonDp
                val effective = maxOf(columnWidth.value, minForSort)

                calculatedWidth += (effective + spacerWidth.value)
                if (verticalDividers && (index < headers.size - 1 || (editOption || deleteOption || moreOption))) {
                    calculatedWidth += dividerThicknessDp
                }
            }
            calculatedWidth += optionColumnWidth.value
            calculatedWidth
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
                                                Row(
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .width(colWidthDp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = header,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        modifier = Modifier.weight(1f, fill = false)
                                                    )

                                                    val isActive = sortColumn == index

                                                    val nextMode = if (isActive) {
                                                        when (sortMode) {
                                                            SortMode.Asc     -> SortMode.Desc
                                                            SortMode.Desc    -> SortMode.Neutral
                                                            SortMode.Neutral -> SortMode.Asc
                                                        }
                                                    } else {
                                                        SortMode.Asc
                                                    }

                                                    val painter = when (nextMode) {
                                                        SortMode.Asc     -> painterResource(R.drawable.ic_remix_sort_asc)
                                                        SortMode.Desc    -> painterResource(R.drawable.ic_remix_sort_desc)
                                                        SortMode.Neutral -> painterResource(R.drawable.ic_remix_sort_ntrl)
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            if (sortColumn == index) {
                                                                sortMode = when (sortMode) {
                                                                    SortMode.Asc     -> SortMode.Desc
                                                                    SortMode.Desc    -> {
                                                                        sortColumn = null
                                                                        SortMode.Neutral
                                                                    }
                                                                    SortMode.Neutral -> SortMode.Asc
                                                                }
                                                            } else {
                                                                sortColumn = index
                                                                sortMode = SortMode.Asc
                                                            }
                                                            currentPage = 1
                                                        }, modifier = Modifier.size(26.dp).padding(start = 8.dp)
                                                    ) {
                                                        Icon(
                                                            painter = painter,
                                                            contentDescription = when {
                                                                sortColumn == null -> "Orden descendente"
                                                                isActive && sortMode == SortMode.Desc -> "Orden descendente"
                                                                isActive && sortMode == SortMode.Asc  -> "Orden ascendente"
                                                                isActive && sortMode == SortMode.Neutral -> "Orden neutral"
                                                                else -> "Orden descendente"
                                                            }
                                                        )
                                                    }
                                                }
                                                if (verticalDividers && (index < headers.size - 1 || hasOptionColumn)) {
                                                    VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                                }
                                            }
                                        }

                                        if (horizontalDividers) {
                                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                        }

                                        rows.forEach { item ->
                                            val rowData = rowMapper(item)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(IntrinsicSize.Min)
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                rowData.forEachIndexed { colIndex, cell ->
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
                                                            .padding(4.dp,14.dp)
                                                            .width(colWidthDp),
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    if (verticalDividers && (colIndex < headers.size - 1 || hasOptionColumn)) {
                                                        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                                    }
                                                }
                                                Row(
                                                    modifier = Modifier
                                                        .width(optionColumnWidth)
                                                        .padding(4.dp,0.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (moreOption){
                                                        IconButton(onClick = { onMore(item) }) {
                                                            Icon(
                                                                painter = painterResource(id = R.drawable.ic_remix_more),
                                                                contentDescription = stringResource(R.string.label_more_options)
                                                            )
                                                        }
                                                    }
                                                    if (editOption){
                                                        IconButton(onClick = { onEdit(item) }) {
                                                            Icon(
                                                                painter = painterResource(id = R.drawable.ic_remix_edit),
                                                                contentDescription = stringResource(R.string.label_edit)
                                                            )
                                                        }
                                                    }
                                                    if (deleteOption){
                                                        IconButton(onClick = { onDelete(item) }) {
                                                            Icon(
                                                                painter = painterResource(id = R.drawable.ic_remix_delete),
                                                                contentDescription = stringResource(R.string.label_delete)
                                                            )
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

                        if (!paginationRowFixed) {
                            item {
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
                                        Text(stringResource(R.string.label_rows), style = MaterialTheme.typography.bodyMedium)
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
                                        val stringOf = stringResource(R.string.label_of)

                                        Text("$firstItem–$lastItem $stringOf $totalItems", style = MaterialTheme.typography.bodyMedium)

                                        IconButton(
                                            onClick = { currentPage = 1 },
                                            enabled = currentPage > 1
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_remix_skip_left),
                                                contentDescription = stringResource(R.string.label_first_page)
                                            )
                                        }

                                        IconButton(
                                            onClick = { if (currentPage > 1) currentPage-- },
                                            enabled = currentPage > 1
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_remix_arrow_left),
                                                contentDescription = stringResource(R.string.label_previous_page)
                                            )
                                        }

                                        IconButton(
                                            onClick = { if (currentPage < totalPages) currentPage++ },
                                            enabled = currentPage < totalPages
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_remix_arrow_right),
                                                contentDescription = stringResource(R.string.label_next_page)
                                            )
                                        }

                                        IconButton(
                                            onClick = { currentPage = totalPages },
                                            enabled = currentPage < totalPages
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_remix_skip_right),
                                                contentDescription = stringResource(R.string.label_last_page)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (paginationRowFixed) {
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
                                Text(stringResource(R.string.label_rows), style = MaterialTheme.typography.bodyMedium)
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
                                val stringOf = stringResource(R.string.label_of)

                                Text("$firstItem–$lastItem $stringOf $totalItems", style = MaterialTheme.typography.bodyMedium)

                                IconButton(
                                    onClick = { currentPage = 1 },
                                    enabled = currentPage > 1
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_remix_skip_left),
                                        contentDescription = stringResource(R.string.label_first_page)
                                    )
                                }

                                IconButton(
                                    onClick = { if (currentPage > 1) currentPage-- },
                                    enabled = currentPage > 1
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_remix_arrow_left),
                                        contentDescription = stringResource(R.string.label_previous_page)
                                    )
                                }

                                IconButton(
                                    onClick = { if (currentPage < totalPages) currentPage++ },
                                    enabled = currentPage < totalPages
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_remix_arrow_right),
                                        contentDescription = stringResource(R.string.label_next_page)
                                    )
                                }

                                IconButton(
                                    onClick = { currentPage = totalPages },
                                    enabled = currentPage < totalPages
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_remix_skip_right),
                                        contentDescription = stringResource(R.string.label_last_page)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class SortMode { Desc, Asc, Neutral }

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
fun <T> dataLoaderFromList(
    sourceProvider: () -> List<T>,
): suspend (Int, Int) -> List<T> = { page, pageSize ->
    val sourceList = sourceProvider()
    val fromIndex = (page - 1) * pageSize
    val toIndex = (fromIndex + pageSize).coerceAtMost(sourceList.size)

    if (fromIndex >= sourceList.size) emptyList()
    else sourceList.subList(fromIndex, toIndex)
}

suspend fun <T> calculateMaxColumnLengths(
    headers: List<String>,
    dataLoader: suspend (page: Int, pageSize: Int) -> List<T>?,
    rowMapper: (T) -> List<String>,
    totalItems: Int,
    pageSize: Int,
    sortButtonDp: Float = 34f,
    characterToDpFactor: Float = 8f
): List<Int> {
    val extraCharsForSort = kotlin.math.ceil(sortButtonDp / characterToDpFactor).toInt()

    val maxLengths = MutableList(headers.size) { index ->
        headers[index].length + extraCharsForSort
    }

    val totalPages = (totalItems + pageSize - 1) / pageSize

    for (page in 1..totalPages) {
        val currentPageItems = dataLoader(page, pageSize)
        currentPageItems?.forEach { item ->
            val row = rowMapper(item)
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