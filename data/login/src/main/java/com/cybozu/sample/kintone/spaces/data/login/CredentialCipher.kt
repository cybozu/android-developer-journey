package com.cybozu.sample.kintone.spaces.data.login

interface CredentialCipher {
    fun encrypt(plainText: String): EncryptedData

    fun decrypt(encryptedData: EncryptedData): String
}
