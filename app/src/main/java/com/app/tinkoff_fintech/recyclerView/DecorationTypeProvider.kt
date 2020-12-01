package com.app.tinkoff_fintech.recyclerView

interface DecorationTypeProvider {

    fun getType(position: Int) : DecorationType

}