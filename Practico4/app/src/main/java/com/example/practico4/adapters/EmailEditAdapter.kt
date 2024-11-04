package com.example.practico4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practico4.databinding.ItemEmailEditBinding
import com.example.practico4.models.Email

class EmailEditAdapter(private val emailList: List<Email>) :
    RecyclerView.Adapter<EmailEditAdapter.EmailViewHolder>() {

    inner class EmailViewHolder(private val binding: ItemEmailEditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(email: Email) {
            binding.emailTextView.setText(email.email)
            binding.labelTextView.text = email.label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val binding = ItemEmailEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(emailList[position])
    }

    override fun getItemCount(): Int = emailList.size
}
