package com.linuk.cko.api

import com.linuk.cko.data.CardType

interface PaymentUtils {
    fun getCardType(cardNumber: String): CardType
    fun getCardTypeImageRes(cardType: CardType?): Int?
    fun isCardNumberUpdateValid(numberLength: Int, cardType: CardType?): Boolean
    fun isCardNumberDigitsValid(numberLength: Int, cardType: CardType?): Boolean
    fun isCardNumberValid(number: String?): Boolean
    fun isCvvValid(cvv: String, cardType: CardType?): Boolean
    fun isCvvUpdateValid(cvv: String, cardType: CardType?): Boolean
    fun isMonthValid(month: String): Boolean
    fun isMonthUpdateValid(month: String): Boolean
    fun isYearValid(year: String): Boolean
    fun isYearUpdateValid(year: String): Boolean
}