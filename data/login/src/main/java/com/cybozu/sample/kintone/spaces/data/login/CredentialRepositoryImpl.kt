package com.cybozu.sample.kintone.spaces.data.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.Base64
import javax.inject.Inject
import kotlinx.coroutines.flow.first

internal class CredentialRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val credentialCipher: CredentialCipher,
    ) : CredentialRepository {
        override suspend fun saveCredential(
            userName: String,
            password: String,
        ) {
            val encryptedUserName = credentialCipher.encrypt(userName)
            val encryptedPassword = credentialCipher.encrypt(password)

            dataStore.edit { preferences ->
                preferences[USER_NAME_CIPHER_KEY] = Base64.getEncoder().encodeToString(encryptedUserName.cipherText)
                preferences[USER_NAME_IV_KEY] = Base64.getEncoder().encodeToString(encryptedUserName.iv)
                preferences[PASSWORD_CIPHER_KEY] = Base64.getEncoder().encodeToString(encryptedPassword.cipherText)
                preferences[PASSWORD_IV_KEY] = Base64.getEncoder().encodeToString(encryptedPassword.iv)
            }
        }

        override suspend fun getCredential(): Credential? {
            val preferences = dataStore.data.first()

            val userNameCipherText = preferences[USER_NAME_CIPHER_KEY] ?: return null
            val userNameIv = preferences[USER_NAME_IV_KEY] ?: return null
            val passwordCipherText = preferences[PASSWORD_CIPHER_KEY] ?: return null
            val passwordIv = preferences[PASSWORD_IV_KEY] ?: return null

            val userName =
                credentialCipher.decrypt(
                    EncryptedData(
                        cipherText = Base64.getDecoder().decode(userNameCipherText),
                        iv = Base64.getDecoder().decode(userNameIv)
                    )
                )
            val password =
                credentialCipher.decrypt(
                    EncryptedData(
                        cipherText = Base64.getDecoder().decode(passwordCipherText),
                        iv = Base64.getDecoder().decode(passwordIv)
                    )
                )

            return Credential(userName = userName, password = password)
        }

        private companion object {
            val USER_NAME_CIPHER_KEY = stringPreferencesKey("user_name_cipher")
            val USER_NAME_IV_KEY = stringPreferencesKey("user_name_iv")
            val PASSWORD_CIPHER_KEY = stringPreferencesKey("password_cipher")
            val PASSWORD_IV_KEY = stringPreferencesKey("password_iv")
        }
    }
