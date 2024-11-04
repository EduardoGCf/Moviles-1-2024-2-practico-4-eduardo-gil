package com.example.practico4.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.practico4.models.Contacto
import com.example.practico4.network.ApiClient
import com.example.practico4.network.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactoViewModel : ViewModel() {
    private val _contacto = MutableLiveData<Contacto?>()
    val contacto: LiveData<Contacto?> get() = _contacto

    private val _contactos = MutableLiveData<List<Contacto>>()
    val contactos: LiveData<List<Contacto>> get() = _contactos

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchContactos() {
        Log.d("ContactoViewModel", "Iniciando fetchContactos")

        ApiClient.getClient().create(ApiService::class.java).getContactos().enqueue(object : Callback<List<Contacto>> {
            override fun onResponse(call: Call<List<Contacto>>, response: Response<List<Contacto>>) {
                Log.d("ContactoViewModel", "Respuesta recibida de la API")
                if (response.isSuccessful) {
                    val contactos = response.body()
                    Log.d("ContactoViewModel", "Datos recibidos: ${Gson().toJson(contactos)}")
                    _contactos.value = contactos ?: emptyList()
                } else {
                    Log.e("ContactoViewModel", "Error en la respuesta: ${response.errorBody()?.string()}")
                    _error.value = "Error en la carga de contactos"
                }
            }

            override fun onFailure(call: Call<List<Contacto>>, t: Throwable) {
                Log.e("ContactoViewModel", "Error en la llamada a la API", t)
                _error.value = "No se pudo cargar los contactos"
            }
        })
    }

    fun fetchContactoById(contactoId: Int) {
        ApiClient.getClient().create(ApiService::class.java).getContactoById(contactoId).enqueue(object : Callback<Contacto> {
            override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                if (response.isSuccessful) {
                    _contacto.value = response.body()
                } else {
                    _error.value = "Error al cargar el contacto"
                }
            }

            override fun onFailure(call: Call<Contacto>, t: Throwable) {
                _error.value = "No se pudo cargar el contacto"
            }
        })
    }


    fun searchContactos(query: String) {
        Log.d("ContactoViewModel", "Iniciando búsqueda de contactos con query: $query")

        ApiClient.getClient().create(ApiService::class.java).searchContactos(query).enqueue(object : Callback<List<Contacto>> {
            override fun onResponse(call: Call<List<Contacto>>, response: Response<List<Contacto>>) {
                Log.d("ContactoViewModel", "Respuesta de búsqueda recibida de la API")
                if (response.isSuccessful) {
                    val contactos = response.body()
                    Log.d("ContactoViewModel", "Datos de búsqueda recibidos: ${Gson().toJson(contactos)}")
                    _contactos.value = contactos ?: emptyList()
                } else {
                    Log.e("ContactoViewModel", "Error en la respuesta de búsqueda: ${response.errorBody()?.string()}")
                    _error.value = "Error en la búsqueda de contactos"
                }
            }

            override fun onFailure(call: Call<List<Contacto>>, t: Throwable) {
                Log.e("ContactoViewModel", "Error en la llamada a la API de búsqueda", t)
                _error.value = "No se pudo buscar los contactos"
            }
        })
    }

    fun deleteContacto(contactoId: Int) {
        ApiClient.getClient().create(ApiService::class.java).deleteContacto(contactoId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) fetchContactos() // Refrescar lista
            }
            override fun onFailure(call: Call<Void>, t: Throwable) { /* Manejo de errores */ }
        })
    }

}