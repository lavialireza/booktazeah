package com.example.bookapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// سطح ۱: فهرست اصلی (دسته‌بندی کلی)
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String
)

// سطح ۲: زمینه‌ها (هر زمینه متعلق به یک دسته‌بندی است)
@Entity(
    tableName = "fields",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FieldEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val title: String
)

// سطح ۳: کتاب‌ها (هر کتاب متعلق به یک زمینه است)
@Entity(
    tableName = "books",
    foreignKeys = [ForeignKey(
        entity = FieldEntity::class,
        parentColumns = ["id"],
        childColumns = ["fieldId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fieldId: Long,
    val title: String,
    val author: String? = null
)

// سطح ۴: فصل‌ها (هر فصل متعلق به یک کتاب است)
@Entity(
    tableName = "chapters",
    foreignKeys = [ForeignKey(
        entity = BookEntity::class,
        parentColumns = ["id"],
        childColumns = ["bookId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val orderIndex: Int,
    val title: String
)

// سطح ۵: بخش‌ها (هر بخش متعلق به یک فصل است)
@Entity(
    tableName = "sections",
    foreignKeys = [ForeignKey(
        entity = ChapterEntity::class,
        parentColumns = ["id"],
        childColumns = ["chapterId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapterId: Long,
    val orderIndex: Int,
    val title: String,
    val content: String // متن کامل بخش (خروجی از فایل ورد)
)
