package com.switcherette.boarribs.all_sightings_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.databinding.ItemSightingBinding
import java.sql.Date
import java.text.SimpleDateFormat

class SightingsAdapter(
    private var getDetailView: (Sighting) -> Unit
) : ListAdapter<Sighting, SightingsAdapter.SightingsViewHolder>(SightingsDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SightingsViewHolder {
        val binding = ItemSightingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return SightingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SightingsViewHolder, position: Int) {
        holder.bind(getItem(position), getDetailView)
    }

    class SightingsViewHolder(
        private val binding: ItemSightingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sighting: Sighting, getDetailView: (Sighting) -> Unit) {
            Glide
                .with(itemView.context)
                .load(sighting.picture)
                .into(binding.ivItem)
            binding.tvItemHeading.text = sighting.heading
            binding.tvItemDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm").format(
                Date(sighting.timestamp)
            )
            binding.root.setOnClickListener {
                getDetailView(sighting)
            }
        }
    }

    class SightingsDiffUtil : DiffUtil.ItemCallback<Sighting>() {
        override fun areItemsTheSame(oldItem: Sighting, newItem: Sighting): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Sighting, newItem: Sighting): Boolean {
            return oldItem.id == newItem.id
        }
    }
}