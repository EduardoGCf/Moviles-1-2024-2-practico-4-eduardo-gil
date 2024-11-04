package com.example.practico4.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practico4.R
import com.example.practico4.databinding.ContactItemBinding
import com.example.practico4.models.Contacto
import com.example.practico4.ui.activities.ContactDetailActivity

class ContactoAdapter(
    private val contactos: MutableList<Contacto>,
    private val onEditClick: ((Contacto) -> Unit)? = null,
    private val onDeleteClick: ((Contacto) -> Unit)? = null
) : RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>() {

    inner class ContactoViewHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contacto: Contacto) {
            binding.contactName.text = contacto.name
            binding.contactLastName.text = contacto.last_name
            binding.companyTextView.text = contacto.company
            // Configuración de la imagen del contacto
            Glide.with(itemView.context)
                .load(contacto.profile_picture) // URL de la imagen del contacto
                .placeholder(R.drawable.ic_placeholder) // Imagen de marcador de posición
                .error(R.drawable.ic_placeholder) // Imagen de error si no se puede cargar
                .into(binding.contactImage)

            // Listeners para abrir detalle del contacto
            binding.contactName.setOnClickListener {
                openContactDetail(it.context, contacto.id)
            }
            binding.contactLastName.setOnClickListener {
                openContactDetail(it.context, contacto.id)
            }
            binding.contactImage.setOnClickListener {
                openContactDetail(it.context, contacto.id)
            }
            binding.companyTextView.setOnClickListener {
                openContactDetail(it.context, contacto.id)
            }


            // Listeners de edición y eliminación si se proporcionan
            binding.editButton.setOnClickListener {
                onEditClick?.invoke(contacto)
            }
            binding.deleteButton.setOnClickListener {
                onDeleteClick?.invoke(contacto)
            }
        }

        private fun openContactDetail(context: Context, contactoId: Int) {
            val intent = Intent(context, ContactDetailActivity::class.java)
            intent.putExtra("contactoId", contactoId)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        holder.bind(contactos[position])
    }

    override fun getItemCount(): Int = contactos.size

    fun setContacts(contactList: List<Contacto>) {
        contactos.clear()
        contactos.addAll(contactList)
        notifyDataSetChanged()
    }
}
