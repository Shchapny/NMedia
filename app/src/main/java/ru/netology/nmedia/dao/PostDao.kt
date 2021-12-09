package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content =:content WHERE id =:postId")
    fun updateContentById(postId: Long, content: String)

    fun save(post: PostEntity) {
        if (post.id == 0L) insert(post) else updateContentById(
            postId = post.id,
            content = post.content
        )
    }

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END 
        WHERE id =:id;
    """
    )
    fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id =:id")
    fun removeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        share = share + 1
        WHERE id =:id
    """
    )
    fun shareById(id: Long)

    fun video() {

    }
}