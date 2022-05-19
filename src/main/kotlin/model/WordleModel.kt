package model

import java.io.File

class WordleModel {
    var tries = 0
    var answer = ""
    var colors = MutableList(5) {TextColors.GREY}
    var startTime = 0
    var timer = 40;

    enum class TextColors() {
        GREY, GREEN, YELLOW
    }

    enum class Difficulty {
        HARD, EASY
    }
    var difficulty = Difficulty.EASY

    val words = getFileLines("wordleWords.txt")
    val answers = getFileLines("wordleAnswers.txt")

    private fun getFileLines(fileName: String): List<String> {
        val resourceURL = this.javaClass.classLoader.getResource(fileName)
        return File(resourceURL!!.toURI()).reader().readLines()
    }

    fun startGame() {
        answer = answers.random()
        println(answer)
        startTime = System.currentTimeMillis().toInt() / 1000
        println(difficulty.name)
    }

    fun updateTimer(){
        if (isGameHard()) {
            if (timer > 0) {
                timer--
            }
        }
    }
    fun isTimerStopped() = timer <= 0

    fun isGameHard() = difficulty == Difficulty.HARD

    fun isWordCorrect(word: String): Boolean {
        return word.length == 5 && word in words
    }

    fun restart() {
        colors = MutableList(5) {TextColors.GREY}
        tries = 0
        answer = answers.random()
        startTime = System.currentTimeMillis().toInt() / 1000
        println(answer)
        timer = 40
    }

    fun getColors(word: String): List<TextColors> {
        colors = MutableList(5) {TextColors.GREY}

        for (i in 0 until 5) {
            if (word[i] in answer) {
                colors[i] = TextColors.YELLOW
            }
        }

        for (i in 0 until 5) {
            if (answer[i] == word[i]) {
                colors[i] = TextColors.GREEN
            }
        }
        return colors
    }



    fun toNextTry() {
        if (isLosed() || isWon()) tries = 0
        else tries++
        timer = 40
    }

    fun isLosed() = tries > 5 || isTimerStopped()

    fun isWon() = colors.stream().allMatch {it.equals(TextColors.GREEN) }
}