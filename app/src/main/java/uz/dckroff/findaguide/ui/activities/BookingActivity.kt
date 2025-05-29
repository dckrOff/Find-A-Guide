package uz.dckroff.findaguide.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    
    // Форматы даты и времени
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    // Выбранные дата и время
    private var selectedDate: Date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time
    private var selectedTime: String = "10:00"
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
        setupNumberOfPeopleInput()
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
        // Устанавливаем начальные значения
        binding.btnDate.text = dateFormat.format(selectedDate)
        binding.btnTime.text = selectedTime
        
        // Setup date picker
        binding.btnDate.setOnClickListener {
            showDatePickerDialog()
        }
        
        // Setup time picker
        binding.btnTime.setOnClickListener {
            showTimePickerDialog()
        }
    }
    
    private fun setupNumberOfPeopleInput() {
        // Обновляем общую стоимость при изменении количества людей
        binding.etNumberOfPeople.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                updateTotalPrice()
            }
        })
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
            binding.tvPrice.text = String.format("$%.2f/hour", guide.price)
            
            // Загружаем фото гида
            Glide.with(this)
                .load(guide.photo)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
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
        val calendar = Calendar.getInstance().apply {
            time = selectedDate
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val minDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)  // Минимальная дата - завтрашний день
        }
        
        val dialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = calendar.time
                binding.btnDate.text = dateFormat.format(selectedDate)
            },
            year,
            month,
            day
        )
        
        dialog.datePicker.minDate = minDate.timeInMillis
        dialog.show()
    }
    
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        val dialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                selectedTime = timeFormat.format(calendar.time)
                binding.btnTime.text = selectedTime
            },
            hour,
            minute,
            true  // 24-часовой формат
        )
        
        dialog.show()
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
        
        val numberOfPeople = binding.etNumberOfPeople.text.toString().toIntOrNull()
        if (numberOfPeople == null || numberOfPeople <= 0) {
            Toast.makeText(this, "Please enter a valid number of people", Toast.LENGTH_SHORT).show()
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
        binding.tvTotalPrice.text = String.format("$%.2f", totalPrice)
    }
} 