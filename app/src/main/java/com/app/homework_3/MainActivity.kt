package com.app.homework_3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = RVAdapter(getPosts().toMutableList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val callback = ItemTouchHelperCallback(adapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_post_recycler_view)!!)
        recyclerView.addItemDecoration(CustomItemDecorator(this))

        swipeContainer.setOnRefreshListener {
            adapter.refresh()
            swipeContainer.isRefreshing = false
        }
    }

    private fun getPosts(): List<Post> {
        val jsonString = this.assets.open("posts.json").bufferedReader().use{
            it.readText()
        }
        return Gson().fromJson(jsonString, Posts::class.java).posts
    }

}


