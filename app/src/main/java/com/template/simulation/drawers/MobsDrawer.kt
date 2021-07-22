package com.template.simulation.drawers

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.template.simulation.R
import com.template.simulation.models.Coordinate
import com.template.simulation.models.Mob
import com.template.simulation.models.Resource
import com.template.simulation.utils.*
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.random.Random

val MOB_SIZE =30
val TIME_TICK: Long = 200

class MobsDrawer(private val container: FrameLayout, private val basesDrawer: BasesDrawer, private val resDrawer: ResDrawer) {
    val allMobs = mutableListOf<Mob>()

    fun startMobs() {
        Thread {
            while (true) {
                goThrowAllResAndCreateMobs()
                mobsMove()
                sleep(TIME_TICK)
            }
        }.start()
    }

    private fun mobsMove() {
        allMobs.toList().forEach { mob ->
            showMobsMove(mob)
            checkMobTakeRes(mob)
            checkMobBackToBase(mob)
        }
    }

    private fun checkMobBackToBase(mob: Mob) {
        if (mob.goingToCoord == basesDrawer.getMiddleOfRedBase() ||
                mob.goingToCoord == basesDrawer.getMiddleOfBlueBase())
                    if (checkMobOnBase(mob)) {
                        if (mob.color == "red") {
                            basesDrawer.changeRedMobsCount(1)
                            basesDrawer.changeRedResCount(mob.hasRes)
                        } else {
                            basesDrawer.changeBlueMobsCount(1)
                            basesDrawer.changeBlueResCount(mob.hasRes)
                        }
                        delMob(mob)
                    }

    }

    private fun delMob(mob: Mob) {
        delView(container, mob.view)
        allMobs.remove(mob)
    }

    private fun checkMobOnBase(mob: Mob): Boolean {
        val mobCoord = getViewCoord(mob.view)

        if (mob.color == "red")
            return checkViewContainsCoord(basesDrawer.redBaseView, mobCoord)
        else
            return checkViewContainsCoord(basesDrawer.blueBaseView, mobCoord)
    }

    private fun checkMobTakeRes(mob: Mob) {
        val mobCoordinate = getViewCoord(mob.view)
        val destinationCoordinate = mob.goingToCoord ?: return

        if (abs(mobCoordinate.left - destinationCoordinate.left) <= MOB_SIZE &&
                abs(mobCoordinate.top - destinationCoordinate.top) <= MOB_SIZE) {
                    resDrawer.allRes
                    val foundedRes = resDrawer.findResByCoordinate(destinationCoordinate) ?: return
                    mobTakesRes(mob, foundedRes)
                    makeAnotherMobGoBack(mob, foundedRes)
        }
    }

    private fun makeAnotherMobGoBack(mob: Mob, res: Resource) {
        allMobs.forEach {
            if (it != mob) {
                val resCoord = getViewCoord(res.view)
                if (it.goingToCoord == resCoord)
                    it.goingToCoord = if (it.color == "red") basesDrawer.getMiddleOfRedBase()
                                        else basesDrawer.getMiddleOfBlueBase()
            }
        }
    }

    private fun mobTakesRes(mob: Mob, res: Resource) {
        mob.hasRes += res.size
        mob.goingToCoord = if (mob.color == "red") basesDrawer.getMiddleOfRedBase()
                            else basesDrawer.getMiddleOfBlueBase()
        resDrawer.delRes(res)

    }

    private fun showMobsMove(mob: Mob) {
        val lParams = mob.view.layoutParams as FrameLayout.LayoutParams

        val vectorToDestination = getWayToDestination(mob) ?: return

        if (vectorToDestination.left != 0) {
            lParams.leftMargin += vectorToDestination.left / abs(vectorToDestination.left) * mob.speed * MOB_SIZE / 5
        }
        if (vectorToDestination.top != 0) {
            lParams.topMargin += vectorToDestination.top / abs(vectorToDestination.top)  * mob.speed * MOB_SIZE / 5
        }

        container.runOnUiThread {
            container.removeView(mob.view)
            container.addView(mob.view)
        }
    }

    private fun getWayToDestination(mob: Mob): Coordinate? {
        val mobCoordinate = getViewCoord(mob.view)
        val destinationCoordinate = mob.goingToCoord ?: return null

        var left = destinationCoordinate.left - mobCoordinate.left
        var top = destinationCoordinate.top - mobCoordinate.top

        if (abs(mobCoordinate.left - destinationCoordinate.left) <= MOB_SIZE)
            left = 0
        if (abs(mobCoordinate.top - destinationCoordinate.top) <= MOB_SIZE)
            top = 0

        return Coordinate(left, top)

    }

    private fun goThrowAllResAndCreateMobs() {
        if (resDrawer.allRes.isEmpty()) return

        resDrawer.allRes.forEach { res ->
            if (!res.isBlueMobGoingToIt && basesDrawer.blueMobsCount > 0) {
                basesDrawer.changeBlueMobsCount(-1)
                res.isBlueMobGoingToIt = true

                val blueMobId = createBlueMob()
                makeMobGoingToView(blueMobId, res.view)
            }
            if (!res.isRedMobGoingToIt && basesDrawer.redMobsCount > 0) {
                basesDrawer.changeRedMobsCount(-1)
                res.isRedMobGoingToIt = true

                val redMobId = createRedMob()
                makeMobGoingToView(redMobId, res.view)
            }
        }
    }

    private fun makeMobGoingToView(mobId: Int, view: View) {
        val coordinate = getViewCoord(view)
        allMobs.first { it.id == mobId }.goingToCoord = coordinate
    }

    private fun createRedMob(): Int {
        val redMobView = ImageView(container.context)
        redMobView.setImageResource(R.drawable.mob)

        redMobView.setColorFilter(container.context.resources.getColor(R.color.red_mob))
        redMobView.layoutParams = FrameLayout.LayoutParams(MOB_SIZE, MOB_SIZE)
        (redMobView.layoutParams as FrameLayout.LayoutParams).leftMargin = basesDrawer.getMiddleOfRedBase().left
        (redMobView.layoutParams as FrameLayout.LayoutParams).topMargin = basesDrawer.getMiddleOfRedBase().top

        val newMob = Mob(
                        color = "red",
                        hp = Random.nextInt(10) + 1,
                        power = Random.nextInt(10) + 1,
                        speed = Random.nextInt(10) + 1,
                        view = redMobView
                )

        allMobs.add(newMob)

        container.runOnUiThread {
            container.addView(redMobView)
        }

        return newMob.id
    }

    private fun createBlueMob(): Int  {
        val blueMobView = ImageView(container.context)
        blueMobView.setImageResource(R.drawable.mob)
     //   blueMobView.setColorFilter(R.color.blue_mob)
        blueMobView.setColorFilter(container.context.resources.getColor(R.color.blue_mob))
        blueMobView.layoutParams = FrameLayout.LayoutParams(MOB_SIZE, MOB_SIZE)
        (blueMobView.layoutParams as FrameLayout.LayoutParams).leftMargin = basesDrawer.getMiddleOfBlueBase().left
        (blueMobView.layoutParams as FrameLayout.LayoutParams).topMargin = basesDrawer.getMiddleOfBlueBase().top

        val newMob = Mob(
                color = "blue",
                hp = Random.nextInt(10) + 1,
                power = Random.nextInt(10) + 1,
                speed = Random.nextInt(10) + 1,
                view = blueMobView
        )

        allMobs.add(newMob)

        container.runOnUiThread {
            container.addView(blueMobView)
        }

        return newMob.id
    }
}