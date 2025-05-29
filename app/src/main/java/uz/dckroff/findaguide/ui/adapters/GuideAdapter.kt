package uz.dckroff.findaguide.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ItemGuideBinding
import uz.dckroff.findaguide.databinding.ItemGuideSearchBinding
import uz.dckroff.findaguide.model.Guide

class GuideAdapter(
    private val onGuideClick: (Guide) -> Unit,
    private val viewType: Int = VIEW_TYPE_SEARCH
) : ListAdapter<Guide, RecyclerView.ViewHolder>(GuideDiffCallback()) {

    companion object {
        const val VIEW_TYPE_SEARCH = 1
        const val VIEW_TYPE_FEATURED = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FEATURED -> {
                val binding = ItemGuideBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FeaturedGuideViewHolder(binding)
            }
            else -> {
                val binding = ItemGuideSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchGuideViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val guide = getItem(position)
        when (holder) {
            is SearchGuideViewHolder -> holder.bind(guide)
            is FeaturedGuideViewHolder -> holder.bind(guide)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    inner class SearchGuideViewHolder(private val binding: ItemGuideSearchBinding) :
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
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
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

    inner class FeaturedGuideViewHolder(private val binding: ItemGuideBinding) :
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
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
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