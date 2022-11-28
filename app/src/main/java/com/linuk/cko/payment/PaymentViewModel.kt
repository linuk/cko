package com.linuk.cko.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linuk.cko.data.CardDetails
import com.linuk.cko.api.PaymentRepository
import com.linuk.cko.payment.CardType
import com.linuk.cko.api.PaymentUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val utils: PaymentUtils,
) : ViewModel() {
    private val _viewType by lazy { MutableLiveData<ViewType>(ViewType.PaymentDetails) }
    private val _cardNumber by lazy { MutableLiveData("") }
    private val _isCardNumberInvalid by lazy { MutableLiveData(false) }
    private val _expiryMonth by lazy { MutableLiveData("") }
    private val _expiryYear by lazy { MutableLiveData("") }
    private val _cvv by lazy { MutableLiveData("") }
    private val _cardType by lazy { MutableLiveData(CardType.DEFAULT) }
    private val _buttonEnabled by lazy { MutableLiveData(false) }
    private val _isLoading by lazy { MutableLiveData(false) }
    private val _errorMessage by lazy { MutableLiveData<String?>(null) }

    val viewType: LiveData<ViewType> by lazy { _viewType }
    val cardNumber: LiveData<String> by lazy { _cardNumber }
    val isCardNumberInvalid: LiveData<Boolean> by lazy { _isCardNumberInvalid }
    val expiryMonth: LiveData<String> by lazy { _expiryMonth }
    val expiryYear: LiveData<String> by lazy { _expiryYear }
    val cvv: LiveData<String> by lazy { _cvv }
    val cardType: LiveData<CardType> by lazy { _cardType }
    val buttonEnabled: LiveData<Boolean> by lazy { _buttonEnabled }
    val isLoading: LiveData<Boolean> by lazy { _isLoading }
    val errorMessage: LiveData<String?> by lazy { _errorMessage }

    fun onViewChanged(view: ViewType) {
        _viewType.postValue(view)
    }

    fun onCardNumberChanged(number: String) {
        val newCardType = utils.getCardType(number)
        if (utils.isCardNumberUpdateValid(number.length, newCardType)) {
            _cardNumber.value = number
            maybeEnableButton()
            if (newCardType != cardType.value) {
                _cardType.value = newCardType
            }
        }
    }

    fun onExpiryMonthChanged(month: String) {
        if (utils.isMonthUpdateValid(month)) {
            _expiryMonth.value = month
            maybeEnableButton()
        }
    }

    fun onExpiryYearChanged(year: String) {
        if (utils.isYearUpdateValid(year)) {
            _expiryYear.value = year
            maybeEnableButton()
        }
    }

    fun onCvvChanged(cvv: String) {
        if (utils.isCvvUpdateValid(cvv, cardType.value)) {
            _cvv.value = cvv
            maybeEnableButton()
        }
    }

    fun onErrorDialogDone() = _errorMessage.postValue(null)

    private fun areFieldsValid(): Boolean =
        cardNumber.value?.let {
            val isDigitsValid = utils.isCardNumberDigitsValid(it.length, cardType.value)
            val isNumberValid = utils.isCardNumberValid(cardNumber.value)

            val isNumberFullyFilledButInValid = isDigitsValid && !isNumberValid
            if (_isCardNumberInvalid.value != isNumberFullyFilledButInValid) {
                _isCardNumberInvalid.postValue(isNumberFullyFilledButInValid)
            }
            return@let isDigitsValid && isNumberValid
        } == true &&
                expiryYear.value?.let { utils.isYearValid(it) } == true &&
                expiryMonth.value?.let { utils.isMonthValid(it) } == true &&
                cvv.value?.let { utils.isCvvValid(it, cardType.value) } == true

    private fun maybeEnableButton() {
        val isValid = areFieldsValid()
        if (isValid != buttonEnabled.value) _buttonEnabled.postValue(isValid)
    }

    fun maybeMakePayment() {
        if (areFieldsValid()) {
            _isLoading.postValue(true)
            performMakingPayment()
        }
    }

    private fun performMakingPayment() {
        val cardDetails = CardDetails(
            number = cardNumber.value ?: "",
            expiryMonth = expiryMonth.value ?: "",
            expiryYear = expiryYear.value ?: "",
            cvv = cvv.value ?: ""
        )

        repository.makePayment(
            cardDetails,
            { url ->
                _isLoading.postValue(false)
                onViewChanged(ViewType.ThreeDS(url))
            },
            { message ->
                _isLoading.postValue(false)
                _errorMessage.postValue(message)
            }
        )
    }
}

sealed interface ViewType {
    object PaymentDetails : ViewType
    data class ThreeDS(val url: String) : ViewType
    data class PaymentResult(val isSuccessful: Boolean) : ViewType
}