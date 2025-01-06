package com.muratguzel.trackyourtime.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratguzel.trackyourtime.data.dataSource.AuthDataSource
import com.muratguzel.trackyourtime.data.dataSource.CountdownDataSource
import com.muratguzel.trackyourtime.data.dataSource.SettingsDataSource
import com.muratguzel.trackyourtime.data.repo.AuthRepository
import com.muratguzel.trackyourtime.data.repo.CountDownRepository
import com.muratguzel.trackyourtime.data.repo.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(mAth: FirebaseAuth, mFireStore: FirebaseFirestore): AuthDataSource {
        return AuthDataSource(mAth, mFireStore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(ads: AuthDataSource): AuthRepository {
        return AuthRepository(ads)
    }

    @Provides
    @Singleton
    fun provideSettingsDataSource(mAth: FirebaseAuth, mFireStore: FirebaseFirestore): SettingsDataSource {
        return SettingsDataSource(mAth, mFireStore)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(sds:SettingsDataSource): SettingsRepository {
        return SettingsRepository(sds)
    }

    @Provides
    @Singleton
    fun provideCountDownDataSource(mAuth: FirebaseAuth, mFireStore: FirebaseFirestore): CountdownDataSource {
        return CountdownDataSource(mAuth, mFireStore)
    }

    @Provides
    @Singleton
    fun provideCountDownRepository(cds: CountdownDataSource): CountDownRepository {
        return CountDownRepository(cds)
    }
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}