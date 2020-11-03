package com.app.tinkoff_fintech.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAll(): Single<List<Post>>
    @Query("SELECT * FROM post")
    fun getAllW(): List<Post>

    @Query("SELECT count(*) FROM post")
    fun count(): Int

    @Query("SELECT * FROM post WHERE id = :id")
    fun findById(id: Int): Single<Post>

    @Insert
    fun insertAll(posts: List<Post>) : Completable

    @Delete
    fun delete(post: Post)

    @Query("DELETE FROM post")
    fun deleteAll() : Completable

}