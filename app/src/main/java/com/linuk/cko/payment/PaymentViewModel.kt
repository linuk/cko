package com.linuk.cko.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linuk.cko.data.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val utils: PaymentUtils,
) : ViewModel() {
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

    fun onViewChanged(view: ViewType) {
        _viewType.postValue(view)
    }

    @TestOnly
    fun onViewChangedOnMainThread(view: ViewType) {
        _viewType.value = view
    }

    fun onCardNumberChanged(number: String) {
        _cardNumber.value = number
        utils.getCardType(number)
            .takeIf { it != cardType.value }
            ?.let { type -> _cardType.value = type }
        maybeEnableButton()
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
            utils.isCardNumberDigitsValid(it.length, cardType.value) &&
                    utils.isCardNumberValid(cardNumber.value)
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