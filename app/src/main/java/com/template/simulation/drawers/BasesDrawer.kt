package com.template.simulation.drawers

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.template.simulation.GameCore
import com.template.simulation.R
import com.template.simulation.TIME_TICK
import com.template.simulation.models.Coordinate
import com.template.simulation.utils.*
import java.lang.Thread.sleep
import kotlin.random.Random

var RED_BASE_WIDTH = 200
var RED_BASE_HEIGHT = 200
var BLUE_BASE_WIDTH = 200
var BLUE_BASE_HEIGHT = 200

class BasesDrawer(val container: FrameLayout) {
    val redBaseView = View(container.context)
    val blueBaseView = View(container.context)

    private val redTextView = TextView(container.context)
    private val blueTextView = TextView(container.context)

    var redMobsCount = Random.nextInt(9) + 2
    var blueMobsCount = Random.nextInt(9) + 2
    var blueResCount = Random.nextInt(10) + 1
    var redResCount = Random.nextInt(10) + 1

    fun drawBasesAndStartLifeCycle() {
        drawRedBase()
        drawBlueBase()
        drawMiddleLine()
        Thread {
            while (true) {
                checkBaseIsFullSize(redBaseView)
                checkBaseIsFullSize(blueBaseView)
                if (Random.nextBoolean()) checkRedBaseCanBeIncrease()
                else checkRedMobsCanMultiply()

                if (Random.nextBoolean()) checkBlueMobsCanMultiply()
                else checkBlueBaseCanBeIncrease()

            }
            sleep(TIME_TICK)
        }.start()
    }

    private fun checkRedMobsCanMultiply() {
        if (redMobsCount > 1 && redResCount > 5) {
            if (isChanceBiggerThanPercent(10) && isChanceBiggerThanPercent(10)) {
                changeRedMobsCount(1)
                changeRedResCount(-5)
            }
        }
    }

    private fun checkBlueMobsCanMultiply() {
        if (blueMobsCount > 1 && blueResCount > 5) {
            if (isChanceBiggerThanPercent(10) && isChanceBiggerThanPercent(10)) {
                changeBlueMobsCount(1)
                changeBlueResCount(-5)
            }
        }
    }

    private fun checkRedBaseCanBeIncrease() {
        if (redMobsCount > 2 && redResCount > 10) {
            if (isChanceBiggerThanPercent(10) && isChanceBiggerThanPercent(10)) {
            //    var redBaseSize = getRedBaseSize()
            //   Log.d("MyLog", "1. Red increate - width = ${redBaseSize.left} - height = ${redBaseSize.top}")
                increateBase(redBaseView)
                changeRedResCount(-10)
            //    Log.d("MyLog", "2. Red increate - width = ${redBaseSize.left} - height = ${redBaseSize.top}")
            }
        }
    }

    private fun checkBlueBaseCanBeIncrease() {
        if (blueMobsCount > 2 && blueResCount > 10) {
            if (isChanceBiggerThanPercent(10) && isChanceBiggerThanPercent(10)) {
            //    var blueBaseSize = getBlueBaseSize()
            //    Log.d("MyLog", "1. Blue increate - width = ${blueBaseSize.left} - height = ${blueBaseSize.top}")
                increateBase(blueBaseView)
                changeBlueResCount(-10)
            //    Log.d("MyLog", "2. Blue increate - width = ${blueBaseSize.left} - height = ${blueBaseSize.top}")
            }
        }
    }

    private fun increateBase(baseView: View) {
        if (!checkBaseWidthCanBeIncreate(baseView)) {
            if (!checkBaseHeightCanBeIncreate(baseView))
                checkBaseIsFullSize(baseView)
            else {
                increateBaseHeight(baseView)
            }
            return
        }

        if (!checkBaseHeightCanBeIncreate(baseView)) {
            increateBaseWidth(baseView)
            return
        }

        if (Random.nextBoolean()) increateBaseWidth(baseView)
        else increateBaseHeight(baseView)
    }

    private fun increateBaseHeight(baseView: View) {
        val topBorder = 0
        val bottomBorder = getScreenHeight()

        val baseHeight = if (baseView == redBaseView) getRedBaseSize().top
                        else getBlueBaseSize().top

        val lParams = (baseView.layoutParams as FrameLayout.LayoutParams)

        if ( lParams.topMargin == topBorder ) {
            container.runOnUiThread {
                if (bottomBorder - (lParams.topMargin + baseHeight) < 100)
                    lParams.height += bottomBorder - (lParams.topMargin + baseHeight)
                else
                    lParams.height += 100
            }
            return
        }
        if ( lParams.topMargin + baseHeight >= bottomBorder ) {
            container.runOnUiThread {
                if (lParams.topMargin >= 100) {
                    lParams.topMargin -= 100
                    lParams.height += 100
                } else {
                    lParams.height += lParams.topMargin
                    lParams.topMargin = topBorder
                }
            }
            return
        }

        container.runOnUiThread {
            if (Random.nextBoolean()) {
                if (bottomBorder - (lParams.topMargin + baseHeight) < 100)
                    lParams.height += bottomBorder - (lParams.topMargin + baseHeight)
                else
                    lParams.height += 100
            } else {
                if (lParams.topMargin >= 100) {
                    lParams.topMargin -= 100
                    lParams.height += 100
                } else {
                    lParams.height += lParams.topMargin
                    lParams.topMargin = topBorder
                }
            }
        }
    }


