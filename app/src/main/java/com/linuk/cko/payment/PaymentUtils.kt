package com.linuk.cko.payment

import com.linuk.cko.R
import java.time.LocalDate

const val BASE_URL = "https://integrations-cko.herokuapp.com"
const val PAYMENT_PATH = "pay"
const val SUCCESS_PAYMENT_REDIRECTION_URL = "https://success.com"
const val FAILURE_PAYMENT_REDIRECTION_URL = "https://failure.com"
const val DEBUG_CARD_CVV = "100"
const val DEBUG_CARD_EXP_MONTH = "06"
const val DEBUG_CARD_EXP_YEAR = "2023"
const val DEBUG_CARD_VALID_NUMBER = "4242424242424242"
const val DEBUG_CARD_INVALID_NUMBER = "4243754271700719"

private val CURRENT_YEAR by lazy { LocalDate.now().year }
private const val AMEX_CVV_DIGITS = 4
private const val MASTER_CVV_DIGITS = 3
private const val VISA_CVV_DIGITS = 3

private const val AMEX_NUMBER_DIGITS = 15
private const val MASTER_NUMBER_DIGITS = 16
private val VISA_NUMBER_DIGITS = listOf(13, 16)

const val DEFAULT_CVV_DIGITS = 3

enum class CardType {
    VISA, MASTER, AMEX, DEFAULT
}

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

fun isCardNumberDigitsValid(number: String, cardType: CardType?) = when (cardType) {
    CardType.MASTER -> number.length == MASTER_NUMBER_DIGITS
    CardType.VISA -> VISA_NUMBER_DIGITS.contains(number.length)
    CardType.AMEX -> number.length == AMEX_NUMBER_DIGITS
    else -> false
}

// Assuming the card number digits are correct, this should be called after
// [isCardNumberDigitsValid] is validated
fun isCardNumberValid(number: String?): Boolean {
    // TODO: Implement "Luhn Algorithm"
    return true
}

fun isCvvValid(cvv: String, cardType: CardType?) = when (cardType) {
    CardType.MASTER -> cvv.length == MASTER_CVV_DIGITS
    CardType.VISA -> cvv.length == VISA_CVV_DIGITS
    CardType.AMEX -> cvv.length == AMEX_CVV_DIGITS
    else -> false
}

fun isCvvUpdateValid(cvv: String, cardType: CardType?) = when (cardType) {
    CardType.MASTER -> cvv.length <= MASTER_CVV_DIGITS
    CardType.VISA -> cvv.length <= VISA_CVV_DIGITS
    CardType.AMEX -> cvv.length <= AMEX_CVV_DIGITS
    else -> cvv.length <= DEFAULT_CVV_DIGITS
}

fun isMonthValid(month: String) = month.toIntOrNull()?.let { it in 1..12 } == true

fun isMonthUpdateValid(month: String) =
    month.isEmpty() || month.toIntOrNull()?.let { it <= 12 } == true

fun isYearValid(year: String) = year.length == 4 && year.toInt() >= CURRENT_YEAR

fun isYearUpdateValid(year: String) =
    year.length <= 3 || (year.length == 4 && year.toInt() >= CURRENT_YEAR)