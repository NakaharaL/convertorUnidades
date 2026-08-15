package com.leonardonakahara.convertorunidades

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.MaterialToolbar
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
    private lateinit var btnEnviarSMS: Button
    private lateinit var toolbar: MaterialToolbar

    private lateinit var adapterCategorias: CategoriaAdapter
    private lateinit var prefs: PreferencesHelper
    private lateinit var storageManager: StorageManager
    private lateinit var dbHelper: MeuDatabaseHelper

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQ_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configuração da Toolbar para exibir o Menu
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
        btnEnviarSMS = findViewById(R.id.btnEnviarSMS)

        val listaCategorias = listOf(
            CategoriaUnidade("comprimento", getString(R.string.comprimento), android.R.drawable.ic_menu_compass),
            CategoriaUnidade("velocidade", getString(R.string.velocidade), android.R.drawable.ic_menu_directions),
            CategoriaUnidade("temperatura", getString(R.string.temperatura), android.R.drawable.ic_menu_day),
            CategoriaUnidade("volume", getString(R.string.volume), android.R.drawable.ic_menu_gallery),
            CategoriaUnidade("peso", getString(R.string.peso), android.R.drawable.ic_menu_sort_by_size)
        )

        adapterCategorias = CategoriaAdapter(this, listaCategorias)
        gvTiposUnidades.adapter = adapterCategorias

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

        lerArquivoRaw()

        gvTiposUnidades.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            adapterCategorias.setItemSelecionado(position)
            val categoriaSelecionada = listaCategorias[position]
            atualizarSpinnersUnidade(categoriaSelecionada.id)
            Toast.makeText(this, "Categoria: ${categoriaSelecionada.nome}", Toast.LENGTH_SHORT).show()
        }

        btnConverter.setOnClickListener {
            val valor = etValorEntrada.text.toString()

            obterLocalizacaoDoUsuario()

            prefs.salvarEstado(
                valorEntrada = valor,
                categoriaPos = gvTiposUnidades.checkedItemPosition.takeIf { it != GridView.INVALID_POSITION } ?: 0,
                unidadeEntradaPos = spUnidadeEntrada.selectedItemPosition,
                unidadeSaidaPos = spUnidadeSaida.selectedItemPosition
            )

            storageManager.salvarEmArquivoInterno("dados_internos.txt", valor)
            val textoLidoInterno = storageManager.lerDeArquivoInterno("dados_internos.txt")
            storageManager.salvarEmArquivoExterno("dados_externos.txt", valor)
            dbHelper.inserirRegistro(valor)

            Toast.makeText(this, "Arquivo Interno Lido: $textoLidoInterno", Toast.LENGTH_SHORT).show()

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

        btnEnviarSMS.setOnClickListener {
            solicitarTelefoneEEnviarSMS()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_mapa -> {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_sobre -> {
                Toast.makeText(this, "Aplicativo Conversor de Unidades v1.0", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_limpar_historico -> {
                Toast.makeText(this, "Histórico limpo!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun obterLocalizacaoDoUsuario() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQ_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Toast.makeText(
                    this,
                    "Localização da Conversão:\nLat: ${location.latitude}, Lng: ${location.longitude}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Não foi possível obter a localização atual.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun solicitarTelefoneEEnviarSMS() {
        val valorEntrada = etValorEntrada.text.toString().trim()
        val valorSaida = etValorSaida.text.toString().trim()
        val unidadeEntrada = spUnidadeEntrada.selectedItem?.toString() ?: ""
        val unidadeSaida = spUnidadeSaida.selectedItem?.toString() ?: ""

        if (valorEntrada.isEmpty() && valorSaida.isEmpty()) {
            Toast.makeText(this, "Realize uma conversão ou insira os dados antes de enviar.", Toast.LENGTH_SHORT).show()
            return
        }

        val mensagem = "Conversão: $valorEntrada $unidadeEntrada = $valorSaida $unidadeSaida"

        val inputTelefone = EditText(this).apply {
            hint = "Ex: 11999999999"
            inputType = InputType.TYPE_CLASS_PHONE
        }

        AlertDialog.Builder(this)
            .setTitle("Enviar SMS")
            .setMessage("Digite o número de telefone do destinatário:")
            .setView(inputTelefone)
            .setPositiveButton("Enviar") { _, _ ->
                val telefone = inputTelefone.text.toString().trim()
                if (telefone.isNotEmpty()) {
                    abrirAppDeSMS(telefone, mensagem)
                } else {
                    Toast.makeText(this, "Número de telefone inválido.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirAppDeSMS(telefone: String, mensagem: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$telefone".toUri()
            putExtra("sms_body", mensagem)
        }

        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(this, "Não foi possível abrir o aplicativo de SMS.", Toast.LENGTH_SHORT).show()
        }
    }

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