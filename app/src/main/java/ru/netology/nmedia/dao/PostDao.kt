package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content =:content WHERE id =:postId")
    suspend fun updateContentById(postId: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) {
            insert(post)
        } else {
            updateContentById(post.id, post.content)
            0L
        }

    @Query("DELETE FROM PostEntity WHERE id =:id")
    suspend fun removeById(id: Long)

    @Query("UPDATE PostEntity SET share = share + 1 WHERE id =:id")
    fun shareById(id: Long)

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END 
        WHERE id =:id;
    """
    )
    suspend fun likeById(id: Long)
}