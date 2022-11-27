package com.linuk.cko.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaymentViewModel : ViewModel() {
    private val _viewType by lazy { MutableLiveData<ViewType>(ViewType.PaymentDetails) }
    private val _cardNumber by lazy { MutableLiveData("") }
    private val _expiryMonth by lazy { MutableLiveData("") }
    private val _expiryYear by lazy { MutableLiveData("") }
    private val _cvv by lazy { MutableLiveData("") }
    private val _cardType by lazy { MutableLiveData(CardType.DEFAULT) }
    private val _buttonEnabled by lazy { MutableLiveData(false) }
    private val _isLoading by lazy { MutableLiveData(false) }
    private val _errorMessage by lazy { MutableLiveData<String?>(null) }

    val viewType: LiveData<ViewType> = _viewType
    val cardNumber: LiveData<String> = _cardNumber
    val expiryMonth: LiveData<String> = _expiryMonth
    val expiryYear: LiveData<String> = _expiryYear
    val cvv: LiveData<String> = _cvv
    val cardType: LiveData<CardType> = _cardType
    val buttonEnabled: LiveData<Boolean> = _buttonEnabled
    val isLoading: LiveData<Boolean> = _isLoading
    val errorMessage: LiveData<String?> = _errorMessage

    fun onViewChanged(view: ViewType) = _viewType.postValue(view)

    fun onCardNumberChanged(number: String) {
        _cardNumber.value = number
        getCardType(number)
            .takeIf { it != cardType.value }
            ?.let { type -> _cardType.value = type }
        maybeEnableButton()
    }

    fun onExpiryMonthChanged(month: String) {
        if (isMonthUpdateValid(month)) {
            _expiryMonth.value = month
            maybeEnableButton()
        }
    }

    fun onExpiryYearChanged(year: String) {
        if (isYearUpdateValid(year)) {
            _expiryYear.value = year
            maybeEnableButton()
        }
    }

    fun onCvvChanged(cvv: String) {
        if (isCvvUpdateValid(cvv, cardType.value)) {
            _cvv.value = cvv
            maybeEnableButton()
        }
    }

    fun onButtonPressed() = _isLoading.postValue(true)

    fun onPaymentRequestDone() = _isLoading.postValue(false)

    fun onErrorMessageReceived(message: String) = _errorMessage.postValue(message)

    fun onErrorDialogDone() = _errorMessage.postValue(null)

    fun areFieldsValid(): Boolean = cardNumber.value?.let {
        isCardNumberDigitsValid(it, cardType.value) && isCardNumberValid(cardNumber.value)
    } == true &&
            expiryYear.value?.let { isYearValid(it) } == true &&
            expiryMonth.value?.let { isMonthValid(it) } == true &&
            cvv.value?.let { isCvvValid(it, cardType.value) } == true

    private fun maybeEnableButton() {
        val isValid = areFieldsValid()
        if (isValid != buttonEnabled.value) _buttonEnabled.postValue(isValid)
    }
}

sealed interface ViewType {
    object PaymentDetails : ViewType
    data class ThreeDS(val url: String) : ViewType
    data class PaymentResult(val isSuccessful: Boolean) : ViewType
}