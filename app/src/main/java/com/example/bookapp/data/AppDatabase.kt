package com.example.bookapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.json.JSONArray
import org.json.JSONObject

@Database(
    entities = [
        CategoryEntity::class,
        FieldEntity::class,
        BookEntity::class,
        ChapterEntity::class,
        SectionEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun fieldDao(): FieldDao
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
    abstract fun sectionDao(): SectionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookapp.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * داده‌های نمونه/واقعی را از فایل assets/sample_data.json می‌خواند
 * و در صورتی که دیتابیس خالی باشد، آن‌ها را وارد می‌کند.
 * ساختار JSON باید خروجی اسکریپت scripts/docx_to_json.py باشد.
 */
suspend fun seedDatabaseIfEmpty(context: Context, db: AppDatabase) {
    if (db.categoryDao().getAll().isNotEmpty()) return

    val jsonText = context.assets.open("sample_data.json")
        .bufferedReader(Charsets.UTF_8).use { it.readText() }
    val categories = JSONArray(jsonText)

    for (i in 0 until categories.length()) {
        val catObj = categories.getJSONObject(i)
        val catId = db.categoryDao().insert(CategoryEntity(title = catObj.getString("title")))

        val fields = catObj.getJSONArray("fields")
        for (fi in 0 until fields.length()) {
            val fieldObj = fields.getJSONObject(fi)
            val fieldId = db.fieldDao().insert(
                FieldEntity(categoryId = catId, title = fieldObj.getString("title"))
            )

            val books = fieldObj.getJSONArray("books")
            for (bi in 0 until books.length()) {
                val bookObj = books.getJSONObject(bi)
                val bookId = db.bookDao().insert(
                    BookEntity(
                        fieldId = fieldId,
                        title = bookObj.getString("title"),
                        author = bookObj.optString("author", null)
                    )
                )

                val chapters = bookObj.getJSONArray("chapters")
                for (ci in 0 until chapters.length()) {
                    val chapObj: JSONObject = chapters.getJSONObject(ci)
                    val chapterId = db.chapterDao().insert(
                        ChapterEntity(
                            bookId = bookId,
                            orderIndex = ci,
                            title = chapObj.getString("title")
                        )
                    )

                    val sections = chapObj.getJSONArray("sections")
                    for (si in 0 until sections.length()) {
                        val secObj = sections.getJSONObject(si)
                        db.sectionDao().insert(
                            SectionEntity(
                                chapterId = chapterId,
                                orderIndex = si,
                                title = secObj.getString("title"),
                                content = secObj.getString("content")
                            )
                        )
                    }
                }
            }
        }
    }
}
