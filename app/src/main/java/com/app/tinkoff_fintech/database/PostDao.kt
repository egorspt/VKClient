package com.app.tinkoff_fintech.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.tinkoff_fintech.models.Post
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAll(): Single<List<Post>>

    @Query("SELECT * FROM post LIMIT 10 OFFSET :offset")
    fun getAll(offset: Int): Single<List<Post>>

    @Query("SELECT * FROM post WHERE isLiked = 1")
    fun getFavorites(): LiveData<List<Post>>

    @Query("SELECT * FROM post WHERE isLiked = 0")
    fun getNotFavorites(): LiveData<List<Post>>

    @Query("UPDATE post SET isLiked = :isLiked, countLikes = CASE WHEN :isLiked = 1 then countLikes + 1 ELSE countLikes - 1 END WHERE id = :id")
    fun updateLike(id: Int, isLiked: Int): Completable

    @Query("SELECT * FROM post WHERE id = :id")
    fun find(id: Int): Single<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<Post>): Completable

    @Query("delete from post WHERE id = :id")
    fun delete(id: Int): Completable

    @Query("DELETE FROM post")
    fun deleteAll() : Completable

    @Query("SELECT count(*) from post")
    fun count() : Int



    @Query("SELECT * FROM post")
    fun getAllN(): List<Post>

    @Query("SELECT * FROM post WHERE isLiked = 1")
    fun getFavoritesN(): List<Post>
}