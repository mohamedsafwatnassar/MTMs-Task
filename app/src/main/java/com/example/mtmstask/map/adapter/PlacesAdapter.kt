package com.example.mtmstask.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmstask.database.model.SourceModel
import com.example.mtmstask.databinding.ItemPlacesBinding

class PlacesAdapter(
    private val sourceModelList: List<SourceModel>,
    private val onItemClickCallBack: (position: Int, sourceModel: SourceModel) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>() {

    private lateinit var binding: ItemPlacesBinding

    class PlacesViewHolder(itemView: ItemPlacesBinding) : RecyclerView.ViewHolder(itemView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        binding = ItemPlacesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val locationModel = sourceModelList[position]

        binding.txtPlaceName.text = locationModel.name

        holder.itemView.setOnClickListener {
            onItemClickCallBack.invoke(position, locationModel)
        }
    }

    override fun getItemCount(): Int {
        return sourceModelList.size
    }
}