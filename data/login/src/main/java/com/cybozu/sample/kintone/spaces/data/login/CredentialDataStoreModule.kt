package com.cybozu.sample.kintone.spaces.data.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

private val Context.credentialDataStore: DataStore<Preferences> by preferencesDataStore(name = "credential")

@Module
@InstallIn(SingletonComponent::class)
internal class CredentialDataStoreModule {
    @Provides
    internal fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.credentialDataStore
}
