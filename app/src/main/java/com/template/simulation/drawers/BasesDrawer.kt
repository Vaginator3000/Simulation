package com.template.simulation.drawers

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.template.simulation.R
import com.template.simulation.models.Coordinate
import com.template.simulation.utils.*
import org.w3c.dom.Text
import kotlin.random.Random

var RED_BASE_WIDTH = 200
var RED_BASE_HEIGHT = 200
var BLUE_BASE_WIDTH = 200
var BLUE_BASE_HEIGHT = 200

class BasesDrawer(val container: FrameLayout) {
    val redBaseView = View(container.context)
    val blueBaseView = View(container.context)

    val redTextView = TextView(container.context)
    val blueTextView = TextView(container.context)

    var redMobsCount = Random.nextInt(10) + 1
    var blueMobsCount = Random.nextInt(10) + 1
    var blueResCount = Random.nextInt(10) + 1
    var redResCount = Random.nextInt(10) + 1


    fun drawRedBaseAndMiddleLine() {
        redBaseView.setBackgroundColor(container.context.resources.getColor(R.color.red_base))
        redBaseView.id = View.generateViewId()
        redBaseView.layoutParams = FrameLayout.LayoutParams(RED_BASE_WIDTH,RED_BASE_HEIGHT)

    //    do {
            var halfWidth = getScreenWidth() / 2
            if (getScreenWidth() % 2 == 1) halfWidth++

            (redBaseView.layoutParams as FrameLayout.LayoutParams).leftMargin = halfWidth + Random.nextInt(halfWidth - RED_BASE_WIDTH)

            (redBaseView.layoutParams as FrameLayout.LayoutParams).topMargin = Random.nextInt(getScreenHeight() - RED_BASE_HEIGHT)

    /*    } while (!checkCoordInBorder(Coordinate(
                                        (redBaseView.layoutParams as FrameLayout.LayoutParams).leftMargin + RED_BASE_SIZE,
                                        (redBaseView.layoutParams as FrameLayout.LayoutParams).topMargin + RED_BASE_SIZE)
                                    ))

     */

        container.addView(redBaseView)

        createRedTextView(getViewCoord(redBaseView))

        drawMiddleLine()
    }

    private fun drawMiddleLine() {
        val lineView = View(container.context)
        lineView.setBackgroundColor(Color.WHITE)
        lineView.id = View.generateViewId()
        lineView.layoutParams = FrameLayout.LayoutParams(1, getScreenHeight())

        (lineView.layoutParams as FrameLayout.LayoutParams).leftMargin = getScreenWidth() / 2
        (lineView.layoutParams as FrameLayout.LayoutParams).topMargin = 0

        container.addView(lineView)
    }

    fun drawBlueBase() {
        blueBaseView.setBackgroundColor(container.context.resources.getColor(R.color.blue_base))
        blueBaseView.id = View.generateViewId()
        blueBaseView.layoutParams = FrameLayout.LayoutParams(BLUE_BASE_WIDTH, BLUE_BASE_HEIGHT)

    //    do {
            var halfWidth = getScreenWidth() / 2
            if (getScreenWidth() % 2 == 1) halfWidth++

            (blueBaseView.layoutParams as FrameLayout.LayoutParams).leftMargin = Random.nextInt(halfWidth - BLUE_BASE_WIDTH)

            (blueBaseView.layoutParams as FrameLayout.LayoutParams).topMargin = Random.nextInt(getScreenHeight() - BLUE_BASE_HEIGHT)

    //    } while ((blueBaseView.layoutParams as FrameLayout.LayoutParams).leftMargin + RED_BASE_SIZE > halfWidth)

        container.addView(blueBaseView)

        createBlueTextView(getViewCoord(blueBaseView))
    }

    private fun createRedTextView(coordinate: Coordinate) {
        redTextView.text = "mobs: $redMobsCount\nres: $redResCount"

        redTextView.textSize = 12f
        redTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        redTextView.setTextColor(Color.WHITE)

        redTextView.layoutParams = FrameLayout.LayoutParams((BLUE_BASE_WIDTH * 0.9).toInt(), (BLUE_BASE_HEIGHT * 0.9).toInt())
        (redTextView.layoutParams as FrameLayout.LayoutParams).leftMargin = coordinate.left + coordinate.left / 100
        (redTextView.layoutParams as FrameLayout.LayoutParams).topMargin = coordinate.top + coordinate.top / 100

        container.addView(redTextView)
    }

    private fun createBlueTextView(coordinate: Coordinate) {
        blueTextView.text = "mobs: $blueMobsCount\nres: $blueResCount"

        blueTextView.textSize = 12f
        blueTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        blueTextView.setTextColor(Color.WHITE)

        blueTextView.layoutParams = FrameLayout.LayoutParams((BLUE_BASE_WIDTH * 0.9).toInt(), (BLUE_BASE_HEIGHT * 0.9).toInt())
        (blueTextView.layoutParams as FrameLayout.LayoutParams).leftMargin = coordinate.left + coordinate.left / 100
        (blueTextView.layoutParams as FrameLayout.LayoutParams).topMargin = coordinate.top + coordinate.top / 100

        container.addView(blueTextView)
    }

    fun changeRedResCount(resCount: Int) {
        redResCount += resCount
        updateRedTextView()
    }

    fun changeBlueResCount(resCount: Int) {
        blueResCount += resCount
        updateBlueTextView()
    }

    fun changeRedMobsCount(mobCount: Int) {
        redMobsCount += mobCount
        updateRedTextView()
    }

    fun changeBlueMobsCount(mobCount: Int) {
        blueMobsCount += mobCount
        updateBlueTextView()
    }

    private fun updateRedTextView() {
        redTextView.text = "mobs: $redMobsCount\nres: $redResCount"
    }

    private fun updateBlueTextView() {
        blueTextView.text = "mobs: $blueMobsCount\nres: $blueResCount"
    }

    fun getMiddleOfBlueBase(): Coordinate {
        val lParams = blueBaseView.layoutParams as FrameLayout.LayoutParams
        return Coordinate(
            left = lParams.leftMargin + blueBaseView.layoutParams.width / 2 - 10,
            top = lParams.topMargin + blueBaseView.layoutParams.height / 2 - 10
        )
    }

    fun getMiddleOfRedBase(): Coordinate {
        val lParams = redBaseView.layoutParams as FrameLayout.LayoutParams
        return Coordinate(
            left = lParams.leftMargin + redBaseView.layoutParams.width / 2 - MOB_SIZE / 2,
            top = lParams.topMargin + redBaseView.layoutParams.height / 2 - MOB_SIZE / 2
        )
    }

    private fun checkBaseIsFullSize(base: View): Boolean {
        val lParams = base.layoutParams as FrameLayout.LayoutParams
        if (lParams.width >= getScreenWidth() && lParams.height >= getScreenHeight())
            return true
        return false
    }

    fun checkCoordIsABase(coordinate: Coordinate): Boolean {
        return checkViewContainsCoord(redBaseView, coordinate) || checkViewContainsCoord(blueBaseView, coordinate)
    }

}