package com.leonardonakahara.convertorunidades

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var etEndereco: EditText
    private lateinit var etLat: EditText
    private lateinit var etLng: EditText
    private lateinit var btnBuscarEndereco: Button
    private lateinit var btnBuscarCoord: Button
    private lateinit var btnBuscarJson: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        etEndereco = findViewById(R.id.etEndereco)
        etLat = findViewById(R.id.etLat)
        etLng = findViewById(R.id.etLng)
        btnBuscarEndereco = findViewById(R.id.btnBuscarEndereco)
        btnBuscarCoord = findViewById(R.id.btnBuscarCoord)
        btnBuscarJson = findViewById(R.id.btnBuscarJson)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnBuscarEndereco.setOnClickListener {
            val enderecoTexto = etEndereco.text.toString().trim()
            if (enderecoTexto.isNotEmpty()) {
                geocodificarEndereco(enderecoTexto)
            } else {
                Toast.makeText(this, "Digite um endereço", Toast.LENGTH_SHORT).show()
            }
        }

        btnBuscarCoord.setOnClickListener {
            val latStr = etLat.text.toString().trim()
            val lngStr = etLng.text.toString().trim()
            if (latStr.isNotEmpty() && lngStr.isNotEmpty()) {
                val lat = latStr.toDoubleOrNull()
                val lng = lngStr.toDoubleOrNull()
                if (lat != null && lng != null) {
                    geocodificarReversa(lat, lng)
                } else {
                    Toast.makeText(this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha latitude e longitude", Toast.LENGTH_SHORT).show()
            }
        }

        btnBuscarJson.setOnClickListener {
            consumirApiExterna()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Local Selecionado"))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            etLat.setText(latLng.latitude.toString())
            etLng.setText(latLng.longitude.toString())

            geocodificarReversa(latLng.latitude, latLng.longitude)
        }
    }

    private fun geocodificarEndereco(nomeEndereco: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(nomeEndereco, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val location = addresses[0]
                            val latLng = LatLng(location.latitude, location.longitude)
                            runOnUiThread {
                                atualizarMapa(latLng, nomeEndereco)
                            }
                        }
                    }
                })
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(nomeEndereco, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)
                    atualizarMapa(latLng, nomeEndereco)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao buscar endereço: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun geocodificarReversa(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val latLng = LatLng(lat, lng)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val endereco = addresses[0].getAddressLine(0) ?: "Endereço não encontrado"
                            runOnUiThread {
                                atualizarMapa(latLng, endereco)
                                etEndereco.setText(endereco)
                            }
                        }
                    }
                })
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val endereco = addresses[0].getAddressLine(0) ?: "Endereço não encontrado"
                    atualizarMapa(latLng, endereco)
                    etEndereco.setText(endereco)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro na geocodificação reversa: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarMapa(latLng: LatLng, titulo: String) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng).title(titulo))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun consumirApiExterna() {
        val client = OkHttpClient()
        val url = "https://api.github.com/users/octocat"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MapActivity, "Erro HTTP: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonBody = response.body?.string()
                if (response.isSuccessful && jsonBody != null) {
                    val jsonObject = JSONObject(jsonBody)
                    val login = jsonObject.optString("login", "N/A")
                    val name = jsonObject.optString("name", "N/A")
                    val bio = jsonObject.optString("bio", "N/A")

                    runOnUiThread {
                        Toast.makeText(
                            this@MapActivity,
                            "JSON Recebido!\nUsuário: $login\nNome: $name\nBio: $bio",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }
}