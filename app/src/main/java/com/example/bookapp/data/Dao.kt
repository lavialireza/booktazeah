package com.example.bookapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id")
    suspend fun getAll(): List<CategoryEntity>

    @Insert
    suspend fun insert(category: CategoryEntity): Long
}

@Dao
interface FieldDao {
    @Query("SELECT * FROM fields WHERE categoryId = :categoryId ORDER BY id")
    suspend fun getByCategory(categoryId: Long): List<FieldEntity>

    @Insert
    suspend fun insert(field: FieldEntity): Long
}

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE fieldId = :fieldId ORDER BY id")
    suspend fun getByField(fieldId: Long): List<BookEntity>

    @Insert
    suspend fun insert(book: BookEntity): Long
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY orderIndex")
    suspend fun getByBook(bookId: Long): List<ChapterEntity>

    @Insert
    suspend fun insert(chapter: ChapterEntity): Long
}

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections WHERE chapterId = :chapterId ORDER BY orderIndex")
    suspend fun getByChapter(chapterId: Long): List<SectionEntity>

    @Query("SELECT * FROM sections WHERE id = :sectionId")
    suspend fun getById(sectionId: Long): SectionEntity

    @Insert
    suspend fun insert(section: SectionEntity): Long
}
