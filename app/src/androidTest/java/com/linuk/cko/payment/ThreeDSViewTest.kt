package com.linuk.cko.payment

import android.net.Uri
import android.webkit.WebResourceRequest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linuk.cko.payment.PaymentFixtures.buildRepository
import com.linuk.cko.payment.PaymentFixtures.buildUtils
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.FAILURE_PAYMENT_REDIRECTION_URL
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.SUCCESS_PAYMENT_REDIRECTION_URL
import com.linuk.cko.payment.views.handleRedirect
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class ThreeDSViewTest {

    @get:Rule
    val rule = createComposeRule()

    // make post value instantly reflect
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = buildRepository()
    private val utils = buildUtils()

    @Mock
    private lateinit var viewTypeObserver: Observer<ViewType>
    private lateinit var viewModel: PaymentViewModel

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        viewModel = PaymentViewModel(repository, utils)
    }

    @Test
    fun handleRedirectWithSuccessfulURL() {
        viewModel.viewType.observeForever(viewTypeObserver)
        verify(viewTypeObserver).onChanged(ViewType.PaymentDetails)
        assert(handleRedirect(viewModel, buildRequest(SUCCESS_PAYMENT_REDIRECTION_URL)))
        verify(viewTypeObserver).onChanged(ViewType.PaymentResult(isSuccessful = true))
    }

    @Test
    fun handleRedirectWithFailureURL() {
        viewModel.viewType.observeForever(viewTypeObserver)
        verify(viewTypeObserver).onChanged(ViewType.PaymentDetails)
        assert(handleRedirect(viewModel, buildRequest(FAILURE_PAYMENT_REDIRECTION_URL)))
        verify(viewTypeObserver).onChanged(ViewType.PaymentResult(isSuccessful = false))
    }

    @Test
    fun handleRedirectWithOtherURL() {
        viewModel.viewType.observeForever(viewTypeObserver)
        verify(viewTypeObserver).onChanged(ViewType.PaymentDetails)
        assert(!handleRedirect(viewModel, buildRequest("https://random_url")))
        verify(viewTypeObserver, never()).onChanged(any(ViewType.PaymentResult::class.java))
    }

    private fun buildRequest(url: String) = object : WebResourceRequest {
        override fun getUrl(): Uri = Uri.parse(url)
        override fun isForMainFrame() = false
        override fun isRedirect() = false
        override fun hasGesture() = false
        override fun getMethod() = ""
        override fun getRequestHeaders() = mutableMapOf<String, String>()
    }
}