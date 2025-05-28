package uz.dckroff.findaguide.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ItemBookingBinding
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.model.BookingStatus

class BookingAdapter(
    private val onBookingClick: (Booking) -> Unit,
    private val onCancelClick: (Booking) -> Unit
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookingClick(getItem(position))
                }
            }

            binding.btnCancel.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCancelClick(getItem(position))
                }
            }
        }

        fun bind(booking: Booking) {
            binding.tvBookingDate.text = booking.date
            binding.tvBookingTime.text = booking.time
            binding.tvBookingDuration.text = "${booking.numberOfPeople} person(s)"
            binding.tvBookingPrice.text = "$${booking.price}"

            // Устанавливаем статус бронирования и его цвет
            val statusText: String
            val statusColor: Int

            when (booking.status) {
                BookingStatus.PENDING -> {
                    statusText = binding.root.context.getString(R.string.status_pending)
                    statusColor = R.color.status_pending
                    binding.btnCancel.isEnabled = true
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.ratingContainer.visibility = View.GONE
                }

                BookingStatus.CONFIRMED -> {
                    statusText = binding.root.context.getString(R.string.status_confirmed)
                    statusColor = R.color.status_confirmed
                    binding.btnCancel.isEnabled = true
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.ratingContainer.visibility = View.GONE
                }

                BookingStatus.COMPLETED -> {
                    statusText = binding.root.context.getString(R.string.status_completed)
                    statusColor = R.color.status_completed
                    binding.btnCancel.visibility = View.GONE
                    
                    // Показываем рейтинг, если пользователь оставил отзыв
                    if (booking.userRating > 0) {
                        binding.ratingContainer.visibility = View.VISIBLE
                        binding.ratingBar.rating = booking.userRating
                    } else {
                        binding.ratingContainer.visibility = View.GONE
                    }
                }

                BookingStatus.CANCELLED -> {
                    statusText = binding.root.context.getString(R.string.status_cancelled)
                    statusColor = R.color.status_cancelled
                    binding.btnCancel.visibility = View.GONE
                    binding.ratingContainer.visibility = View.GONE
                }
            }

            binding.chipStatus.text = statusText
            binding.chipStatus.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(binding.root.context, statusColor)
            )
        }
    }

    class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem == newItem
        }
    }
} 