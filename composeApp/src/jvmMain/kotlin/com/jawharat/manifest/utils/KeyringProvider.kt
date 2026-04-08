package com.jawharat.manifest.utils

import com.github.javakeyring.Keyring


interface IKeyringProvider : AutoCloseable {
    fun getKeyring(): Keyring?
}

class KeyringProvider(private val keyring: Keyring) : IKeyringProvider {
    override fun getKeyring(): Keyring = keyring
    override fun close() = keyring.close()
}

class NoOpKeyringProvider : IKeyringProvider {
    override fun getKeyring(): Keyring? = null
    override fun close() {}
}
