package ru.morozovit.dncryptor101.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage

class App: Application() {
    override fun start(primaryStage: Stage) {
        with (primaryStage) {
            title = "Dncryptor101"
            width = 800 / 2.0
            height = 600 / 2.0

            val label = Label("Test")
            with (Scene(label)) {
                primaryStage.scene = this
            }
            show()
        }
    }
}