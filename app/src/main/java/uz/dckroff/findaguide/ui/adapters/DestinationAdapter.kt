package uz.dckroff.findaguide.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ItemDestinationBinding
import uz.dckroff.findaguide.model.Destination

class DestinationAdapter(private val onDestinationClick: (Destination) -> Unit) : 
    ListAdapter<Destination, DestinationAdapter.DestinationViewHolder>(DestinationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val binding = ItemDestinationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DestinationViewHolder(private val binding: ItemDestinationBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDestinationClick(getItem(position))
                }
            }
        }
        
        fun bind(destination: Destination) {
            binding.tvDestinationName.text = destination.name
            binding.tvDestinationCountry.text = destination.description
            binding.tvGuideCount.text = binding.root.context.getString(
                R.string.available_guides_count, 
                destination.guidesCount
            )
            
            // Загружаем изображение направления с использованием Glide
            Glide.with(binding.root)
                .load(destination.photo)
                .placeholder(R.drawable.placeholder_destination)
                .error(R.drawable.placeholder_destination)
                .centerCrop()
                .into(binding.ivDestination)
        }
    }

    class DestinationDiffCallback : DiffUtil.ItemCallback<Destination>() {
        override fun areItemsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem == newItem
        }
    }
} 