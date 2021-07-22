package com.template.simulation.models

import android.view.View

data class Resource (
        val id: Int = View.generateViewId(),
        val size: Int,
        val view: View,
        var isRedMobGoingToIt: Boolean = false,
        var isBlueMobGoingToIt: Boolean = false
)