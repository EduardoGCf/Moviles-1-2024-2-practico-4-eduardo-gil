package com.example.practico4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practico4.databinding.ItemPhoneEditBinding
import com.example.practico4.models.Telefono

class PhoneEditAdapter(private val phoneList: List<Telefono>) :
    RecyclerView.Adapter<PhoneEditAdapter.PhoneViewHolder>() {

    inner class PhoneViewHolder(private val binding: ItemPhoneEditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(phone: Telefono) {
            binding.phoneTextView.setText(phone.number)
            binding.labelTextView.text = phone.label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val binding = ItemPhoneEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        holder.bind(phoneList[position])
    }

    override fun getItemCount(): Int = phoneList.size
}
