package com.template.simulation.drawers

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.template.simulation.R
import com.template.simulation.TIME_TICK
import com.template.simulation.models.Coordinate
import com.template.simulation.models.Mob
import com.template.simulation.models.Resource
import com.template.simulation.utils.*
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.random.Random

val MOB_SIZE = 30

class MobsDrawer(private val container: FrameLayout, private val basesDrawer: BasesDrawer, private val resDrawer: ResDrawer) {
    val allMobs = mutableListOf<Mob>()

    fun startMobs() {
        Thread {
            while (true) {
            //    goThrowAllResAndCreateMobs()
                spawnMobs()
                mobsMove()
                sleep(TIME_TICK)
            }
        }.start()
    }

    private fun spawnMobs() {
        if (isChanceBiggerThanPercent(20) && isChanceBiggerThanPercent(15))
            if (basesDrawer.blueMobsCount > 1) {
                createBlueMob()
                basesDrawer.changeBlueMobsCount(-1)
            }
        if (isChanceBiggerThanPercent(15) && isChanceBiggerThanPercent(20))
            if (basesDrawer.redMobsCount > 1) {
                createRedMob()
                basesDrawer.changeRedMobsCount(-1)
            }
    }

    private fun mobsMove() {
        if (allMobs.isEmpty()) return
        allMobs.toList().forEach { mob ->
            showMobsMove(mob)
            checkMobTakeRes(mob)
            checkMobBackToBase(mob)
        }
    }

