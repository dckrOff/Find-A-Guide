package uz.dckroff.findaguide.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ItemGuideBinding
import uz.dckroff.findaguide.model.Guide

class GuideAdapter(private val onGuideClick: (Guide) -> Unit) : 
    ListAdapter<Guide, GuideAdapter.GuideViewHolder>(GuideDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ItemGuideBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GuideViewHolder(private val binding: ItemGuideBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onGuideClick(getItem(position))
                }
            }
        }
        
        fun bind(guide: Guide) {
            binding.tvGuideName.text = guide.name
            binding.tvLocation.text = guide.location
            binding.tvPrice.text = binding.root.context.getString(
                R.string.price_per_hour, 
                guide.price
            )
            binding.ratingBar.rating = guide.rating
            binding.tvRating.text = guide.rating.toString()
            
            // Загружаем фото гида с использованием Glide
            Glide.with(binding.root)
                .load(guide.photo)
                .placeholder(R.drawable.placeholder_guide)
                .error(R.drawable.placeholder_guide)
                .centerCrop()
                .into(binding.ivGuidePhoto)
            
            // Устанавливаем языки гида
            val languages = guide.languages.joinToString(", ")
            binding.tvLanguages.text = binding.root.context.getString(
                R.string.speaks, 
                languages
            )
        }
    }

    class GuideDiffCallback : DiffUtil.ItemCallback<Guide>() {
        override fun areItemsTheSame(oldItem: Guide, newItem: Guide): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Guide, newItem: Guide): Boolean {
            return oldItem == newItem
        }
    }
} 