    private fun increateBaseWidth(baseView: View) {
        var halfWidth = getScreenWidth() / 2
        if (getScreenWidth() % 2 == 1) halfWidth++

        val leftBorder = if (baseView == redBaseView) halfWidth
                            else 0
        val rightBorder = if (baseView == redBaseView) getScreenWidth()
                            else halfWidth

        val baseWidth = if (baseView == redBaseView) getRedBaseSize().left
                        else getBlueBaseSize().left

        val lParams = (baseView.layoutParams as FrameLayout.LayoutParams)

        if ( lParams.leftMargin <= leftBorder ) {
            container.runOnUiThread {
                if (rightBorder - (lParams.leftMargin + baseWidth) < 100)
                    lParams.width += rightBorder - (lParams.leftMargin + baseWidth)
                else
                    lParams.width += 100
            }
            return
        }
        if ( lParams.leftMargin + baseWidth == rightBorder ) {
            container.runOnUiThread {
                if (lParams.leftMargin - leftBorder >= 100) {
                    lParams.leftMargin -= 100
                    lParams.width += 100
                } else {
                    lParams.width += lParams.leftMargin - leftBorder
                    lParams.leftMargin = leftBorder
                }
            }
            return
        }

        container.runOnUiThread {
            if (Random.nextBoolean()) {
                if (rightBorder - (lParams.leftMargin + baseWidth) < 100)
                    lParams.width += rightBorder - (lParams.leftMargin + baseWidth)
                else
                    lParams.width += 100
            } else {
                if (lParams.leftMargin - leftBorder >= 100) {
                    lParams.leftMargin -= 100
                    lParams.width += 100
                } else {
                    lParams.width += lParams.leftMargin - leftBorder
                    lParams.leftMargin = leftBorder
                }
            }
        }
    }

    private fun checkBaseWidthCanBeIncreate(baseView: View): Boolean {
        val baseWidth = if (baseView == redBaseView) getRedBaseSize().left
                        else getBlueBaseSize().left
        if (baseWidth == getScreenWidth())
            return false
        return true
    }

    private fun checkBaseHeightCanBeIncreate(baseView: View): Boolean {
        val baseHeight = if (baseView == redBaseView) getRedBaseSize().top
                        else getBlueBaseSize().top
        if (baseHeight== getScreenHeight())
            return false
        return true
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

    private fun drawRedBase() {
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
    }

    private fun drawBlueBase() {
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
        container.runOnUiThread {
            redTextView.text = "mobs: $redMobsCount\nres: $redResCount"
        }
    }

    private fun updateBlueTextView() {
        container.runOnUiThread {
            blueTextView.text = "mobs: $blueMobsCount\nres: $blueResCount"
        }
    }

    fun getMiddleOfBlueBase(): Coordinate {
        val lParams = blueBaseView.layoutParams as FrameLayout.LayoutParams
        return Coordinate(
            left = lParams.leftMargin + blueBaseView.layoutParams.width / 2,
            top = lParams.topMargin + blueBaseView.layoutParams.height / 2
        )
    }

    fun getMiddleOfRedBase(): Coordinate {
        val lParams = redBaseView.layoutParams as FrameLayout.LayoutParams
        return Coordinate(
            left = lParams.leftMargin + redBaseView.layoutParams.width / 2,
            top = lParams.topMargin + redBaseView.layoutParams.height / 2
        )
    }

    private fun getRedBaseSize(): Coordinate {
        val lParams = redBaseView.layoutParams as FrameLayout.LayoutParams
        return Coordinate(lParams.width, lParams.height)
    }

    private fun getBlueBaseSize(): Coordinate {
        val lParams = redBaseView.layoutParams as FrameLayout.LayoutParams
        return Coordinate(lParams.width, lParams.height)
    }

    private fun checkBaseIsFullSize(baseView: View) {
        val lParams = baseView.layoutParams as FrameLayout.LayoutParams
        if (lParams.width >= getScreenWidth() / 2 && lParams.height >= getScreenHeight()) {
            GameCore.pauseGame()
            val winText = if (baseView == blueBaseView) "Blue win"
                            else "Red win"
            Toast.makeText(container.context, winText, Toast.LENGTH_SHORT).show()
            container.postDelayed( {
                (container.context as Activity).recreate()
            }, 1500)
        }
    }

    fun checkCoordIsABase(coordinate: Coordinate): Boolean {
        return checkViewContainsCoord(redBaseView, coordinate) || checkViewContainsCoord(blueBaseView, coordinate)
    }

    fun checkCoordIsARedBase(coordinate: Coordinate): Boolean {
        return checkViewContainsCoord(redBaseView, coordinate)
    }

    fun checkCoordIsABlueBase(coordinate: Coordinate): Boolean {
        return checkViewContainsCoord(blueBaseView, coordinate)
    }

}