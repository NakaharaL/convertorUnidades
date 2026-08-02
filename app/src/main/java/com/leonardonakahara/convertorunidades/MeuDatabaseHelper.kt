package com.leonardonakahara.convertorunidades

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MeuDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    NOME_BANCO,
    null,
    VERSAO_BANCO
) {

    companion object {
        private const val NOME_BANCO = "conversor_db.db"
        private const val VERSAO_BANCO = 1
        const val TABELA_HISTORICO = "historico"
        const val COLUNA_ID = "id"
        const val COLUNA_VALOR = "valor"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABELA_HISTORICO (
                $COLUNA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUNA_VALOR TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABELA_HISTORICO")
        onCreate(db)
    }

    fun inserirRegistro(valor: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUNA_VALOR, valor)
        }
        return db.insert(TABELA_HISTORICO, null, values)
    }
}