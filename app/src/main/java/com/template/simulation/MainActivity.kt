package com.template.simulation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.template.simulation.drawers.BasesDrawer
import com.template.simulation.drawers.MobsDrawer
import com.template.simulation.drawers.ResDrawer
import kotlinx.android.synthetic.main.activity_main.*

var RED_MOBS_AMOUNT = 10
var BLUE_MOBS_AMOUNT = 10

class MainActivity : AppCompatActivity() {

    val basesDrawer by lazy {
        BasesDrawer(container)
    }

    val mobsDrawer by lazy {
        MobsDrawer(container, basesDrawer, resDrawer)
    }

    val resDrawer by lazy {
        ResDrawer(container, basesDrawer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container.setOnClickListener {
            basesDrawer.drawRedBaseAndMiddleLine()
            basesDrawer.drawBlueBase()

            mobsDrawer.startMobs()

            resDrawer.startResCreate()
        }
    }
}