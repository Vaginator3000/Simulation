package com.template.simulation.utils

import android.app.Activity
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.template.simulation.models.Coordinate
import kotlin.random.Random

fun checkCoordInBorder(coordinate: Coordinate): Boolean {
    if (coordinate.left < 0 || coordinate.left > getScreenWidth())
        return false
    if (coordinate.top < 0 || coordinate.top > getScreenHeight())
        return false
    return true
}

fun findViewByCoord(coordinate: Coordinate, viewSize: Int, elements: List<View>): View? {
    elements.forEach {
        val viewCoord = getViewCoord(it)
        if (viewCoord.top <= coordinate.top && coordinate.top < viewCoord.top + viewSize &&
                viewCoord.left <= coordinate.left && coordinate.left < viewCoord.left + viewSize)
                    return it
    }
    return null
}

fun getViewCoord(view: View): Coordinate {
    val lParams = view.layoutParams as FrameLayout.LayoutParams
    return Coordinate(lParams.leftMargin, lParams.topMargin)
}

fun delViewByCoord(container: FrameLayout, coordinate: Coordinate, viewSize: Int, elements: List<View>) {
    val viewToDelete = findViewByCoord(coordinate, viewSize, elements) ?: return
    delView(container, viewToDelete)
}

fun delView(container: FrameLayout, view: View) {
    container.runOnUiThread {
        container.removeView(view)
    }
}

fun FrameLayout.runOnUiThread(block: () -> Unit) {
    (this.context as Activity).runOnUiThread {
        block()
    }
}

fun isChanceBiggerThanPercent(percent: Int): Boolean {
    return Random.nextInt(100) < percent
}

fun getScreenHeight(): Int {
    val statusBarHeight = (24 * Resources.getSystem().displayMetrics.density).toInt()
    return Resources.getSystem().displayMetrics.heightPixels - statusBarHeight
}

fun getScreenWidth() =
    Resources.getSystem().displayMetrics.widthPixels

fun checkViewContainsCoord(view: View, coordinate: Coordinate): Boolean {
    var lParams = view.layoutParams as FrameLayout.LayoutParams
    val topLeftViewCoord = Coordinate(lParams.leftMargin, lParams.topMargin)
    val bottomRightViewCoord = Coordinate(lParams.leftMargin + view.layoutParams.width, lParams.topMargin + view.layoutParams.height)

    if (topLeftViewCoord.top <= coordinate.top && coordinate.top <= bottomRightViewCoord.top)
        if (topLeftViewCoord.left <= coordinate.left && coordinate.left <= bottomRightViewCoord.left)
            return true
    return false
}