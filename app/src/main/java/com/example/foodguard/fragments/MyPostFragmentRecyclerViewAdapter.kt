//package com.example.foodguard.fragments
//
//import androidx.recyclerview.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.TextView
//
//import com.example.foodguard.placeholder.PlaceholderContent.PlaceholderItem
//import com.example.foodguard.databinding.FragmentPostDetailsBinding
//
///**
// * [RecyclerView.Adapter] that can display a [PlaceholderItem].
// * TODO: Replace the implementation with code for your data type.
// */
//class MyPostFragmentRecyclerViewAdapter(
//        private val values: List<PlaceholderItem>)
//    : RecyclerView.Adapter<MyPostFragmentRecyclerViewAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//    return ViewHolder(FragmentPostDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = values[position]
//        holder.idView.text = item.id
//        holder.contentView.text = item.content
//    }
//
//    override fun getItemCount(): Int = values.size
//
//    inner class ViewHolder(binding: FragmentPostDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
//        val idView: TextView = binding.itemNumber
//        val contentView: TextView = binding.content
//
//        override fun toString(): String {
//            return super.toString() + " '" + contentView.text + "'"
//        }
//    }
//
//}