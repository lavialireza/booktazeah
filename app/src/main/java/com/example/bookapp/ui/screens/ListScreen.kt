package com.example.bookapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ListItemData(val id: Long, val title: String, val subtitle: String? = null)

/**
 * صفحه فهرست عمومی: برای دسته‌بندی‌ها، زمینه‌ها، کتاب‌ها، فصل‌ها و بخش‌ها
 * استفاده می‌شود. فقط عنوان صفحه و لیست آیتم‌ها فرق می‌کند.
 */
@Composable
fun GenericListScreen(
    screenTitle: String,
    items: List<ListItemData>,
    onItemClick: (ListItemData) -> Unit,
    onBack: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("موردی برای نمایش وجود ندارد")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(items) { item ->
                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = item.subtitle?.let { { Text(it) } },
                        modifier = Modifier.clickable { onItemClick(item) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
