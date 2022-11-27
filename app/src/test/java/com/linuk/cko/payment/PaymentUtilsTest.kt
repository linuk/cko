package com.linuk.cko.payment

import com.linuk.cko.R
import org.junit.Test
import java.time.LocalDate

class PaymentUtilsTest {
    private val utils = PaymentUtils()

    @Test
    fun `get VISA card type`() {
        assert(utils.getCardType("4") == CardType.VISA)
        (1..9).filter { it != 4 }.map { it.toString() }.forEach {
            assert(utils.getCardType(it) != CardType.VISA)
        }
    }

    @Test
    fun `get AMEX card type`() {
        assert(utils.getCardType("34") == CardType.AMEX)
        assert(utils.getCardType("37") == CardType.AMEX)
        (10..99).filter { it != 34 && it != 37 }.map { it.toString() }.forEach {
            assert(utils.getCardType(it) != CardType.AMEX) { it }
        }
    }

    @Test
    fun `get MASTER card type`() {
        (51..57).map { it.toString() }.forEach {
            assert(utils.getCardType(it) == CardType.MASTER) { it }
        }
        (0..50).map { it.toString() }.forEach {
            assert(utils.getCardType(it) != CardType.MASTER)
        }
    }

    @Test
    fun `get image res`() {
        assert(utils.getCardTypeImageRes(CardType.VISA) == R.drawable.visa)
        assert(utils.getCardTypeImageRes(CardType.MASTER) == R.drawable.master)
        assert(utils.getCardTypeImageRes(CardType.AMEX) == R.drawable.amex)
        assert(utils.getCardTypeImageRes(null) == null)
    }

    @Test
    fun `VISA number digits check`() {
        assert(utils.isCardNumberDigitsValid(16, CardType.VISA))
        assert(utils.isCardNumberDigitsValid(13, CardType.VISA))
        (1..20).filter { it != 13 && it != 16 }.forEach {
            assert(!utils.isCardNumberDigitsValid(it, CardType.VISA))
        }
    }

    @Test
    fun `MASTER number digits check`() {
        assert(utils.isCardNumberDigitsValid(16, CardType.MASTER))
        (1..20).filter { it != 16 }.forEach {
            assert(!utils.isCardNumberDigitsValid(it, CardType.MASTER))
        }
    }

    @Test
    fun `AMEX number digits check`() {
        assert(utils.isCardNumberDigitsValid(15, CardType.AMEX))
        (1..20).filter { it != 15 }.forEach {
            assert(!utils.isCardNumberDigitsValid(it, CardType.AMEX))
        }
    }

    @Test
    fun `is Cvv Valid`() {
        listOf(CardType.MASTER, CardType.VISA).forEach { cardType ->
            assert(utils.isCvvValid("123", cardType))
            listOf("12", "1", "").forEach { cvv ->
                assert(!utils.isCvvValid(cvv, cardType))
            }
        }

        assert(utils.isCvvValid("1234", CardType.AMEX))
        listOf("123", "12", "1", "").forEach { cvv ->
            assert(!utils.isCvvValid(cvv, CardType.AMEX))
        }
    }

    @Test
    fun `is Cvv update valid`() {
        listOf(CardType.MASTER, CardType.VISA).forEach { cardType ->
            listOf("123", "12", "1", "").forEach { cvv ->
                assert(utils.isCvvUpdateValid(cvv, cardType))
            }
            assert(!utils.isCvvUpdateValid("1234", cardType))
        }

        listOf("1234", "123", "12", "1", "").forEach { cvv ->
            assert(utils.isCvvUpdateValid(cvv, CardType.AMEX))
        }
        assert(!utils.isCvvUpdateValid("12345", CardType.AMEX))
    }

    @Test
    fun `is month valid`() {
        (1..12).map { if (it < 10) "0$it" else it.toString() }
            .forEach { month -> assert(utils.isMonthValid(month)) }
        (13..99).map { it.toString() }
            .forEach { month -> assert(!utils.isMonthValid(month)) }
    }

    @Test
    fun `is month update valid`() {
        assert(utils.isMonthUpdateValid(""))
        (1..12).map { if (it < 10) "0$it" else it.toString() }
            .forEach { month -> assert(utils.isMonthUpdateValid(month)) }
        assert(!utils.isMonthValid("123"))
    }

    @Test
    fun `is year valid`() {
        val currentYear = LocalDate.now().year
        assert(utils.isYearValid(currentYear.toString()))
        assert(utils.isYearValid((currentYear + 1).toString()))
        assert(!utils.isYearValid((currentYear - 1).toString()))
        assert(!utils.isYearValid((currentYear).toString().substring(0, 3)))
    }

    @Test
    fun `is year update valid`() {
        val currentYear = LocalDate.now().year
        listOf("", "2", "20", "203").forEach { year -> assert(utils.isYearUpdateValid(year)) }
        assert(utils.isYearUpdateValid(currentYear.toString()))
        assert(utils.isYearUpdateValid((currentYear + 1).toString()))
        assert(!utils.isYearUpdateValid((currentYear - 1).toString()))
    }

    @Test
    fun `is card number valid`() {
        val validCardNumbers = listOf(
            79927398713,
            4242424242424242,
            4243754271700719,
            4005371662456930,
            4005915091911392,
        )

        validCardNumbers.forEach { validNumber ->
            assert(utils.isCardNumberValid(validNumber.toString()))
        }

        validCardNumbers.map { it + 1 }.forEach { invalidNumber ->
            assert(!utils.isCardNumberValid(invalidNumber.toString()))
        }
    }

    @Test
    fun `is Master card number update valid`() {
        (1..16).forEach { assert(utils.isCardNumberUpdateValid(it, CardType.MASTER)) }
        (17..30).forEach { assert(!utils.isCardNumberUpdateValid(it, CardType.MASTER)) }
    }

    @Test
    fun `is VISA card number update valid`() {
        (1..16).forEach { assert(utils.isCardNumberUpdateValid(it, CardType.VISA)) }
        (17..30).forEach { assert(!utils.isCardNumberUpdateValid(it, CardType.VISA)) }
    }

    @Test
    fun `is AMEX card number update valid`() {
        (1..15).forEach { assert(utils.isCardNumberUpdateValid(it, CardType.AMEX)) }
        (16..30).forEach { assert(!utils.isCardNumberUpdateValid(it, CardType.AMEX)) }
    }

    @Test
    fun `is default card number update valid`() {
        (1..19).forEach { assert(utils.isCardNumberUpdateValid(it, CardType.DEFAULT)) }
        (20..30).forEach { assert(!utils.isCardNumberUpdateValid(it, CardType.DEFAULT)) }
    }
}