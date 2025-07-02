package com.example.materialdatatablelibrary

import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.example.materialdatatable.MaterialDataTableC
import kotlinx.coroutines.delay

@Composable
fun MaterialDataTableTestScreen() {
    var currentPage by remember { mutableStateOf(1) }
    val pageSize = 10
    val totalItems = 20

    var isLoading by remember { mutableStateOf(true) }
    var rows by remember { mutableStateOf(emptyList<List<String>>()) }

    val headers = listOf("ID", "Name", "Role", "Email")

    // Simulate loading with delay
    LaunchedEffect(currentPage) {
        isLoading = true
        delay(1000) // simulate loading time
        rows = (1..pageSize).map { i ->
            val id = ((currentPage - 1) * pageSize + i).toString()
            listOf(
                id,
                "User $id",
                if (id.toInt() % 2 == 0) "Admin" else "User",
                "user$id@example.com"
            )
        }
        isLoading = false
    }

    MaterialDataTableC(
        headers = headers,
        rows = rows,
        isLoading = isLoading,
        currentPage = currentPage,
        pageSize = pageSize,
        totalItems = totalItems,
        onEdit = { rowIndex ->
            println("Edit row at index $rowIndex")
        },
        onDelete = { rowIndex ->
            println("Delete row at index $rowIndex")
        },
        onNextPage = {
            if (currentPage * pageSize < totalItems) currentPage++
        },
        onPreviousPage = {
            if (currentPage > 1) currentPage--
        }
    )
}