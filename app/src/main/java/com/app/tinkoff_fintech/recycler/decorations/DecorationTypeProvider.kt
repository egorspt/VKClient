package com.app.tinkoff_fintech.recycler.decorations

import com.app.tinkoff_fintech.recycler.decorations.DecorationType

interface DecorationTypeProvider {

    fun getType(position: Int) : DecorationType

}