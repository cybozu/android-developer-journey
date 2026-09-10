package com.cybozu.sample.kintone.spaces.data.login

interface CredentialRepository {
    suspend fun saveCredential(
        userName: String,
        password: String,
    )

    suspend fun getCredential(): Credential?
}
