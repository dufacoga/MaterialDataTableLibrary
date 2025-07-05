package com.example.materialdatatablelibrary

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.materialdatatable.MaterialDataTableC
import com.example.materialdatatable.dataLoaderFromListWithDelay
import kotlinx.coroutines.delay

@Composable
fun MaterialDataTableTestScreen() {
    val headers = listOf("ID", "Name", "Role", "Email")

    val parentState = rememberLazyListState()
    val childState  = rememberLazyListState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val width: Dp = screenWidth * 0.90f
    val height: Dp = if (isLandscape) {
        screenHeight * 1.5f
    } else {
        screenHeight * 0.80f
    }

    LazyColumn(
        state = parentState,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, screenHeight * 0.10f, 0.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            val dataList: List<List<String>> = (1..100).map { i ->
                val id = i.toString()
                listOf(
                    id,
                    "Douglas Cortes $id",
                    if (id.toInt() % 2 == 0) "Admin" else "User",
                    "user$id@example.com"
                )
            }
            val loader = dataLoaderFromListWithDelay(
                sourceProvider = { dataList },
                rowMapper = { it }
            )

            MaterialDataTableC(
                headers = headers,
                dataLoader = loader,
                onEdit = { rowIndex -> println("Edit row at index $rowIndex") },
                onDelete = { rowIndex -> println("Delete row at index $rowIndex") },
                onMoreVert = { rowIndex -> println("MoreVert row at index $rowIndex") },
                columnSizeAdaptive = true,
                columnWidth = 150.dp,
                editOption = true,
                deleteOption = true,
                horizontalDividers = true,
                verticalDividers = true,
                childState = childState,
                width = width,
                height = height,
                totalItems = dataList.size
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.10f))
        }
    }
}