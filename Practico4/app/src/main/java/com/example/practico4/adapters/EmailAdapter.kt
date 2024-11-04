package com.example.practico4.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practico4.R
import com.example.practico4.models.Email

class EmailAdapter(private var emails: MutableList<Email>) : RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    fun setEmails(newEmails: List<Email>) {
        emails.clear()  // Asegúrate de que emails sea de tipo MutableList para que clear() esté disponible
        emails.addAll(newEmails)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_email, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(emails[position])
    }

    override fun getItemCount(): Int = emails.size

    inner class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        private val emailLabelTextView: TextView = itemView.findViewById(R.id.emailLabelTextView)

        fun bind(email: Email) {
            emailTextView.text = email.email
            emailLabelTextView.text = email.label
        }
    }
}
