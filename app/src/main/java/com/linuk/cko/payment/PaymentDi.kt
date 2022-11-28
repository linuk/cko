package com.linuk.cko.payment

import com.linuk.cko.api.PaymentRepository
import com.linuk.cko.api.PaymentUtils
import com.linuk.cko.data.PaymentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class PaymentDi {
    @Binds
    abstract fun paymentRepository(bound: PaymentRepositoryImpl): PaymentRepository

    @Binds
    abstract fun paymentUtils(bound: PaymentUtilsImpl): PaymentUtils
}