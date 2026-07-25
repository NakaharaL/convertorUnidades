package com.leonardonakahara.convertorunidades

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var gvTiposUnidades: GridView
    private lateinit var spUnidadeEntrada: Spinner
    private lateinit var spUnidadeSaida: Spinner
    private lateinit var adapterCategorias: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        gvTiposUnidades = findViewById(R.id.gvTiposUnidades)
        spUnidadeEntrada = findViewById(R.id.spUnidadeEntrada)
        spUnidadeSaida = findViewById(R.id.spUnidadeSaida)

        val listaCategorias = listOf(
            CategoriaUnidade("comprimento", getString(R.string.comprimento), android.R.drawable.ic_menu_compass),
            CategoriaUnidade("velocidade", getString(R.string.velocidade), android.R.drawable.ic_menu_directions),
            CategoriaUnidade("temperatura", getString(R.string.temperatura), android.R.drawable.ic_menu_day),
            CategoriaUnidade("volume", getString(R.string.volume), android.R.drawable.ic_menu_gallery),
            CategoriaUnidade("peso", getString(R.string.peso), android.R.drawable.ic_menu_sort_by_size)
        )

        adapterCategorias = CategoriaAdapter(this, listaCategorias)
        gvTiposUnidades.adapter = adapterCategorias

        atualizarSpinnersUnidade("comprimento")

        gvTiposUnidades.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            adapterCategorias.setItemSelecionado(position)

            val categoriaSelecionada = listaCategorias[position]
            atualizarSpinnersUnidade(categoriaSelecionada.id)

            Toast.makeText(this, "Categoria: ${categoriaSelecionada.nome}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarSpinnersUnidade(tipoCategoria: String) {
        val opcoes = when (tipoCategoria) {
            "comprimento" -> listOf("Metros (m)", "Quilômetros (km)", "Centímetros (cm)", "Milhas")
            "velocidade" -> listOf("km/h", "m/s", "Mph")
            "temperatura" -> listOf("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)")
            "volume" -> listOf("Litros (L)", "Mililitros (mL)", "Metros Cúbicos (m³)")
            "peso" -> listOf("Quilogramas (kg)", "Gramas (g)", "Libras (lb)")
            else -> emptyList()
        }

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opcoes
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spUnidadeEntrada.adapter = spinnerAdapter
        spUnidadeSaida.adapter = spinnerAdapter
    }
}