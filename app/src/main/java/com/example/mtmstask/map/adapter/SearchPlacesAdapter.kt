package com.example.mtmstask.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmstask.databinding.ItemPlacesBinding

class SearchPlacesAdapter(
    private val placesList: List<String>,
    private val onItemClickCallBack: (position: Int, places: String) -> Unit
) : RecyclerView.Adapter<SearchPlacesAdapter.PlacesViewHolder>() {

    private lateinit var binding: ItemPlacesBinding

    class PlacesViewHolder(itemView: ItemPlacesBinding) : RecyclerView.ViewHolder(itemView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        binding = ItemPlacesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = placesList[position]

        binding.txtPlaceName.text = place

        holder.itemView.setOnClickListener {
            onItemClickCallBack.invoke(position, place)
        }
    }

    override fun getItemCount(): Int {
        return placesList.size
    }
}