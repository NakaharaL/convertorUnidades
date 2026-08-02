package com.leonardonakahara.convertorunidades

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SegundaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            textSize = 18f
            setPadding(32, 32, 32, 32)
        }
        setContentView(textView)

        val sharedPreferences = getSharedPreferences("conversor_prefs", MODE_PRIVATE)
        val valorEntrada = sharedPreferences.getString("key_valor_entrada", "Nenhum valor encontrado")
        val posCategoria = sharedPreferences.getInt("key_categoria_posicao", 0)

        textView.text = buildString {
            append("=== DADOS LIDOS VIA getSharedPreferences() ===\n\n")
            append("Valor salvo: $valorEntrada\n")
            append("Posição da categoria: $posCategoria")
        }
    }
}