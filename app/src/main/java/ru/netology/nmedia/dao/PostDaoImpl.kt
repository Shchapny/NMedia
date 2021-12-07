package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {

    companion object {
        const val TABLE_NAME = "posts"
        val DDL = """
        CREATE TABLE $TABLE_NAME (
            ${Column.Id.columnName} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Column.Author.columnName} TEXT NOT NULL,
            ${Column.Content.columnName} TEXT NOT NULL,
            ${Column.Published.columnName} TEXT NOT NULL,
            ${Column.LikedByMe.columnName} BOOLEAN NOT NULL DEFAULT 0,
            ${Column.Likes.columnName} INTEGER NOT NULL DEFAULT 0,
            ${Column.Share.columnName} INTEGER NOT NULL DEFAULT 0
        );    
        """.trimIndent()
    }

    enum class Column(val columnName: String) {
        Id("id"),
        Author("author"),
        Content("content"),
        Published("published"),
        LikedByMe("likedByMe"),
        Likes("likes"),
        Share("share")
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            TABLE_NAME,
            Column.values().map { it.columnName }.toTypedArray(),
            null,
            null,
            null,
            null,
            "${Column.Id.columnName} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(Column.Id.columnName, post.id)
            }
            put(Column.Author.columnName, "Нетология. Университет интернет-профессий будущего")
            put(Column.Content.columnName, post.content)
            put(Column.Published.columnName, formattedDate)
        }
        val id = db.replace(TABLE_NAME, null, values)
        db.query(
            TABLE_NAME,
            Column.values().map { it.columnName }.toTypedArray(),
            "${Column.Id.columnName} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            TABLE_NAME,
            "${Column.Id.columnName} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
                share = share + CASE WHEN share > -1 THEN 1 END
           WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun video() {

    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(Column.Id.columnName)),
                author = getString(getColumnIndexOrThrow(Column.Author.columnName)),
                content = getString(getColumnIndexOrThrow(Column.Content.columnName)),
                published = getString(getColumnIndexOrThrow(Column.Published.columnName)),
                likedByMe = getInt(getColumnIndexOrThrow(Column.LikedByMe.columnName)) != 0,
                likes = getLong(getColumnIndexOrThrow(Column.Likes.columnName)),
                share = getLong(getColumnIndexOrThrow(Column.Share.columnName))
            )
        }
    }

    private val formattedDate = SimpleDateFormat.getDateTimeInstance()!!
        .format(Calendar.getInstance().time!!)!!
}