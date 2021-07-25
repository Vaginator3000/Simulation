package com.template.simulation

object GameCore {
    private var pause = false

    fun pauseGame() {
        pause = false
    }

    fun continueGame() {
        pause = true
    }

    fun isGameOnPause() = pause
}