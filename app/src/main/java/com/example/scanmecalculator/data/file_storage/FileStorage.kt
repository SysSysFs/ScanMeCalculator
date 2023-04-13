package com.example.scanmecalculator.data.file_storage

import android.app.Application
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.scanmecalculator.domain.model.TextParserInfo
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class FileStorage(
    private val app: Application
) {
    private val mainKeyAlias by lazy {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        MasterKeys.getOrCreate(keyGenParameterSpec)
    }

    private val savedFile by lazy {
        val context = app.applicationContext
        val path = context.filesDir
        val file = File(path, FILE_NAME_TEXT_PARSER_INFO)
        file
    }

    private val encryptedFile by lazy {
        //Create the encrypted file
        EncryptedFile.Builder(
            savedFile,
            app,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }

    fun saveTextParserInfo(textParserInfo: TextParserInfo) {
        val list = readTextParserInfoList().toMutableList().apply {
            add(textParserInfo)
        }

        val jsonText = Json.encodeToString(
            serializer = ListSerializer(TextParserInfo.serializer()),
            value = list
        )

        if (savedFile.exists()) {
            savedFile.delete()
        }

        encryptedFile.openFileOutput().use {
            it.write(jsonText.toByteArray(StandardCharsets.UTF_8))
            it.flush()
        }
    }

    fun readTextParserInfoList(): List<TextParserInfo> {
        var text = ""
        encryptedFile.openFileInput().use { fileInputStream ->
            val reader = BufferedReader(InputStreamReader(fileInputStream))
            reader.forEachLine { line ->
                text += "$line \n"
            }
        }
        return Json.decodeFromString(
            deserializer = ListSerializer(TextParserInfo.serializer()),
            string = text
        )
    }

    companion object {
        const val FILE_NAME_TEXT_PARSER_INFO = "text-parser-info-list.json"
    }
}