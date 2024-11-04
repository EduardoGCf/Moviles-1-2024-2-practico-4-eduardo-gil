package com.example.practico4.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.practico4.R
import com.example.practico4.adapters.EmailEditAdapter
import com.example.practico4.adapters.PhoneEditAdapter
import com.example.practico4.databinding.ActivityEditContactBinding
import com.example.practico4.models.Contacto
import com.example.practico4.models.Email
import com.example.practico4.models.Telefono
import com.example.practico4.network.ApiClient
import com.example.practico4.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditContactBinding
    private val phones = mutableListOf<Telefono>()
    private val emails = mutableListOf<Email>()
    private var selectedImageUri: Uri? = null
    private var contactoId: Int = 0

    private lateinit var phoneAdapter: PhoneEditAdapter
    private lateinit var emailAdapter: EmailEditAdapter
    private val apiService by lazy { ApiClient.getClient().create(ApiService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactoId = intent.getIntExtra("contactoId", -1)
        setupRecyclerViews()

        binding.btnSave.setOnClickListener {
            saveContactChanges()
        }
        binding.btnSelectImage.setOnClickListener {
            openGallery()
        }
        binding.btnAddPhone.setOnClickListener {
            showPhoneField()
        }
        binding.btnAddEmail.setOnClickListener {
            showEmailField()
        }

        loadContactData(contactoId)
    }

    private fun setupRecyclerViews() {
        phoneAdapter = PhoneEditAdapter(phones)
        emailAdapter = EmailEditAdapter(emails)
        binding.phonesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditContactActivity)
            adapter = phoneAdapter
        }
        binding.emailsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditContactActivity)
            adapter = emailAdapter
        }
    }




    //BASURA ME ESTRESE ESTO ERA PARA LA IMAGEN
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun uploadImage(contactoId: Int, imageUri: Uri) {
    }
    private fun getRealPathFromUri(uri: Uri): String? {
        return null
    }







    private fun loadContactData(contactoId: Int) {
        apiService.getContactoById(contactoId).enqueue(object : Callback<Contacto> {
            override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                response.body()?.let { contacto ->
                    binding.etName.setText(contacto.name)
                    binding.etLastName.setText(contacto.last_name)
                    binding.etCompany.setText(contacto.company)
                    binding.etAddress.setText(contacto.address)
                    binding.etCity.setText(contacto.city)
                    binding.etState.setText(contacto.state)
                    phones.clear()
                    phones.addAll(contacto.phones)
                    emails.clear()
                    emails.addAll(contacto.emails)
                    phoneAdapter.notifyDataSetChanged()
                    emailAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Contacto>, t: Throwable) {
                Toast.makeText(this@EditContactActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun saveContactChanges() {
        val updatedContact = Contacto(
            id = contactoId,
            name = binding.etName.text.toString(),
            last_name = binding.etLastName.text.toString(),
            company = binding.etCompany.text.toString(),
            address = binding.etAddress.text.toString(),
            city = binding.etCity.text.toString(),
            state = binding.etState.text.toString(),
            profile_picture = selectedImageUri?.toString(),
            phones = phones,
            emails = emails
        )

        apiService.updateContacto(contactoId, updatedContact).enqueue(object : Callback<Contacto> {
            override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditContactActivity, "Contacto actualizado", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent().apply {
                        putExtra("contactoId", contactoId)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()

                } else {
                    Toast.makeText(this@EditContactActivity, "Error al actualizar contacto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Contacto>, t: Throwable) {
                Toast.makeText(this@EditContactActivity, "Error de red al actualizar", Toast.LENGTH_SHORT).show()
            }
        })
    }

//TELEFONOS

    private fun showPhoneField() {
        val phoneField = EditText(this).apply {
            hint = "Teléfono"
            inputType = InputType.TYPE_CLASS_PHONE
        }
        val labelSpinner = createLabelSpinner("phone")
        val saveButton = Button(this).apply {
            text = "Guardar Teléfono"
            setOnClickListener {
                val label = if (labelSpinner.selectedItem == "Personalizada") {
                    (labelSpinner.tag as? EditText)?.text.toString()
                } else {
                    labelSpinner.selectedItem.toString()
                }
                val newPhone = Telefono(0, phoneField.text.toString(), contactoId, label)
                addPhoneToApi(newPhone)
                binding.phoneContainer.removeAllViews()
            }
        }
        binding.phoneContainer.addView(phoneField)
        binding.phoneContainer.addView(labelSpinner)
        binding.phoneContainer.addView(saveButton)
    }

    private fun addPhoneToApi(phone: Telefono) {
        apiService.addTelefono(phone).enqueue(object : Callback<Telefono> {
            override fun onResponse(call: Call<Telefono>, response: Response<Telefono>) {
                if (response.isSuccessful) {
                    phones.add(response.body()!!)
                    phoneAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@EditContactActivity, "Error al agregar teléfono", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Telefono>, t: Throwable) {
                Toast.makeText(this@EditContactActivity, "Error de red al agregar teléfono", Toast.LENGTH_SHORT).show()
            }
        })
    }






// EMAILS


    private fun showEmailField() {
        val emailField = EditText(this).apply {
            hint = "Correo"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val labelSpinner = createLabelSpinner("email")
        val saveButton = Button(this).apply {
            text = "Guardar Correo"
            setOnClickListener {
                val label = if (labelSpinner.selectedItem == "Personalizada") {
                    (labelSpinner.tag as? EditText)?.text.toString()
                } else {
                    labelSpinner.selectedItem.toString()
                }
                val newEmail = Email(0, emailField.text.toString(), contactoId, label)
                addEmailToApi(newEmail)
                binding.emailContainer.removeAllViews()
            }
        }
        binding.emailContainer.addView(emailField)
        binding.emailContainer.addView(labelSpinner)
        binding.emailContainer.addView(saveButton)
    }



    private fun addEmailToApi(email: Email) {
        apiService.addCorreo(email).enqueue(object : Callback<Email> {
            override fun onResponse(call: Call<Email>, response: Response<Email>) {
                if (response.isSuccessful) {
                    emails.add(response.body()!!)
                    emailAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@EditContactActivity, "Error al agregar correo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Email>, t: Throwable) {
                Toast.makeText(this@EditContactActivity, "Error de red al agregar correo", Toast.LENGTH_SHORT).show()
            }
        })
    }





//CREAR APARTADO DE LABELS CON LO QUE CREO UN NUEVO CELULAR Y CORREO

    private fun createLabelSpinner(type: String): Spinner {
        val spinner = Spinner(this)
        val labels = if (type == "phone") listOf("Casa", "Trabajo", "Universidad", "Personalizada") else listOf("Personal", "Trabajo", "Universidad", "Personalizada")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        val customLabelField = EditText(this).apply {
            hint = "Etiqueta personalizada"
            visibility = View.GONE
        }

        if (type == "phone") {
            binding.phoneContainer.addView(customLabelField)
        } else {
            binding.emailContainer.addView(customLabelField)
        }

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                customLabelField.visibility = if (labels[position] == "Personalizada") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

        spinner.tag = customLabelField
        return spinner
    }



    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
