package model

import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class WordleModelTest {

    var model: WordleModel = WordleModel()

    @BeforeEach
    fun setUp() {
        model = WordleModel()
        model.start()
    }

    @Test
    fun startGame() {
        assertNotEquals("", model.answer)
        assertEquals(System.currentTimeMillis() / 1000, model.startTime)
    }

    @Test
    fun isWordCorrect() {
        val word1 = model.answer + "_"
        val word2 = "clown"
        val word3 = "aaaaa"
        assertFalse(model.isWordCorrect(word1))
        assertTrue(model.isWordCorrect(word2))
        assertFalse(model.isWordCorrect(word3))
    }

    @Test
    fun isWon() {
        model.inputWord(model.answer)
        assertTrue(model.isWon())

        setUp()

        model.inputWord("aaaaaaaaaaaaaaaaaaaaaaaaaaa")
        assertFalse(model.isWon())

        setUp()

        model.inputWord("a")
        assertFalse(model.isWon())

        setUp()
        for (i in 0 until 6) {
            model.inputWord("a")
            model.toNextTry()
        }
        model.inputWord(model.answer)

        model.isWon()
        assertFalse(model.isWon())

        setUp()
        model.makeGameHard()
        for (i in 0 until 40) {
            model.updateTimer()
        }
        model.inputWord(model.answer)
        println(model.timer)
        assertFalse(model.isWon())
    }

    @Test
    fun isLost() {
        model.inputWord(model.answer)
        assertFalse(model.isLost())

        setUp()

        model.inputWord("aaaaaaaaaaaaaaaaaaaaaaaaaaa")
        assertFalse(model.isLost())

        setUp()
        for (i in 0 until 6) {
            model.inputWord("a")
            model.toNextTry()
        }
        model.inputWord(model.answer)
        assertTrue(model.isLost())

        setUp()
        model.makeGameHard()
        for (i in 0 until 40) {
            model.updateTimer()
        }
        model.inputWord(model.answer)
        assertTrue(model.isLost())
    }

    @Test
    fun inputWord() {
        val answer = "about"
        model.answer = answer
        model.inputWord(answer)
        for (i in 0 until  model.answer.length) {
            assertEquals(WordleModel.TextColors.GREEN, model.lastWordColors[i])
        }

        model.inputWord("tatar")

        assertEquals(WordleModel.TextColors.YELLOW, model.lastWordColors[0])
        assertEquals(WordleModel.TextColors.YELLOW, model.lastWordColors[1])
        assertEquals(WordleModel.TextColors.GREY, model.lastWordColors[2])
        assertEquals(WordleModel.TextColors.GREY, model.lastWordColors[3])
        assertEquals(WordleModel.TextColors.GREY, model.lastWordColors[4])

        model.inputWord("ababakan stolytsa hakasyy")
        for (i in 0 until  model.answer.length) {
            assertEquals(WordleModel.TextColors.GREY, model.lastWordColors[i])
        }

        model.inputWord("artel")

        assertEquals(WordleModel.TextColors.GREEN, model.lastWordColors[0])
        assertEquals(WordleModel.TextColors.GREY, model.lastWordColors[1])
        assertEquals(WordleModel.TextColors.YELLOW, model.lastWordColors[2])
        assertEquals(WordleModel.TextColors.GREY, model.lastWordColors[3])
        assertEquals(WordleModel.TextColors.GREY, model.lastWordColors[4])

    }
}