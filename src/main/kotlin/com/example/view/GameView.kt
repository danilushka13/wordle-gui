package com.example.view

import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import tornadofx.*

class GameView : View("GameView") {
    val timerLabel = label("40")
    val textfield = TextField()
    val wordsList = MutableList(6) {
        TextFlow(*MutableList(5) {
            Text("-").apply {
                font = Font.font(24.0)
            }
        }.toTypedArray())
    }

    var endGame = {}
    var enterWord = {}

    override val root: Parent = vbox {
        this.hide()
        vbox {
            label("Угадайте слово с шести раз:") {
                hboxConstraints {
                    marginBottom = 15.0
                }
                style {
                    font = loadFont("/playFont.ttf",20)!!
                    backgroundColor += Color.ALICEBLUE
                }
            }
            hbox {
                button("Подтвердить") {
                    action {
                        enterWord()
                    }
                    style {
                        fontWeight = FontWeight.BLACK
                        borderColor += box(
                            all = Color.OLIVE
                        )

                    }
                }
                this += timerLabel.apply {
                    hboxConstraints {
                        marginLeft = 100.0
                    }
                    style {
                        backgroundColor += Color.ORANGE
                    }
                }
            }
//            button("Подтвердить") {
//                action {
//                    enterWord()
//                }
//            }
//            this += timerLabel.apply {
//            }

        }

        //label("Угадайте слово с шести раз:")
        textfield.setOnKeyPressed {
            if (it.code.name == "ENTER") {
                enterWord()
            }
        }
        this += textfield

        for (word in wordsList) {
            this += word
        }

    }
}