package com.leonardonakahara.convertorunidades

import android.content.Context
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class StorageManager(private val context: Context) {

    fun salvarEmArquivoInterno(nomeArquivo: String, conteudo: String): Boolean {
        return try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(nomeArquivo, Context.MODE_PRIVATE)
            fileOutputStream.write(conteudo.toByteArray())
            fileOutputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun lerDeArquivoInterno(nomeArquivo: String): String {
        return try {
            val fileInputStream: FileInputStream = context.openFileInput(nomeArquivo)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var linha: String?

            while (bufferedReader.readLine().also { linha = it } != null) {
                stringBuilder.append(linha).append("\n")
            }

            fileInputStream.close()
            stringBuilder.toString().trim()
        } catch (e: Exception) {
            e.printStackTrace()
            "Erro ao ler arquivo interno."
        }
    }

    fun salvarEmArquivoExterno(nomeArquivo: String, conteudo: String): Boolean {
        return try {
            @Suppress("DEPRECATION")
            val pastaExterna = Environment.getExternalStorageDirectory()
            val arquivo = File(pastaExterna, nomeArquivo)
            val fileOutputStream = FileOutputStream(arquivo)

            fileOutputStream.write(conteudo.toByteArray())
            fileOutputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}