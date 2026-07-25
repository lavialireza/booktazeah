package com.example.bookapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookapp.data.AppDatabase
import com.example.bookapp.data.seedDatabaseIfEmpty
import com.example.bookapp.ui.screens.GenericListScreen
import com.example.bookapp.ui.screens.ListItemData
import com.example.bookapp.ui.screens.LoginScreen
import com.example.bookapp.ui.screens.TextScreen

private const val ROUTE_LOGIN = "login"
private const val ROUTE_CATEGORIES = "categories"
private const val ROUTE_FIELDS = "fields/{categoryId}/{categoryTitle}"
private const val ROUTE_BOOKS = "books/{fieldId}/{fieldTitle}"
private const val ROUTE_CHAPTERS = "chapters/{bookId}/{bookTitle}"
private const val ROUTE_SECTIONS = "sections/{chapterId}/{chapterTitle}"
private const val ROUTE_TEXT = "text/{sectionId}"

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val navController: NavHostController = rememberNavController()

    // بارگذاری اولیه داده‌ها از assets/sample_data.json در صورت خالی بودن دیتابیس
    LaunchedEffect(Unit) {
        seedDatabaseIfEmpty(context, db)
    }

    NavHost(navController = navController, startDestination = ROUTE_LOGIN) {

        composable(ROUTE_LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(ROUTE_CATEGORIES) {
                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                }
            })
        }

        // سطح ۱: فهرست دسته‌بندی‌ها
        composable(ROUTE_CATEGORIES) {
            var items by remember { mutableStateOf(listOf<ListItemData>()) }
            LaunchedEffect(Unit) {
                items = db.categoryDao().getAll().map { ListItemData(it.id, it.title) }
            }
            GenericListScreen(
                screenTitle = "فهرست",
                items = items,
                onItemClick = { navController.navigate("fields/${it.id}/${it.title}") }
            )
        }

        // سطح ۲: فهرست زمینه‌ها
        composable(ROUTE_FIELDS) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toLongOrNull() ?: 0L
            val categoryTitle = backStackEntry.arguments?.getString("categoryTitle") ?: ""
            var items by remember { mutableStateOf(listOf<ListItemData>()) }
            LaunchedEffect(categoryId) {
                items = db.fieldDao().getByCategory(categoryId).map { ListItemData(it.id, it.title) }
            }
            GenericListScreen(
                screenTitle = categoryTitle,
                items = items,
                onItemClick = { navController.navigate("books/${it.id}/${it.title}") },
                onBack = { navController.popBackStack() }
            )
        }

        // سطح ۳: فهرست کتاب‌ها
        composable(ROUTE_BOOKS) { backStackEntry ->
            val fieldId = backStackEntry.arguments?.getString("fieldId")?.toLongOrNull() ?: 0L
            val fieldTitle = backStackEntry.arguments?.getString("fieldTitle") ?: ""
            var items by remember { mutableStateOf(listOf<ListItemData>()) }
            LaunchedEffect(fieldId) {
                items = db.bookDao().getByField(fieldId).map { ListItemData(it.id, it.title, it.author) }
            }
            GenericListScreen(
                screenTitle = fieldTitle,
                items = items,
                onItemClick = { navController.navigate("chapters/${it.id}/${it.title}") },
                onBack = { navController.popBackStack() }
            )
        }

        // سطح ۴: فهرست فصل‌ها
        composable(ROUTE_CHAPTERS) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull() ?: 0L
            val bookTitle = backStackEntry.arguments?.getString("bookTitle") ?: ""
            var items by remember { mutableStateOf(listOf<ListItemData>()) }
            LaunchedEffect(bookId) {
                items = db.chapterDao().getByBook(bookId).map { ListItemData(it.id, it.title) }
            }
            GenericListScreen(
                screenTitle = bookTitle,
                items = items,
                onItemClick = { navController.navigate("sections/${it.id}/${it.title}") },
                onBack = { navController.popBackStack() }
            )
        }

        // سطح ۵: فهرست بخش‌ها
        composable(ROUTE_SECTIONS) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId")?.toLongOrNull() ?: 0L
            val chapterTitle = backStackEntry.arguments?.getString("chapterTitle") ?: ""
            var items by remember { mutableStateOf(listOf<ListItemData>()) }
            LaunchedEffect(chapterId) {
                items = db.sectionDao().getByChapter(chapterId).map { ListItemData(it.id, it.title) }
            }
            GenericListScreen(
                screenTitle = chapterTitle,
                items = items,
                onItemClick = { navController.navigate("text/${it.id}") },
                onBack = { navController.popBackStack() }
            )
        }

        // سطح ۶: نمایش متن نهایی بخش
        composable(ROUTE_TEXT) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId")?.toLongOrNull() ?: 0L
            var title by remember { mutableStateOf("") }
            var content by remember { mutableStateOf("") }
            LaunchedEffect(sectionId) {
                val section = db.sectionDao().getById(sectionId)
                title = section.title
                content = section.content
            }
            TextScreen(title = title, content = content, onBack = { navController.popBackStack() })
        }
    }
}

