package com.cybozu.sample.kintone.spaces.data.login

data class EncryptedData(
    val cipherText: ByteArray,
    val iv: ByteArray,
)
