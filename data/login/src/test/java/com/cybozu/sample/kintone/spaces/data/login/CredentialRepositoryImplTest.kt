package com.cybozu.sample.kintone.spaces.data.login

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.kotest.matchers.shouldBe
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class CredentialRepositoryImplTest {
    private lateinit var file: File
    private lateinit var repository: CredentialRepositoryImpl

    @Before
    fun setUp() {
        file = File.createTempFile("test_credential", ".preferences_pb")
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.Unconfined),
                produceFile = { file }
            )
        repository =
            CredentialRepositoryImpl(
                dataStore = dataStore,
                credentialCipher = FakeCredentialCipher()
            )
    }

    @After
    fun tearDown() {
        file.delete()
    }

    @Test
    fun `保存前はnullが返る`() =
        runTest {
            repository.getCredential() shouldBe null
        }

    @Test
    fun `保存した内容を取得できる`() =
        runTest {
            repository.saveCredential(userName = "taro", password = "password123")

            val credential = repository.getCredential()

            credential shouldBe Credential(userName = "taro", password = "password123")
        }

    @Test
    fun `保存し直すと最新の内容で上書きされる`() =
        runTest {
            repository.saveCredential(userName = "taro", password = "password123")
            repository.saveCredential(userName = "jiro", password = "password456")

            val credential = repository.getCredential()

            credential shouldBe Credential(userName = "jiro", password = "password456")
        }
}

private class FakeCredentialCipher : CredentialCipher {
    override fun encrypt(plainText: String): EncryptedData =
        EncryptedData(
            cipherText = plainText.toByteArray(Charsets.UTF_8),
            iv = ByteArray(0)
        )

    override fun decrypt(encryptedData: EncryptedData): String = String(encryptedData.cipherText, Charsets.UTF_8)
}
