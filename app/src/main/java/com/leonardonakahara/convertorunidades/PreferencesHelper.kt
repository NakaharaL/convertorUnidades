package com.leonardonakahara.convertorunidades

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit // Importante: Extensão do Core-KTX

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("conversor_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_VALOR_ENTRADA = "key_valor_entrada"
        private const val KEY_CATEGORIA_POSICAO = "key_categoria_posicao"
        private const val KEY_UNIDADE_ENTRADA_POS = "key_unidade_entrada_pos"
        private const val KEY_UNIDADE_SAIDA_POS = "key_unidade_saida_pos"
    }

    fun salvarEstado(valorEntrada: String, categoriaPos: Int, unidadeEntradaPos: Int, unidadeSaidaPos: Int) {
        // A extensão 'edit' aplica as alterações automaticamente (commit = false usa .apply() por padrão)
        sharedPreferences.edit {
            putString(KEY_VALOR_ENTRADA, valorEntrada)
            putInt(KEY_CATEGORIA_POSICAO, categoriaPos)
            putInt(KEY_UNIDADE_ENTRADA_POS, unidadeEntradaPos)
            putInt(KEY_UNIDADE_SAIDA_POS, unidadeSaidaPos)
        }
    }

    fun obterValorEntrada(): String = sharedPreferences.getString(KEY_VALOR_ENTRADA, "") ?: ""
    fun obterCategoriaPosicao(): Int = sharedPreferences.getInt(KEY_CATEGORIA_POSICAO, 0)
    fun obterUnidadeEntradaPosicao(): Int = sharedPreferences.getInt(KEY_UNIDADE_ENTRADA_POS, 0)
    fun obterUnidadeSaidaPosicao(): Int = sharedPreferences.getInt(KEY_UNIDADE_SAIDA_POS, 0)

    fun limpar() {
        sharedPreferences.edit {
            clear()
        }
    }
}