package com.feduss.tomato.hilt

import com.feduss.tomatimer.business.TimerInteractor
import com.feduss.tomatimer.business.TimerInteractorImpl
import com.feduss.tomatimer.data.TimerRepository
import com.feduss.tomatimer.data.TimerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Binds the interface with its implementation
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindTimerRepository(
        timerRepositoryImpl: TimerRepositoryImpl
    ): TimerRepository

    @Binds
    abstract fun bindTimerInteractor(
        timerInteractorImpl: TimerInteractorImpl
    ): TimerInteractor
}