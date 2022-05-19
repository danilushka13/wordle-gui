package com.example.view

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.Toggle
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import model.WordleModel
import tornadofx.*
import kotlin.concurrent.thread

class MainView : View("Wordle"){

    init {
        FX.primaryStage.icons += Image(MainView::class.java.classLoader.getResource("icon.png").toString())
    }

    private fun enterWord() {
        val nowWordNumber = model.tries
        val printedText = textfield.text
        textfield.text = ""
        val nowWord = wordsList[nowWordNumber]

        if (model.isWordCorrect(printedText)) {
            if (!model.isLosed() && !model.isWon()) {
                val colorsList = model.getColors(printedText)
                for (i in 0 until 5) {
                    (nowWord.children[i] as Text).text = printedText[i].toString()
                    when (colorsList[i]) {
                        WordleModel.TextColors.GREY -> {
                            nowWord.children[i].style {
                                fill = Color.GREY
                            }
                        }
                        WordleModel.TextColors.YELLOW -> {
                            nowWord.children[i].style {
                                fill = Color.ORANGE
                            }
                        }
                        WordleModel.TextColors.GREEN -> {
                            nowWord.children[i].style {
                                fill = Color.GREEN
                            }
                        }
                    }
                }
            }

            model.toNextTry()
        }
        if (model.isLosed() || model.isWon()) endGame()

    }

    fun endGame() {
        endGameWindow.style {
            visibility = FXVisibility.VISIBLE
        }
        if (model.isLosed())
            endLabel.text += "Ты проигал"
        if (model.isWon()) {
            endLabel.text += "Ты победил\n"
            endLabel.text += "Время: ${System.currentTimeMillis().toInt() / 1000 - model.startTime} секунд"
        }
        model.timer = 0
    }

    val windowHeight = 400.0
    val windowWidth = 400.0

    val model = WordleModel()
    val textfield = TextField()
    val enterButton = Button("Подтвердить")
    val wordsList = MutableList(6) {
        TextFlow(*MutableList(5) {
            Text("-").apply {
                font = Font.font(24.0)
            }
        }.toTypedArray())
    }

    val endLabel = label("Игра окончена:\n") {
        font = Font.font(20.0)
        style {
            backgroundColor += Color.WHITE
        }
    }

    val timerLabel = label("40")
    val endGameWindow = vbox {
        setPrefSize(200.0, 200.0)
        this += endLabel
    }

    val difficultLevelBox = ToggleGroup()

    fun restart() {
        model.restart()
        for (i in 0 until wordsList.size) {
            for (node in wordsList[i].children) {
                (node as Text).text = "-"
                (node).style {
                    fill = Color.BLACK
                }
            }
        }
        endLabel.text = "Игра окончена\n"

        endGameWindow.style {
            visibility = FXVisibility.HIDDEN
        }
    }

    val mainScene = vbox {

        style {
            visibility = FXVisibility.HIDDEN
        }
        setPrefSize(windowWidth, windowHeight)
        hbox {
            label("Подтвердить слово:") {
                hboxConstraints {
                    marginRight = 20.0
                }
            }
            this += enterButton.apply {
                action {
                    enterWord()
                }
            }
            this += timerLabel.apply {
                thread {
                    while (true) {
                        println("thread_done")
                        if (model.isGameHard()) {
                            println(model.timer)
                            if (!model.isTimerStopped()) {
                                Platform.runLater {
                                    text = "${model.timer}"
                                }
                                model.updateTimer()
                            } else {
                                Platform.runLater {
                                    endGame()
                                }
                            }
                        }
                        Thread.sleep(1000)
                        if (!uiThread.isAlive) {
                            break;
                        }
                    }
                }
            }

        }
        label("Угадайте слово с шести раз:")
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
    val uiThread = Thread.currentThread();


    fun setDifficulty(toggles: List<Toggle>) {
        if (toggles[0].isSelected) {
            model.difficulty = WordleModel.Difficulty.HARD
        } else {
            model.difficulty = WordleModel.Difficulty.EASY
        }
    }

    override val root = stackpane {
        vbox {
            alignment = Pos.CENTER
            imageview {
                image = Image(MainView::class.java.classLoader.getResource("wordle_rules.png").toString())
            }
            button("Start game") {
                alignment = Pos.TOP_LEFT
                style {
                    font = Font.font(30.0)
                }
                action {
                    setDifficulty(difficultLevelBox.toggles.toList())
                    model.startGame()
                    if (!model.isGameHard()) {
                        timerLabel.style {
                            visibility = FXVisibility.HIDDEN
                        }
                    }
                    mainScene.style {
                        visibility = FXVisibility.VISIBLE
                    }
                    parent.style {
                        visibility = FXVisibility.HIDDEN
                    }



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

        this += mainScene

        this += endGameWindow.apply {
            alignment = Pos.CENTER
            style {
                visibility = FXVisibility.HIDDEN
            }
            button("Начать заново") {
                action {
                    restart()
                }
            }
        }
    }
}