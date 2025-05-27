package uz.dckroff.findaguide.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityBookingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private var guideId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get guide ID from intent
        guideId = intent.getStringExtra(EXTRA_GUIDE_ID)
        
        setupToolbar()
        loadGuideInfo()
        setupDatePicker()
        setupTimePicker()
        setupBookButton()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.booking_details)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun loadGuideInfo() {
        // In a real app, this would load from repository
        // For now, just show placeholder data
        binding.tvGuideName.text = "John Smith"
        binding.tvLocation.text = "New York, USA"
        binding.tvPrice.text = "$50/hour"
    }
    
    private fun setupDatePicker() {
        // Set default date to tomorrow
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.btnDate.text = dateFormat.format(calendar.time)
        
        binding.btnDate.setOnClickListener {
            // In a real app, this would show a DatePickerDialog
            // For now, just show a toast
            Toast.makeText(this, "Date picker would show here", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupTimePicker() {
        // Set default time
        binding.btnTime.text = "10:00 AM"
        
        binding.btnTime.setOnClickListener {
            // In a real app, this would show a TimePickerDialog
            // For now, just show a toast
            Toast.makeText(this, "Time picker would show here", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupBookButton() {
        binding.btnBook.setOnClickListener {
            // In a real app, this would create a booking
            // For now, just show a toast and finish
            Toast.makeText(this, "Booking created successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    companion object {
        const val EXTRA_GUIDE_ID = "extra_guide_id"
    }
} 