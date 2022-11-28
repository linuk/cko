package com.linuk.cko.payment

import com.linuk.cko.api.PaymentRepository
import com.linuk.cko.api.PaymentUtils
import com.linuk.cko.data.CardDetails
import com.linuk.cko.data.CardType

object PaymentFixtures {
    // Assuming all inputs update are valid
    fun buildUtils() = object : PaymentUtils {
        override fun getCardType(cardNumber: String): CardType = CardType.DEFAULT
        override fun getCardTypeImageRes(cardType: CardType?): Int? = null
        override fun isCardNumberUpdateValid(numberLength: Int, cardType: CardType?): Boolean =
            true

        override fun isCardNumberDigitsValid(numberLength: Int, cardType: CardType?): Boolean =
            true

        override fun isCardNumberValid(number: String?): Boolean = true
        override fun isCvvValid(cvv: String, cardType: CardType?): Boolean = true
        override fun isCvvUpdateValid(cvv: String, cardType: CardType?): Boolean = true
        override fun isMonthValid(month: String): Boolean = true
        override fun isMonthUpdateValid(month: String): Boolean = true
        override fun isYearValid(year: String): Boolean = true
        override fun isYearUpdateValid(year: String): Boolean = true
    }

    fun buildRepository(): MockPaymentRepository = object : MockPaymentRepository {
        override var isPaymentMade: Boolean = false
        override fun makePayment(
            cardDetails: CardDetails,
            onSuccess: (redirectUrl: String) -> Unit,
            onFailure: (message: String) -> Unit
        ) {
            isPaymentMade = true
        }
    }

    interface MockPaymentRepository : PaymentRepository {
        var isPaymentMade: Boolean
    }
}