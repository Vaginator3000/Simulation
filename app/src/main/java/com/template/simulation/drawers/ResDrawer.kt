package com.template.simulation.drawers

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.template.simulation.R
import com.template.simulation.models.Coordinate
import com.template.simulation.models.Resource
import com.template.simulation.utils.*
import java.lang.Thread.sleep
import kotlin.random.Random


class ResDrawer(val container: FrameLayout, val basesDrawer: BasesDrawer) {
    val allRes = mutableListOf<Resource>()
    val RES_SIZE = 20

    fun startResCreate() {
        Thread {
            while (true) {
                if (isChanceBiggerThanPercent(30))
                    createRes()
                sleep(1000)
            }
        }.start()
    }

    fun delRes(res: Resource) {
        delView(container, res.view)
        allRes.remove(res)
    }

    fun findResByCoordinate(coordinate: Coordinate): Resource? {
        val resView = findViewByCoord(coordinate, RES_SIZE, allRes.map { it.view }) ?: return null
        return allRes.firstOrNull { it.view == resView }
    }

    fun createRes(resCoord: Coordinate? = null, size: Int = 0) {
        val resView = ImageView(container.context)
        resView.setImageResource(R.drawable.mob)
        resView.setColorFilter(container.context.resources.getColor(R.color.res_color))
        resView.layoutParams = FrameLayout.LayoutParams(RES_SIZE, RES_SIZE)

        var topLeftResCoord: Coordinate
        if (resCoord == null) {
            do {
                val newLeftCoord = Random.nextInt(getScreenWidth())
                val newTopCoord = Random.nextInt(getScreenHeight())
                topLeftResCoord = Coordinate(newLeftCoord, newTopCoord)
                val bottomRightResCoord = Coordinate(newLeftCoord + RES_SIZE, newTopCoord + RES_SIZE)
            } while (basesDrawer.checkCoordIsABase(topLeftResCoord) || basesDrawer.checkCoordIsABase(bottomRightResCoord))
        }
        else {
            topLeftResCoord = resCoord
        }

        (resView.layoutParams as FrameLayout.LayoutParams).topMargin = topLeftResCoord.top
        (resView.layoutParams as FrameLayout.LayoutParams).leftMargin = topLeftResCoord.left

        allRes.add(Resource(
                size = if (size == 0) Random.nextInt(10) + 1
                        else size,
                view = resView
        ))

        container.runOnUiThread {
            container.addView(resView)
        }
    }
}