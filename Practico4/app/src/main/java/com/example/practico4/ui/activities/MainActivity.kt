package com.example.practico4.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practico4.databinding.ActivityMainBinding
import com.example.practico4.ui.adapters.ContactoAdapter
import com.example.practico4.viewmodels.ContactoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ContactoAdapter
    private val contactoViewModel: ContactoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        contactoViewModel.contactos.observe(this, Observer { contacts ->
            adapter.setContacts(contacts)
        })

        contactoViewModel.error.observe(this, Observer { errorMessage ->
            showError(errorMessage)
        })

        contactoViewModel.fetchContactos()

        binding.addContactButton.setOnClickListener {
            startActivity(Intent(this, AddContactoActivity::class.java))
        }


        setupSearchView()
    }



    private fun setupRecyclerView() {
        adapter = ContactoAdapter(
            contactos = mutableListOf(),
            onEditClick = { contacto ->
                val intent = Intent(this, EditContactActivity::class.java)
                intent.putExtra("contactoId", contacto.id)
                startActivity(intent)
            },
            onDeleteClick = { contacto ->
                deleteContact(contacto.id)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }



    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { contactoViewModel.searchContactos(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { contactoViewModel.searchContactos(it) }
                return false
            }
        })
    }



    private fun deleteContact(contactoId: Int) {
        contactoViewModel.deleteContacto(contactoId)
    }

    override fun onResume() {
        super.onResume()
        contactoViewModel.fetchContactos()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
