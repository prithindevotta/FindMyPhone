package com.example.findmyphone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmptyAdapter: RecyclerView.Adapter<EmptyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyViewHolder {
        return EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_item, parent, false))
    }

    override fun onBindViewHolder(holder: EmptyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 1;
    }

}
class EmptyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val mainField: TextView = itemView.findViewById<TextView>(R.id.empty)
}