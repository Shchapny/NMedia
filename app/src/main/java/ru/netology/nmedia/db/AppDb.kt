package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.enumeration.AttachmentType

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao

}