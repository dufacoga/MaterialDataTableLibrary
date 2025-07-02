package com.example.materialdatatablelibrary

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.materialdatatable.MaterialDataTableC
import kotlinx.coroutines.delay

@Composable
fun MaterialDataTableTestScreen() {
    val headers = listOf("ID", "Name", "Role", "Email")

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width: Dp = maxWidth * 0.9f
        val height: Dp = maxHeight * 0.65f

        MaterialDataTableC(
            headers = headers,
            dataLoader = { page, pageSize ->
                delay(500)
                (1..pageSize).map { i ->
                    val id = ((page - 1) * pageSize + i).toString()
                    listOf(
                        id,
                        "User $id",
                        if (id.toInt() % 2 == 0) "Admin" else "User",
                        "user$id@example.com"
                    )
                }
            },
            onEdit = { rowIndex -> println("Edit row at index $rowIndex") },
            onDelete = { rowIndex -> println("Delete row at index $rowIndex") },
            width = width,
            height = height
        )
    }
}