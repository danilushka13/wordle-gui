package com.example.view

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Toggle
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import model.WordleModel
import tornadofx.*
import kotlin.concurrent.thread

class MainView : View("Wordle") {

    init {
        FX.primaryStage.icons += Image(MainView::class.java.classLoader.getResource("icon.png")!!.toString())
    }

    private val textColorToColor = mapOf<WordleModel.TextColors, Color>(
        WordleModel.TextColors.GREY to Color.GREY,
        WordleModel.TextColors.YELLOW to Color.ORANGE,
        WordleModel.TextColors.GREEN to Color.GREEN,
    )
    private val windowHeight = 400.0
    private val windowWidth = 400.0
    private val model = WordleModel()
    private val endLabel = label("Игра окончена:\n") {
        font = Font.font(20.0)
        style {
            backgroundColor += Color.WHITE
        }
    }
    private val endGameWindow = vbox {
        setPrefSize(200.0, 200.0)
        this += endLabel
    }
    val tutorialView = find(TutorialView::class)
    val gameView = find(GameView::class)
    fun startTimerThread() {
        val uiThread = Thread.currentThread()
        thread {
            while (true) {
                if (model.isGameHard()) {
                    println(model.timer)
                    if (!model.isTimerStopped()) {
                        Platform.runLater {
                            gameView.timerLabel.text = "${model.timer}"
                        }
                        model.updateTimer()
                    }
                }
                Thread.sleep(1000)
                if (!uiThread.isAlive) {
                    break
                }
            }
        }
    }

    private fun enterWord() {
        val printedText = gameView.textfield.text
        gameView.textfield.text = ""
        val nowWord = gameView.wordsList[model.tries]
        if (model.isWordCorrect(printedText)) {
            if (!model.isLosed() && !model.isWon()) {
                for (i in 0 until 5) {
                    (nowWord.children[i] as Text).text = printedText[i].toString()
                    nowWord.children[i].style {
                        fill = textColorToColor[model.getColors(printedText)[i]] ?: Color.GREY
                    }
                }
            }
            model.toNextTry()
        }
        if (model.isLosed() || model.isWon()) endGame()
    }

    private fun endGame() {
        endLabel.text = "Игра окончена:\nТы проигал"
        if (model.isWon())
            endLabel.text = "Игра окончена:\nТы победил\nВремя: ${
                System.currentTimeMillis().toInt() / 1000 - model.startTime
            } секунд"
        endGameWindow.show()
        model.timer = 0
    }


    private fun restart() {
        model.restart()
        for (i in 0 until gameView.wordsList.size) {
            for (node in gameView.wordsList[i].children) {
                (node as Text).text = "-"
                (node).style {
                    fill = Color.BLACK
                }
            }
        }
        endGameWindow.hide()
    }

    fun setDifficulty(toggles: List<Toggle>) {
        if (toggles[0].isSelected) {
            model.difficulty = WordleModel.Difficulty.HARD
        } else {
            model.difficulty = WordleModel.Difficulty.EASY
        }
        println(model.difficulty.name)
    }

    override val root = stackpane {
        setPrefSize(windowWidth, windowHeight)
        this += tutorialView.apply {
            startButton.action {
                setDifficulty(tutorialView.difficultLevelBox.toggles.toList())
                model.startGame()
                if (!model.isGameHard()) {
                    gameView.timerLabel.hide()
                } else {
                    startTimerThread()
                }
                tutorialView.root.hide()
                gameView.root.show()

            }
        }
        this += gameView.apply {
            endGame = { this@MainView.endGame() }
            enterWord = { this@MainView.enterWord() }

        }

        this += endGameWindow.apply {
            alignment = Pos.CENTER
            this.hide()
            button("Начать заново") {
                action {
                    restart()
                }
            }
        }
    }
}