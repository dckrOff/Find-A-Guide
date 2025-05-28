package uz.dckroff.findaguide.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    
    // Регистрируем ActivityResultLauncher для получения результата из ReviewActivity
    private val reviewActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Если отзыв успешно оставлен, обновляем детали бронирования
            viewModel.loadBookingDetails(bookingId)
            Toast.makeText(this, R.string.review_submitted_successfully, Toast.LENGTH_SHORT).show()
        }
    }
    
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
        
        // Кнопка для оставления отзыва
        binding.btnReviewGuide.setOnClickListener {
            val booking = viewModel.booking.value ?: return@setOnClickListener
            navigateToReview(booking.guideId, booking.guideName, booking.guidePhoto)
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
                    binding.btnReviewGuide.visibility = View.GONE
                    binding.ratingContainer.visibility = View.GONE
                }
                BookingStatus.CONFIRMED -> {
                    statusText = getString(R.string.status_confirmed)
                    statusColor = R.color.status_confirmed
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.btnReviewGuide.visibility = View.GONE
                    binding.ratingContainer.visibility = View.GONE
                }
                BookingStatus.COMPLETED -> {
                    statusText = getString(R.string.status_completed)
                    statusColor = R.color.status_completed
                    binding.btnCancel.visibility = View.GONE
                    
                    // Проверяем, оставлял ли пользователь уже отзыв
                    viewModel.checkIfReviewExists(bookingId)
                    
                    // Если пользователь уже оставил отзыв, показываем его рейтинг
                    if (booking.userRating > 0) {
                        binding.ratingContainer.visibility = View.VISIBLE
                        binding.rbUserRating.rating = booking.userRating
                    } else {
                        binding.ratingContainer.visibility = View.GONE
                    }
                }
                BookingStatus.CANCELLED -> {
                    statusText = getString(R.string.status_cancelled)
                    statusColor = R.color.status_cancelled
                    binding.btnCancel.visibility = View.GONE
                    binding.btnReviewGuide.visibility = View.GONE
                    binding.ratingContainer.visibility = View.GONE
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
        
        // Наблюдаем за статусом наличия отзыва
        viewModel.hasReview.observe(this) { hasReview ->
            val booking = viewModel.booking.value
            if (booking?.status == BookingStatus.COMPLETED) {
                binding.btnReviewGuide.isVisible = !hasReview
                
                // Если есть отзыв, показываем контейнер с рейтингом
                if (hasReview && booking.userRating > 0) {
                    binding.ratingContainer.visibility = View.VISIBLE
                    binding.rbUserRating.rating = booking.userRating
                } else {
                    binding.ratingContainer.visibility = View.GONE
                }
            } else {
                binding.btnReviewGuide.isVisible = false
                binding.ratingContainer.visibility = View.GONE
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
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("guideId", guideId)
        }
        startActivity(intent)
    }
    
    private fun navigateToReview(guideId: String, guideName: String, guidePhoto: String) {
        val intent = Intent(this, ReviewActivity::class.java).apply {
            putExtra("bookingId", bookingId)
            putExtra("guideId", guideId)
            putExtra("guideName", guideName)
            putExtra("guidePhoto", guidePhoto)
        }
        reviewActivityLauncher.launch(intent)
    }
} 