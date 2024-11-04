package com.example.practico4.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.practico4.R
import com.example.practico4.adapters.EmailAdapter
import com.example.practico4.adapters.PhoneAdapter
import com.example.practico4.databinding.ActivityContactDetailsBinding
import com.example.practico4.models.Contacto
import com.example.practico4.viewmodels.ContactoViewModel

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactDetailsBinding
    private val contactoViewModel: ContactoViewModel by viewModels()
    private lateinit var phoneAdapter: PhoneAdapter
    private lateinit var emailAdapter: EmailAdapter

    companion object {
        private const val EDIT_CONTACT_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_CONTACT_REQUEST_CODE && resultCode == RESULT_OK) {
            val updatedContactoId = data?.getIntExtra("contactoId", -1)
            if (updatedContactoId != null && updatedContactoId != -1) {
                // Llamar al método para actualizar la vista con el nuevo contacto
                contactoViewModel.fetchContactoById(updatedContactoId)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el contactoId de los extras del Intent
        val contactoId = intent.getIntExtra("contactoId", -1)
        if (contactoId == -1) {
            finish() // Finalizar si no se encuentra el contactoId
            return
        }

        // Configurar los RecyclerViews para teléfonos y correos electrónicos
        setupRecyclerViews()

        // Configurar observadores
        contactoViewModel.contacto.observe(this) { contacto ->
            contacto?.let { loadContactDetail(it) }
        }

        // Llamar al ViewModel para obtener el contacto específico
        contactoViewModel.fetchContactoById(contactoId)

        binding.editContactButton.setOnClickListener {
            val intent = Intent(this, EditContactActivity::class.java)
            intent.putExtra("contactoId", contactoId)
            startActivityForResult(intent, EDIT_CONTACT_REQUEST_CODE)

        }

    }

    private fun setupRecyclerViews() {
        phoneAdapter = PhoneAdapter(mutableListOf())
        emailAdapter = EmailAdapter(mutableListOf())

        binding.phonesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.phonesRecyclerView.adapter = phoneAdapter

        binding.emailsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.emailsRecyclerView.adapter = emailAdapter
    }


    //ACA ACTUALIZO LA INFO GENERAL DEL CONTACTO  (METO LA INFO DEL CONTACTO EN LOS TEXTVIEW)
    private fun loadContactDetail(contacto: Contacto) {

        binding.nameTextView.text = "${contacto.name} ${contacto.last_name}"
        binding.companyTextView.text = contacto.company
        binding.addressTextView.text = contacto.address
        binding.cityTextView.text = contacto.city
        binding.stateTextView.text = contacto.state

        // Cargar imagen de perfil
        Glide.with(this)
            .load(contacto.profile_picture)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.profileImageView)

        // Actualizar las listas de teléfonos y correos electrónicos
        phoneAdapter.setPhones(contacto.phones)
        emailAdapter.setEmails(contacto.emails)
    }
}
