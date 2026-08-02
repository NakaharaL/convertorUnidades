package com.leonardonakahara.convertorunidades

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var gvTiposUnidades: GridView
    private lateinit var spUnidadeEntrada: Spinner
    private lateinit var spUnidadeSaida: Spinner
    private lateinit var etValorEntrada: EditText
    private lateinit var etValorSaida: EditText
    private lateinit var btnConverter: Button
    private lateinit var btnLimpar: Button
    private lateinit var btnAlternar: Button

    private lateinit var adapterCategorias: CategoriaAdapter
    private lateinit var prefs: PreferencesHelper
    private lateinit var storageManager: StorageManager
    private lateinit var dbHelper: MeuDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Instancia os gerenciadores
        prefs = PreferencesHelper(this)
        storageManager = StorageManager(this)
        dbHelper = MeuDatabaseHelper(this)

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
        etValorEntrada = findViewById(R.id.etValorEntrada)
        etValorSaida = findViewById(R.id.etValorSaida)
        btnConverter = findViewById(R.id.btnConverter)
        btnLimpar = findViewById(R.id.btnLimpar)
        btnAlternar = findViewById(R.id.btnAlternar)

        val listaCategorias = listOf(
            CategoriaUnidade("comprimento", getString(R.string.comprimento), android.R.drawable.ic_menu_compass),
            CategoriaUnidade("velocidade", getString(R.string.velocidade), android.R.drawable.ic_menu_directions),
            CategoriaUnidade("temperatura", getString(R.string.temperatura), android.R.drawable.ic_menu_day),
            CategoriaUnidade("volume", getString(R.string.volume), android.R.drawable.ic_menu_gallery),
            CategoriaUnidade("peso", getString(R.string.peso), android.R.drawable.ic_menu_sort_by_size)
        )

        adapterCategorias = CategoriaAdapter(this, listaCategorias)
        gvTiposUnidades.adapter = adapterCategorias

        // Restaurando valores das SharedPreferences
        val posCategoriaSalva = prefs.obterCategoriaPosicao()
        val posEntradaSalva = prefs.obterUnidadeEntradaPosicao()
        val posSaidaSalva = prefs.obterUnidadeSaidaPosicao()
        val valorSalvo = prefs.obterValorEntrada()

        adapterCategorias.setItemSelecionado(posCategoriaSalva)
        atualizarSpinnersUnidade(listaCategorias[posCategoriaSalva].id)

        spUnidadeEntrada.setSelection(posEntradaSalva)
        spUnidadeSaida.setSelection(posSaidaSalva)
        if (valorSalvo.isNotEmpty()) {
            etValorEntrada.setText(valorSalvo)
        }

        // --- EXECUTANDO LEITURA DO ARQUIVO DA PASTA res/raw VIA openRawResource() ---
        lerArquivoRaw()

        gvTiposUnidades.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            adapterCategorias.setItemSelecionado(position)
            val categoriaSelecionada = listaCategorias[position]
            atualizarSpinnersUnidade(categoriaSelecionada.id)
            Toast.makeText(this, "Categoria: ${categoriaSelecionada.nome}", Toast.LENGTH_SHORT).show()
        }

        btnConverter.setOnClickListener {
            val valor = etValorEntrada.text.toString()

            // 1. Salva nas SharedPreferences
            prefs.salvarEstado(
                valorEntrada = valor,
                categoriaPos = gvTiposUnidades.checkedItemPosition.takeIf { it != GridView.INVALID_POSITION } ?: 0,
                unidadeEntradaPos = spUnidadeEntrada.selectedItemPosition,
                unidadeSaidaPos = spUnidadeSaida.selectedItemPosition
            )

            // 2. Salva em arquivo interno (FileOutputStream)
            storageManager.salvarEmArquivoInterno("dados_internos.txt", valor)

            // 3. Lê do arquivo interno (FileInputStream + InputStreamReader)
            val textoLidoInterno = storageManager.lerDeArquivoInterno("dados_internos.txt")

            // 4. Salva no armazenamento externo (getExternalStorageDirectory)
            storageManager.salvarEmArquivoExterno("dados_externos.txt", valor)

            // 5. Insere registro no banco SQLite via SQLiteOpenHelper
            dbHelper.inserirRegistro(valor)

            Toast.makeText(this, "Arquivo Interno Lido: $textoLidoInterno", Toast.LENGTH_SHORT).show()

            // 6. Abre a SegundaActivity para comprovar compartilhamento via getSharedPreferences()
            val intent = Intent(this, SegundaActivity::class.java)
            startActivity(intent)
        }

        btnAlternar.setOnClickListener {
            val posEntrada = spUnidadeEntrada.selectedItemPosition
            val posSaida = spUnidadeSaida.selectedItemPosition
            spUnidadeEntrada.setSelection(posSaida)
            spUnidadeSaida.setSelection(posEntrada)
        }

        btnLimpar.setOnClickListener {
            etValorEntrada.text.clear()
            etValorSaida.text.clear()
            prefs.limpar()
        }
    }

    // Leitura do recurso res/raw/raw_sample.txt usando openRawResource
    private fun lerArquivoRaw() {
        try {
            val inputStream = resources.openRawResource(R.raw.raw_sample)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val conteudo = reader.use { it.readText() }
            println("=== CONTEÚDO LIDO DO RES/RAW ===")
            println(conteudo)
        } catch (e: Exception) {
            e.printStackTrace()
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