package uz.dckroff.findaguide.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityBookingDetailsBinding
import uz.dckroff.findaguide.model.BookingStatus
import uz.dckroff.findaguide.ui.activities.ChatActivity
import uz.dckroff.findaguide.viewmodel.BookingDetailsViewModel

class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailsBinding
    private var bookingId: String = ""
    
    private val viewModel: BookingDetailsViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Получаем ID бронирования из Intent
        bookingId = intent.getStringExtra("bookingId") ?: ""
        if (bookingId.isEmpty()) {
            Toast.makeText(this, "Booking ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupToolbar()
        setupButtons()
        observeViewModel()
        
        // Загружаем детали бронирования
        viewModel.loadBookingDetails(bookingId)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupButtons() {
        // Кнопка для отмены бронирования
        binding.btnCancel.setOnClickListener {
            showCancelBookingDialog()
        }
        
        // Кнопка для связи с гидом
        binding.btnContactGuide.setOnClickListener {
            val guideId = viewModel.booking.value?.guideId
            if (!guideId.isNullOrEmpty()) {
                navigateToChat(guideId)
            }
        }
    }
    
    private fun observeViewModel() {
        // Наблюдаем за данными бронирования
        viewModel.booking.observe(this) { booking ->
            binding.tvBookingDate.text = booking.date
            binding.tvBookingTime.text = booking.time
            binding.tvBookingPeople.text = getString(R.string.booking_duration, booking.numberOfPeople)
            binding.tvBookingPrice.text = getString(R.string.price_value, booking.price)
            
            // Загружаем фото гида
            Glide.with(this)
                .load(booking.guidePhoto)
                .placeholder(R.drawable.placeholder_guide)
                .error(R.drawable.placeholder_guide)
                .into(binding.ivGuidePhoto)
            
            // Устанавливаем имя гида
            binding.tvGuideName.text = booking.guideName
            
            // Устанавливаем статус бронирования
            val statusText: String
            val statusColor: Int
            
            when (booking.status) {
                BookingStatus.PENDING -> {
                    statusText = getString(R.string.status_pending)
                    statusColor = R.color.status_pending
                    binding.btnCancel.visibility = View.VISIBLE
                }
                BookingStatus.CONFIRMED -> {
                    statusText = getString(R.string.status_confirmed)
                    statusColor = R.color.status_confirmed
                    binding.btnCancel.visibility = View.VISIBLE
                }
                BookingStatus.COMPLETED -> {
                    statusText = getString(R.string.status_completed)
                    statusColor = R.color.status_completed
                    binding.btnCancel.visibility = View.GONE
                }
                BookingStatus.CANCELLED -> {
                    statusText = getString(R.string.status_cancelled)
                    statusColor = R.color.status_cancelled
                    binding.btnCancel.visibility = View.GONE
                }
            }
            
            binding.tvBookingStatus.text = statusText
            binding.tvBookingStatus.setTextColor(getColor(statusColor))
            
            // Показываем примечания, если они есть
            if (!booking.notes.isNullOrEmpty()) {
                binding.tvNotes.text = booking.notes
                binding.notesContainer.isVisible = true
            } else {
                binding.notesContainer.isVisible = false
            }
        }
        
        // Наблюдаем за статусом загрузки
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.contentLayout.isVisible = !isLoading
        }
        
        // Наблюдаем за ошибками
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        
        // Наблюдаем за успешными действиями
        viewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                
                // Если бронирование отменено, закрываем экран через некоторое время
                if (message == getString(R.string.booking_cancelled)) {
                    binding.root.postDelayed({
                        finish()
                    }, 1500) // Задержка 1.5 секунды перед закрытием
                }
            }
        }
    }
    
    private fun showCancelBookingDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.cancel)
            .setMessage(R.string.confirm_cancel_booking)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.cancelBooking(bookingId)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun navigateToChat(guideId: String) {
        val intent = android.content.Intent(this, ChatActivity::class.java).apply {
            putExtra("guideId", guideId)
        }
        startActivity(intent)
    }
} 