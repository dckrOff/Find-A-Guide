package uz.dckroff.findaguide.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityBookingBinding
import uz.dckroff.findaguide.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private var guideId: String = ""
    
    private val viewModel: BookingViewModel by viewModels()
    
    // Выбранные дата и время
    private var selectedDate: Date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time
    private var selectedTime: String = "10:00 AM"
    private var selectedDuration: Int = 2
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get guide ID from intent
        guideId = intent.getStringExtra("guideId") ?: ""
        
        setupToolbar()
        setupDateTimePickers()
        setupBookingButton()
        observeViewModel()
        
        // Загружаем данные о гиде
        viewModel.loadGuideDetails(guideId)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupDateTimePickers() {
        // Setup date picker
        binding.btnDate.setOnClickListener {
            // Show date picker dialog
            showDatePickerDialog()
        }
        
        // Setup time picker
        binding.btnTime.setOnClickListener {
            // Show time picker dialog
            showTimePickerDialog()
        }
    }
    
    private fun setupBookingButton() {
        binding.btnBook.setOnClickListener {
            // Validate inputs
            if (validateInputs()) {
                // Create booking
                createBooking()
            }
        }
    }
    
    private fun observeViewModel() {
        // Наблюдаем за данными гида
        viewModel.guide.observe(this) { guide ->
            binding.tvGuideName.text = guide.name
            binding.tvLocation.text = guide.location
            binding.tvPrice.text = "$${guide.price}/hour"
            
            // Загружаем фото гида
            Glide.with(this)
                .load(guide.photo)
                .placeholder(R.drawable.placeholder_guide)
                .error(R.drawable.placeholder_guide)
                .into(binding.ivGuidePhoto)
            
            // Обновляем общую стоимость
            updateTotalPrice()
        }
        
        // Наблюдаем за статусом создания бронирования
        viewModel.bookingCreated.observe(this) { created ->
            if (created) {
                Toast.makeText(this, getString(R.string.booking_created_successfully), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        
        // Наблюдаем за статусом загрузки
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
        
        // Наблюдаем за ошибками
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showDatePickerDialog() {
        // Placeholder for date picker dialog
        // This would be implemented in later stages
    }
    
    private fun showTimePickerDialog() {
        // Placeholder for time picker dialog
        // This would be implemented in later stages
    }
    
    private fun validateInputs(): Boolean {
        // Validate that date and time are selected
        if (binding.btnDate.text.toString() == getString(R.string.select_date)) {
            Toast.makeText(this, getString(R.string.please_select_date), Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (binding.btnTime.text.toString() == getString(R.string.select_time)) {
            Toast.makeText(this, getString(R.string.please_select_time), Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun createBooking() {
        val numberOfPeople = binding.etNumberOfPeople.text.toString().toIntOrNull() ?: 1
        val notes = binding.etNotes.text.toString()
        
        viewModel.createBooking(
            guideId = guideId,
            date = binding.btnDate.text.toString(),
            time = binding.btnTime.text.toString(),
            numberOfPeople = numberOfPeople,
            notes = notes
        )
    }
    
    private fun updateTotalPrice() {
        val numberOfPeople = binding.etNumberOfPeople.text.toString().toIntOrNull() ?: 1
        val price = viewModel.guide.value?.price ?: 0.0
        
        val totalPrice = numberOfPeople * price
        binding.tvTotalPrice.text = "$${String.format("%.2f", totalPrice)}"
    }
} 