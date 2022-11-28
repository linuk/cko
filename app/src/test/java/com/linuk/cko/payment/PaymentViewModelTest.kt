package com.linuk.cko.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.linuk.cko.api.PaymentRepository
import com.linuk.cko.api.PaymentUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaymentViewModelTest {
    // make post value instantly reflect
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(PaymentRepository::class.java)
    private val utils = mock(PaymentUtils::class.java)
    private lateinit var viewModel: PaymentViewModel

    @Mock
    private lateinit var viewTypeObserver: Observer<ViewType>

    @Mock
    private lateinit var stringObserver: Observer<String?>

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        viewModel = PaymentViewModel(repository, utils)
    }

    @Test
    fun onViewChanged() {
        viewModel.viewType.observeForever(viewTypeObserver)
        // default to PaymentDetails
        verify(viewTypeObserver).onChanged(ViewType.PaymentDetails)

        viewModel.onViewChanged(ViewType.ThreeDS(""))
        verify(viewTypeObserver).onChanged(ViewType.ThreeDS(""))

        viewModel.onViewChanged(ViewType.PaymentResult(true))
        verify(viewTypeObserver).onChanged(ViewType.PaymentResult(true))
    }

    @Test
    fun `onCardNumberChanged should update card number when card number update is valid`() {
        viewModel.cardNumber.observeForever(stringObserver)
        `when`(utils.getCardType(anyString())).thenReturn(CardType.MASTER)
        `when`(utils.isCardNumberUpdateValid(anyInt(), any(CardType::class.java))).thenReturn(true)

        // default to empty string
        verify(stringObserver).onChanged("")
        viewModel.onCardNumberChanged("1234")
        verify(stringObserver).onChanged("1234")
    }

    @Test
    fun `onCardNumberChanged should not update card number when card number update is invalid`() {
        viewModel.cardNumber.observeForever(stringObserver)
        `when`(utils.getCardType(anyString())).thenReturn(CardType.MASTER)
        `when`(utils.isCardNumberUpdateValid(anyInt(), any(CardType::class.java))).thenReturn(false)

        // default to empty string
        verify(stringObserver).onChanged("")
        viewModel.onCardNumberChanged("1234")
        verify(stringObserver).onChanged(any())
    }

    @Test
    fun `onExpiryMonthChanged should update month when month update is valid`() {
        viewModel.expiryMonth.observeForever(stringObserver)
        `when`(utils.isMonthUpdateValid(anyString())).thenReturn(true)

        viewModel.onExpiryMonthChanged("12")
        verify(stringObserver).onChanged("12")
    }

    @Test
    fun `onExpiryMonthChanged should not update month when month update is invalid`() {
        viewModel.expiryMonth.observeForever(stringObserver)
        `when`(utils.isMonthUpdateValid(anyString())).thenReturn(false)

        viewModel.onExpiryMonthChanged("12")
        verify(stringObserver, never()).onChanged("12")
    }

    @Test
    fun `onExpiryYearChanged should update year when year update is valid`() {
        viewModel.expiryYear.observeForever(stringObserver)
        `when`(utils.isYearUpdateValid(anyString())).thenReturn(true)

        viewModel.onExpiryYearChanged("2022")
        verify(stringObserver).onChanged("2022")
    }

    @Test
    fun `onExpiryYearChanged should not update year when year update is invalid`() {
        viewModel.expiryYear.observeForever(stringObserver)
        `when`(utils.isYearUpdateValid(anyString())).thenReturn(false)

        viewModel.onExpiryYearChanged("2022")
        verify(stringObserver, never()).onChanged("2022")
    }

    @Test
    fun `onCvvChanged should update year when cvv update is valid`() {
        viewModel.cvv.observeForever(stringObserver)
        `when`(utils.isCvvUpdateValid(anyString(), any(CardType::class.java))).thenReturn(true)

        viewModel.onCvvChanged("123")
        verify(stringObserver).onChanged("123")
    }

    @Test
    fun `onCvvChanged should not update year when cvv update is invalid`() {
        viewModel.cvv.observeForever(stringObserver)
        `when`(utils.isCvvUpdateValid(anyString(), any(CardType::class.java))).thenReturn(false)

        viewModel.onCvvChanged("123")
        verify(stringObserver, never()).onChanged("123")
    }
}