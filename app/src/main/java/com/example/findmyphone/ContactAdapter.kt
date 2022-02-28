package com.example.findmyphone

import android.app.SearchManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.findmyphone.Data.UserContact
import java.util.zip.Inflater

class ContactAdapter(var listener: OnClick, var contacts: ArrayList<UserContact>):RecyclerView.Adapter<ContactHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val targetView = LayoutInflater.from(parent.context).inflate(R.layout.tracker_item, parent, false)
        val viewHolder =  ContactHolder(targetView)
        targetView.setOnClickListener {
            listener.onClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val curr = contacts[position]
        holder.name.text = curr.name
        holder.phoneNumber.text = curr.phoneNumber
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

}

class ContactHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val name: TextView = itemView.findViewById<TextView>(R.id.name)
    val phoneNumber: TextView = itemView.findViewById<TextView>(R.id.phoneNumber)
}