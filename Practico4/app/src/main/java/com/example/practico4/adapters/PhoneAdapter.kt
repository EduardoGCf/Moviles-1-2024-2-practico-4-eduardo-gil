package com.example.practico4.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practico4.R
import com.example.practico4.models.Telefono

class PhoneAdapter(private var phones: MutableList<Telefono>) : RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder>() {

    fun setPhones(newPhones: List<Telefono>) {
        phones.clear()
        phones.addAll(newPhones)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phone, parent, false)
        return PhoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        holder.bind(phones[position])
    }

    override fun getItemCount(): Int = phones.size

    inner class PhoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        private val phoneLabelTextView: TextView = itemView.findViewById(R.id.phoneLabelTextView)

        fun bind(phone: Telefono) {
            phoneNumberTextView.text = phone.number
            phoneLabelTextView.text = phone.label
        }
    }
}
