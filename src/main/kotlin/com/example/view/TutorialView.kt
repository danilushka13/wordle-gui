package com.example.view

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.text.Font
import tornadofx.*

class TutorialView : View("Tutorial") {
    val difficultLevelBox = ToggleGroup()
    val startButton = Button()

    override val root = vbox {
        alignment = Pos.CENTER
        imageview {
            image = Image(MainView::class.java.classLoader.getResource("wordle_rules.png")!!.toString())
        }
        this += startButton.apply {
            text = "Start game"
            style {
                font = Font.font(30.0)
            }
        }
        vbox {
            alignment = Pos.CENTER
            style {
                font = Font.font(20.0)
            }
            label("Уровень сложности")
            hbox {
                alignment = Pos.CENTER
                radiobutton("Hard (с таймером)", difficultLevelBox) {
                    hboxConstraints {
                        marginRight = 30.0
                    }
                }
                radiobutton("Easy (без таймера)", difficultLevelBox) {
                    isSelected = true
                }
            }
        }

    }
}
