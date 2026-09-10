package com.cybozu.sample.kintone.spaces.data.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CredentialDataModule {
    @Binds
    internal abstract fun bindCredentialRepository(credentialRepositoryImpl: CredentialRepositoryImpl): CredentialRepository

    @Binds
    internal abstract fun bindCredentialCipher(credentialCipherImpl: CredentialCipherImpl): CredentialCipher
}
