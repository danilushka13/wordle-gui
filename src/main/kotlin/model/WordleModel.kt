package model

import java.io.File

class WordleModel {

    var tries = 0
    var answer = ""
    var lastWordColors = MutableList(5) {TextColors.GREY}
    var startTime: Long = 0
    var timer = 40

    enum class TextColors() {
        GREY, GREEN, YELLOW
    }

    enum class Difficulty {
        HARD, EASY
    }

    private var difficulty = Difficulty.EASY


    val words = getFileLines("wordleWords.txt")
    val answers = getFileLines("wordleAnswers.txt")

    // перевводим текстовый файл в массив
    private fun getFileLines(fileName: String): List<String> {
        val resourceURL = this.javaClass.classLoader.getResource(fileName)
        return File(resourceURL!!.toURI()).reader().readLines()
    }

    // запуск игры: запуск секундомера, выбор рандомного ответа
    fun start() {
        lastWordColors = MutableList(5) {TextColors.GREY}
        tries = 0
        answer = answers.random()
        startTime = System.currentTimeMillis() / 1000
        println(answer)
        timer = 40
    }

    // обновление таймера каждую секунду
    fun updateTimer(){
        if (isGameHard()) {
            if (timer > 0) {
                timer--
            }
        }
    }

    fun makeGameHard() {
        difficulty = Difficulty.HARD
    }

    fun makeGameEasy() {
        difficulty = Difficulty.EASY
    }


    fun isTimerStopped() = timer <= 0

    fun isGameHard() = difficulty == Difficulty.HARD

    // проверка введённого пользователем слова на корректность
    fun isWordCorrect(word: String): Boolean {
        return word.length == 5 && word in words
    }

//    // получаем список цветов каждого символа в введённом пользователем слове (цвета получаем из enum class)
//    fun getLastColors(): List<TextColors> {
//        return lastWordColors
//    }

    fun inputWord(word: String) {
        lastWordColors = MutableList(5) {TextColors.GREY}
        if (word.length == 5 && tries <= 5 && !isTimerStopped()) {
            val charList = answer.toMutableList()

            for (i in 0 until 5) {
                if (answer[i] == word[i] && word[i] in charList) {
                    lastWordColors[i] = TextColors.GREEN
                    charList.remove(word[i])
                }
            }

            for (i in 0 until 5) {
                if (word[i] in charList) {
                    lastWordColors[i] = TextColors.YELLOW
                    charList.remove(word[i])
                }
            }
        }
    }


// проверка
    fun toNextTry() {
        if (isLost() || isWon()) tries = 0
        else tries++
        timer = 40
    }

    // проверка на победу/проигрыш
    fun isLost() = tries > 5 || isTimerStopped()

    fun isWon() = lastWordColors.stream().allMatch {it.equals(TextColors.GREEN) }
}