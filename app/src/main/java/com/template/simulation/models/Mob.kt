package com.template.simulation.models

import android.view.View

data class Mob (
        val id: Int = View.generateViewId(),
        val color: String,
        val hp: Int,
        val power: Int,
        val speed: Int,
        val view: View,
        var goingToCoord: Coordinate? = null,
        var isAlive: Boolean = true,
        var hasRes: Int = 0
        )