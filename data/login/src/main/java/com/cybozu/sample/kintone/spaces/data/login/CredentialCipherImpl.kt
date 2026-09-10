package com.cybozu.sample.kintone.spaces.data.login

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

internal class CredentialCipherImpl
    @Inject
    constructor() : CredentialCipher {
        private val keyStore =
            KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
        private val existingKey = keyStore.getKey(ALIAS, null) as? SecretKey
        private val secretKey = existingKey ?: generateKey()

        private fun generateKey(): SecretKey {
            val keyGenerator =
                KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
                )
            val spec =
                KeyGenParameterSpec
                    .Builder(
                        ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()

            keyGenerator.init(spec)
            return keyGenerator.generateKey()
        }

        override fun encrypt(plainText: String): EncryptedData {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            return EncryptedData(cipherText = cipherText, iv = iv)
        }

        override fun decrypt(encryptedData: EncryptedData): String {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, encryptedData.iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val plainBytes = cipher.doFinal(encryptedData.cipherText)
            return String(plainBytes, Charsets.UTF_8)
        }

        private companion object {
            private const val ALIAS = "credential_key"
            private const val TRANSFORMATION = "AES/GCM/NoPadding"
        }
    }
