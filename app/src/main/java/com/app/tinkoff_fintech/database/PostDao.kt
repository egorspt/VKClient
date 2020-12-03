package com.app.tinkoff_fintech.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAll(): Single<List<Post>>
    @Query("SELECT * FROM post")
    fun getAllW(): List<Post>

    @Query("SELECT * FROM post WHERE id = :id")
    fun findById(id: Int): Single<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<Post>) : Completable

    @Query("delete from post WHERE id = :id")
    fun deleteById(id: Int): Completable

    @Query("DELETE FROM post")
    fun deleteAll() : Completable
}