    private fun checkMobBackToBase(mob: Mob) {
        val baseCoord = if (mob.color == "red") basesDrawer.getMiddleOfRedBase()
                            else basesDrawer.getMiddleOfBlueBase()
        if (mob.goingToCoord == baseCoord)
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
        makeGoingToResFree(mob)
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
                    makeAnotherMobFindNext(mob, foundedRes)
        }
    }

    private fun makeAnotherMobFindNext(mob: Mob, res: Resource) {
        allMobs.forEach {
            if (it != mob) {
                val resCoord = getViewCoord(res.view)
                if (it.goingToCoord == resCoord)
                    it.goingToCoord = null
                if (it.hasRes > 0)
                    makeMobGoToBase(it)
            }
        }
    }

    private fun makeMobGoToBase(mob: Mob) {
        mob.goingToCoord = if (mob.color == "red") basesDrawer.getMiddleOfRedBase()
        else basesDrawer.getMiddleOfBlueBase()
    }

    private fun mobTakesRes(mob: Mob, res: Resource) {
        mob.hasRes += res.size
        makeMobGoToBase(mob)
        resDrawer.delRes(res)
    }

    private fun showMobsMove(mob: Mob) {
        val lParams = mob.view.layoutParams as FrameLayout.LayoutParams

        val mobStep = mob.speed * MOB_SIZE / 5

        val vectorToDestination = getWayToDestination(mob)
        if (vectorToDestination == null) {
            makeMobMoveInBorder(mob)
        } else {
            if (vectorToDestination.left != 0) {
                lParams.leftMargin += vectorToDestination.left / abs(vectorToDestination.left) * mobStep
            }
            if (vectorToDestination.top != 0) {
                lParams.topMargin += vectorToDestination.top / abs(vectorToDestination.top) * mobStep
            }
        }

        checkMobOnEnemyBase(mob)

        checkMobSeeNearestRes(mob)

        checkMobSeeEnemy(mob)

        container.runOnUiThread {
            container.removeView(mob.view)
            container.addView(mob.view)
        }
    }

    private fun checkMobOnEnemyBase(mob: Mob) {
        val mobCoordinate = getViewCoord(mob.view)

        var mobOnEnemyBase: Boolean

        if (mob.color == "red") mobOnEnemyBase = basesDrawer.checkCoordIsABlueBase(mobCoordinate)
                                else mobOnEnemyBase = basesDrawer.checkCoordIsARedBase(mobCoordinate)

        if (mobOnEnemyBase) {
            makeGoingToResFree(mob)
            if (mob.color == "red") mob.color = "blue"
            else mob.color = "red"
        }
    }

    private fun makeGoingToResFree(mob: Mob) {
        mob.goingToCoord ?: return
        val res = resDrawer.findResByCoordinate(mob.goingToCoord!!) ?: return

        if (mob.color == "red") res.isRedMobGoingToIt = false
        else res.isBlueMobGoingToIt = false
    }

    private fun makeMobMoveInBorder(mob: Mob) {
        val lParams = mob.view.layoutParams as FrameLayout.LayoutParams
        val mobStep = mob.speed * MOB_SIZE / 5

        var leftStep = 0
        var topStep = 0

        var newCoord: Coordinate
        do {
            if (Random.nextBoolean())
                leftStep = mobStep
            else
                leftStep = -mobStep
            if (Random.nextBoolean())
                topStep = mobStep
            else
                topStep = -mobStep

            newCoord = Coordinate(lParams.leftMargin + leftStep, lParams.topMargin + topStep)

            val mobOnAEnemyBase = if (mob.color == "red") basesDrawer.checkCoordIsABlueBase(newCoord)
                                    else  basesDrawer.checkCoordIsARedBase(newCoord)
        } while (!checkCoordInBorder(newCoord) || mobOnAEnemyBase)

        lParams.leftMargin += leftStep
        lParams.topMargin += topStep
    }

    private fun checkMobSeeEnemy(mob: Mob) {
        val mobCoord = getViewCoord(mob.view)
        val enemyColor = if (mob.color == "red") "blue"
                        else "red"

        val screenHeight = getScreenHeight()
        val screenWidth = getScreenWidth()
        for (enemy in allMobs.filter { it.color == enemyColor }) {
            if (!enemy.isFight) {
                val enemyCoord = getViewCoord(enemy.view)

                if (abs(mobCoord.left - enemyCoord.left) + abs(mobCoord.top - enemyCoord.top) < (screenHeight + screenWidth) / 6 ) {
                    mobsInteracting(mob, enemy)
                }

                if (abs(mobCoord.left - enemyCoord.left) + abs(mobCoord.top - enemyCoord.top) < (screenHeight + screenWidth) / 25) {
                    startMobsFight(mob, enemy)
                }
            }
        }
    }

    private fun startMobsFight(mob: Mob, enemy: Mob) {
        enemy.isFight = true
        mob.isFight = true

        val mobCoord = getViewCoord(mob.view)
        val enemyCoord = getViewCoord(enemy.view)

        val fightCoord1 = Coordinate(
                left = (mobCoord.left + enemyCoord.left) / 2 - MOB_SIZE / 2,
                top = (mobCoord.top + enemyCoord.top) / 2 - MOB_SIZE / 2
        )

        val fightCoord2 = Coordinate(
                left = (mobCoord.left + enemyCoord.left) / 2 + MOB_SIZE / 2,
                top = (mobCoord.top + enemyCoord.top) / 2 + MOB_SIZE / 2
        )

        mob.goingToCoord = fightCoord1
        enemy.goingToCoord = fightCoord2

        Thread {
            while ( !(mob.goingToCoord == fightCoord1 && enemy.goingToCoord == fightCoord2 && mob.isAlive && enemy.isAlive) ) {

            }
            while (mob.isAlive && enemy.isAlive) {
                var damagedMob: Mob
                var attackedMob: Mob
                if (Random.nextBoolean()) {
                    enemy.hp -= mob.power
                    damagedMob = enemy
                    attackedMob = mob
                } else {
                    mob.hp -= enemy.power
                    damagedMob = mob
                    attackedMob = enemy
                }

                container.runOnUiThread {
                    damagedMob.view.alpha = 0.5f
                }
                container.postDelayed({
                    damagedMob.view.alpha = 1f
                }, TIME_TICK / 2)

                if (damagedMob.hp < 0) {
                    killMob(damagedMob)
                    attackedMob.goingToCoord = null
                    attackedMob.isFight = false
                }

                if (damagedMob.hp < 3) {
                    damagedMob.color = attackedMob.color

                    damagedMob.goingToCoord = null
                    damagedMob.isFight = false
                    attackedMob.goingToCoord = null
                    attackedMob.isFight = false
                }

                sleep(TIME_TICK)

            }
        }.start()

    }

    private fun killMob(mob: Mob) {
        val mobCoord = getViewCoord(mob.view)
        delMob(mob)
        if (mob.hasRes > 0) {
            resDrawer.createRes(mobCoord, mob.hasRes)
        }
        mob.isAlive = false
    }

    private fun mobsInteracting(mob: Mob, enemy: Mob) {
        if( enemy.power > mob.hp + 2  ) {
            makeMobGoToBase(mob)
        }

        if (enemy.hp + 2 < mob.power && mob.speed >= enemy.speed) {
            mob.goingToCoord = getViewCoord(enemy.view)
            makeMobGoToBase(enemy)
        }
    }

    private fun checkMobSeeNearestRes(mob: Mob) {
        val nearestRes = goThrowAllResAndReturnNearest(mob) ?: return
        makeMobGoingToView(mob, nearestRes.view)
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

    private fun goThrowAllResAndReturnNearest(mob: Mob): Resource? {
        if (resDrawer.allRes.isEmpty()) return null

        val mobCoord = getViewCoord(mob.view)

        val screenHeight = getScreenHeight()
        val screenWidth = getScreenWidth()

        for (res in resDrawer.allRes.toList()) {
            val resCoord = getViewCoord(res.view)

            var isTeammateGoingToIt = if (mob.color == "red") res.isRedMobGoingToIt
                                        else res.isBlueMobGoingToIt

            if (resCoord == mob.goingToCoord || isTeammateGoingToIt) continue

      //      if (abs(mobCoord.left - resCoord.left) < getScreenWidth() / 10 ||
        //            abs(mobCoord.top - resCoord.top) < getScreenHeight() / 10 ) {
            if (abs(mobCoord.left - resCoord.left) + abs(mobCoord.top - resCoord.top) < (screenHeight + screenWidth) / 6 ) {
                        if (mob.color == "red") res.isRedMobGoingToIt = true
                        else res.isBlueMobGoingToIt = true

                        if (mob.goingToCoord == null)
                            return res
                        else {
                            return chooseNearestRes(mob, res)
                        }
            }
        }
        return null
    }

    private fun chooseNearestRes(mob: Mob, res: Resource): Resource {
        val mobCoord = getViewCoord(mob.view)
        val res1Coord = getViewCoord(res.view)
        val res2Coord = mob.goingToCoord!!

        val wayToRes1 = Coordinate(
                left = abs(mobCoord.left - res1Coord.left),
                top = abs(mobCoord.top - res1Coord.top)
        )

        val wayToRes2 = Coordinate(
                left = abs(mobCoord.left - res2Coord.left),
                top = abs(mobCoord.top - res2Coord.top)
        )

        if (wayToRes1.left + wayToRes1.top < wayToRes2.top + wayToRes2.left)
            return res
        else
            return resDrawer.findResByCoordinate(res2Coord) ?: res
    }

    /*private fun goThrowAllResAndCreateMobs() {
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
    }*/

    private fun makeMobGoingToView(mob: Mob, view: View) {
        val coordinate = getViewCoord(view)
        allMobs.first { it == mob }.goingToCoord = coordinate
    }

    private fun createRedMob(): Int {
        val redMobView = ImageView(container.context)
        redMobView.setImageResource(R.drawable.mob)

        redMobView.setColorFilter(container.context.resources.getColor(R.color.red_mob))
        redMobView.layoutParams = FrameLayout.LayoutParams(MOB_SIZE, MOB_SIZE)
        (redMobView.layoutParams as FrameLayout.LayoutParams).leftMargin = basesDrawer.getMiddleOfRedBase().left
        (redMobView.layoutParams as FrameLayout.LayoutParams).topMargin = basesDrawer.getMiddleOfRedBase().top

        val generateMobPower = Random.nextInt(10) + 1
        val newMob = Mob(
                        color = "red",
                        hp = Random.nextInt(10) + 1,
                        power = generateMobPower,
                        speed = 10 - generateMobPower + 1,
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

        val generateMobPower = Random.nextInt(10) + 1
        val newMob = Mob(
                color = "blue",
                hp = Random.nextInt(10) + 1,
                power = generateMobPower,
                speed = 10 - generateMobPower + 1,
                view = blueMobView
        )

        allMobs.add(newMob)

        container.runOnUiThread {
            container.addView(blueMobView)
        }

        return newMob.id
    }
}