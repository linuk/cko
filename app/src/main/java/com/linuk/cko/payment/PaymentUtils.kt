package com.linuk.cko.payment

import com.linuk.cko.R
import java.time.LocalDate
import javax.inject.Inject

enum class CardType {
    VISA, MASTER, AMEX, DEFAULT
}

class PaymentUtils @Inject constructor() {

    fun getCardType(cardNumber: String) = when {
        cardNumber.isEmpty() -> CardType.DEFAULT
        isVisa(cardNumber) -> CardType.VISA
        isAmex(cardNumber) -> CardType.AMEX
        isMaster(cardNumber) -> CardType.MASTER
        else -> CardType.DEFAULT
    }

    private fun isVisa(cardNumber: String) = cardNumber[0] == '4'

    // Amex prefix with 34, 37
    private fun isAmex(cardNumber: String) =
        cardNumber.length >= 2 && cardNumber[0] == '3' && (cardNumber[1] == '4' || cardNumber[1] == '7')

    // Master prefix ith 51 - 57
    private fun isMaster(cardNumber: String) =
        cardNumber.length >= 2 && cardNumber.startsWith("5") && cardNumber[1] - '0' > 0 && cardNumber[1] - '0' <= 7

    fun getCardTypeImageRes(cardType: CardType?) = when (cardType) {
        CardType.VISA -> R.drawable.visa
        CardType.MASTER -> R.drawable.master
        CardType.AMEX -> R.drawable.amex
        else -> null
    }

    fun isCardNumberUpdateValid(numberLength: Int, cardType: CardType?) = when (cardType) {
        CardType.MASTER -> numberLength <= MASTER_NUMBER_DIGITS
        CardType.VISA -> numberLength <= VISA_NUMBER_DIGITS.maxOf { it }
        CardType.AMEX -> numberLength <= AMEX_NUMBER_DIGITS
        else -> numberLength <= MAX_CARD_NUMBER_DIGITS
    }

    fun isCardNumberDigitsValid(numberLength: Int, cardType: CardType?) = when (cardType) {
        CardType.MASTER -> numberLength == MASTER_NUMBER_DIGITS
        CardType.VISA -> VISA_NUMBER_DIGITS.contains(numberLength)
        CardType.AMEX -> numberLength == AMEX_NUMBER_DIGITS
        else -> false
    }

    // Assuming the card number digits are correct, this should be called after
    // [isCardNumberDigitsValid] is validated
    // Detailed algo logic can be found on: https://en.wikipedia.org/wiki/Luhn_algorithm
    fun isCardNumberValid(number: String?): Boolean {
        if (number.isNullOrEmpty()) return false

        val checkSum = number[number.length - 1] - '0'
        var i = number.length - 2
        var doubleNums = mutableListOf<Int>()
        var singleNumSum = 0
        while (i >= 0) {
            doubleNums.add(number[i--] - '0')
            if (i >= 0) singleNumSum += number[i--] - '0'
        }
        doubleNums = doubleNums
            .map { it * 2 }
            .map { if (it >= 10) it % 10 + it / 10 else it }
            .toMutableList()
        val sum = doubleNums.sum() + singleNumSum
        val expectedCheckSum = (10 - (sum % 10)) % 10
        return expectedCheckSum == checkSum
    }

    fun isCvvValid(cvv: String, cardType: CardType?) = when (cardType) {
        CardType.MASTER -> cvv.length == Companion.MASTER_CVV_DIGITS
        CardType.VISA -> cvv.length == Companion.VISA_CVV_DIGITS
        CardType.AMEX -> cvv.length == Companion.AMEX_CVV_DIGITS
        else -> false
    }

    fun isCvvUpdateValid(cvv: String, cardType: CardType?) = when (cardType) {
        CardType.MASTER -> cvv.length <= Companion.MASTER_CVV_DIGITS
        CardType.VISA -> cvv.length <= Companion.VISA_CVV_DIGITS
        CardType.AMEX -> cvv.length <= Companion.AMEX_CVV_DIGITS
        else -> cvv.length <= Companion.DEFAULT_CVV_DIGITS
    }

    fun isMonthValid(month: String) =
        month.length == 2 && month.toIntOrNull()?.let { it in 1..12 } == true

    fun isMonthUpdateValid(month: String) =
        month.isEmpty() || month.toIntOrNull()?.let { it <= 12 } == true

    fun isYearValid(year: String) = year.length == 4 && year.toInt() >= CURRENT_YEAR

    fun isYearUpdateValid(year: String) =
        year.length <= 3 || (year.length == 4 && year.toInt() >= CURRENT_YEAR)

    companion object {
        const val BASE_URL = "https://integrations-cko.herokuapp.com"
        const val PAYMENT_PATH = "pay"
        const val SUCCESS_PAYMENT_REDIRECTION_URL = "https://success.com"
        const val FAILURE_PAYMENT_REDIRECTION_URL = "https://failure.com"
        const val DEBUG_CARD_CVV = "100"
        const val DEBUG_CARD_EXP_MONTH = "06"
        const val DEBUG_CARD_EXP_YEAR = "2023"
        const val DEBUG_CARD_VALID_NUMBER = "4242424242424242"
        const val DEBUG_CARD_INVALID_NUMBER = "4243754271700719"
        const val DEFAULT_CVV_DIGITS = 3
        private const val AMEX_CVV_DIGITS = 4
        private const val MASTER_CVV_DIGITS = 3
        private const val VISA_CVV_DIGITS = 3
        private const val AMEX_NUMBER_DIGITS = 15
        private const val MASTER_NUMBER_DIGITS = 16
        private const val MAX_CARD_NUMBER_DIGITS = 19
        private val VISA_NUMBER_DIGITS = listOf(13, 16)
        private val CURRENT_YEAR by lazy { LocalDate.now().year }
    }
}