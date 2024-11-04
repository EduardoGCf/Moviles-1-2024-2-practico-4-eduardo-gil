package com.example.practico4.ui.activities

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practico4.R
import com.example.practico4.network.ApiClient
import com.example.practico4.network.ApiService
import com.example.practico4.models.Email
import com.example.practico4.models.Telefono
import com.example.practico4.databinding.ActivityAddContactoBinding
import com.example.practico4.models.Contacto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream

class AddContactoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactoBinding
    private lateinit var apiService: ApiService
    private val telefonosViews = mutableListOf<Triple<EditText, Spinner, EditText>>()  // Almacena el campo de teléfono, spinner de etiqueta y el campo personalizado
    private val correosViews = mutableListOf<Triple<EditText, Spinner, EditText>>()    // Almacena el campo de correo, spinner de etiqueta y el campo personalizado

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)

        binding.btnSave.setOnClickListener {
            saveContacto()
        }

        binding.btnSelectImage.setOnClickListener {
            openGallery()
        }

        // Configuración de botones para añadir teléfonos y correos dinámicamente
        binding.btnAddPhone.setOnClickListener {
            agregarCampoDinamico("phone")
        }

        binding.btnAddEmail.setOnClickListener {
            agregarCampoDinamico("email")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.contactImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveContacto() {
        val newContacto = Contacto(
            id = 0,
            name = binding.etName.text.toString(),
            last_name = binding.etLastName.text.toString(),
            company = binding.etCompany.text.toString(),
            address = binding.etAddress.text.toString(),
            city = binding.etCity.text.toString(),
            state = binding.etState.text.toString(),
            profile_picture = selectedImageUri?.toString(),
            phones = emptyList(),
            emails = emptyList()
        )

        apiService.addContacto(newContacto).enqueue(object : Callback<Contacto> {
            override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                if (response.isSuccessful) {
                    response.body()?.let { contacto ->
                        val personaId = contacto.id
                        agregarTelefonosYCorreos(personaId)
                        selectedImageUri?.let {
                            uploadProfilePicture(personaId, it)
                        }
                    }
                } else {
                    showError("Error al agregar contacto")
                }
            }

            override fun onFailure(call: Call<Contacto>, t: Throwable) {
                showError("Error de red: ${t.message}")
            }
        })
    }







    private fun agregarCampoDinamico(tipo: String) {
        val container = if (tipo == "phone") binding.phoneContainer else binding.emailContainer
        val editText = EditText(this)
        editText.hint = if (tipo == "phone") "Teléfono" else "Correo"
        editText.inputType = if (tipo == "phone") InputType.TYPE_CLASS_PHONE else InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        container.addView(editText)

        // Spinner para etiquetas
        val spinner = Spinner(this)
        val etiquetas = if (tipo == "phone") listOf("Casa", "Trabajo", "Universidad", "Personalizada") else listOf("Personal", "Trabajo", "Universidad", "Personalizada")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        container.addView(spinner)

        // Campo de texto para etiqueta personalizada (oculto inicialmente)
        val etiquetaPersonalizada = EditText(this)
        etiquetaPersonalizada.hint = "Etiqueta personalizada"
        etiquetaPersonalizada.visibility = View.GONE
        container.addView(etiquetaPersonalizada)

        // Mostrar campo personalizado solo si se selecciona "Personalizada"
        spinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View, position: Int, id: Long) {
                etiquetaPersonalizada.visibility = if (etiquetas[position] == "Personalizada") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        // Guardar en listas según tipo
        if (tipo == "phone") {
            telefonosViews.add(Triple(editText, spinner, etiquetaPersonalizada))
        } else {
            correosViews.add(Triple(editText, spinner, etiquetaPersonalizada))
        }
    }

    private fun agregarTelefonosYCorreos(personaId: Int) {
        // Crear y subir los teléfonos
        telefonosViews.forEach { (phoneField, spinner, etiquetaPersonalizada) ->
            val etiquetaSeleccionada = spinner.selectedItem.toString()

            // Determinar la etiqueta final: si es "Personalizada", tomar el valor del EditText adicional
            val etiquetaFinal = if (etiquetaSeleccionada == "Personalizada") {
                etiquetaPersonalizada.text.toString().takeIf { it.isNotBlank() } ?: "Personalizada"
            } else {
                etiquetaSeleccionada
            }

            if (phoneField.text.isNotBlank()) {
                val telefonoConId = Telefono(
                    id = 0,
                    number = phoneField.text.toString(),
                    persona_id = personaId,
                    label = etiquetaFinal
                )

                apiService.addTelefono(telefonoConId).enqueue(object : Callback<Telefono> {
                    override fun onResponse(call: Call<Telefono>, response: Response<Telefono>) {
                        if (!response.isSuccessful) {
                            showError("Error al agregar teléfono")
                        }
                    }

                    override fun onFailure(call: Call<Telefono>, t: Throwable) {
                        showError("Error de red al agregar teléfono: ${t.message}")
                    }
                })
            } else {
                showError("El campo del número de teléfono está vacío.")
            }
        }

        // Crear y subir los correos
        correosViews.forEach { (emailField, spinner, etiquetaPersonalizada) ->
            val etiquetaSeleccionada = spinner.selectedItem.toString()

            // Determinar la etiqueta final: si es "Personalizada", tomar el valor del EditText adicional
            val etiquetaFinal = if (etiquetaSeleccionada == "Personalizada") {
                etiquetaPersonalizada.text.toString().takeIf { it.isNotBlank() } ?: "Personalizada"
            } else {
                etiquetaSeleccionada
            }

            if (emailField.text.isNotBlank()) {
                val emailConId = Email(
                    id = 0,
                    email = emailField.text.toString(),
                    persona_id = personaId,
                    label = etiquetaFinal
                )

                apiService.addCorreo(emailConId).enqueue(object : Callback<Email> {
                    override fun onResponse(call: Call<Email>, response: Response<Email>) {
                        if (!response.isSuccessful) {
                            showError("Error al agregar correo")
                        }
                    }

                    override fun onFailure(call: Call<Email>, t: Throwable) {
                        showError("Error de red al agregar correo: ${t.message}")
                    }
                })
            } else {
                showError("El campo del correo está vacío.")
            }
        }

        Toast.makeText(this, "Contacto y detalles agregados", Toast.LENGTH_SHORT).show()
        finish()
    }






    //OTRA BURRADA QUE SE INTENTO HACER PARA METER LA IMAGEN PERO YA NO DOY MAS
    private fun uploadProfilePicture(personaId: Int, imageUri: Uri) {
//         val contentResolver: ContentResolver = contentResolver
//
//        try {
//            // Obtén el InputStream desde el URI
//            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
//            if (inputStream != null) {
//                // Crea un archivo temporal en el almacenamiento caché
//                val tempFile = File.createTempFile("profile_picture_${personaId}", ".jpg", cacheDir)
//
//                // Copia el InputStream al archivo temporal
//                tempFile.outputStream().use { outputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//
//                // Prepara el archivo para el multipart request
//                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
//                val imageBody = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
//
//                // Enviar la solicitud de subida de imagen a la API
//                apiService.uploadProfilePicture(personaId, imageBody).enqueue(object : Callback<ResponseBody> {
//                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                        if (response.isSuccessful) {
//                            Toast.makeText(this@AddContactoActivity, "Imagen de perfil subida correctamente", Toast.LENGTH_SHORT).show()
//                        } else {
//                            showError("Error al subir imagen de perfil")
//                        }
//                    }

//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        showError("Error de red al subir imagen: ${t.message}")
//                    }
//                })

                // Elimina el archivo temporal después de subirlo
//                tempFile.delete()
//            } else {
//                showError("No se pudo acceder a la imagen seleccionada")
//            }
//        } catch (e: Exception) {
//            showError("Error al procesar la imagen: ${e.message}")
//        }


    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
