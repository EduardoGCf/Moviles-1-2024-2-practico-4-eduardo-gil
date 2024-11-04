package com.example.practico4.network

import com.example.practico4.models.Contacto
import com.example.practico4.models.Email
import com.example.practico4.models.Telefono
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Contactos
    @GET("personas")
    fun getContactos(): Call<List<Contacto>>

    @GET("search")
    fun searchContactos(@Query("q") query: String): Call<List<Contacto>>

    @POST("personas")
    fun addContacto(@Body contacto: Contacto): Call<Contacto>

    @PUT("personas/{id}")
    fun updateContacto(@Path("id") id: Int, @Body contacto: Contacto): Call<Contacto>

    @DELETE("personas/{id}")
    fun deleteContacto(@Path("id") id: Int): Call<Void>

    @GET("personas/{id}")
    fun getContactoById(@Path("id") id: Int): Call<Contacto>

    // Teléfonos
    @GET("phones")
    fun getListaTelefonos(): Call<List<Telefono>>

    @POST("phones")
    fun addTelefono(@Body telefono: Telefono): Call<Telefono>


    @Multipart
    @POST("personas/{idcontacto}/profile-picture")
    fun uploadProfilePicture(
        @Path("idcontacto") idContacto: Int,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>


    // Correos
    @GET("emails")
    fun getListaEmails(): Call<List<Email>>

    @POST("emails")
    fun addCorreo(@Body email: Email): Call<Email>

}
