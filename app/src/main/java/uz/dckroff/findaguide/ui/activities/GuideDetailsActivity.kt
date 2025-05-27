package uz.dckroff.findaguide.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityGuideDetailsBinding

class GuideDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuideDetailsBinding
    private var guideId: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get guide ID from intent
        guideId = intent.getStringExtra("guideId") ?: ""
        
        setupToolbar()
        loadGuideDetails()
        setupButtons()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun loadGuideDetails() {
        // In a real app, this would load from repository
        // For now, just show placeholder data
        
        binding.tvGuideName.text = "John Smith"
        binding.tvLocation.text = "New York, USA"
        binding.tvRating.text = "4.8"
        binding.ratingBar.rating = 4.8f
        binding.tvReviewCount.text = "(124)"
        binding.tvPrice.text = "$50/hour"
        binding.tvDescription.text = "Professional tour guide with over 10 years of experience. Specializing in historical tours and local cuisine experiences. Fluent in English, Spanish, and French."
        
        // Set languages
        binding.chipEnglish.isChecked = true
        binding.chipSpanish.isChecked = true
        binding.chipFrench.isChecked = true
        
        // Set specializations
        binding.chipHistory.isChecked = true
        binding.chipFood.isChecked = true
    }
    
    private fun setupButtons() {
        // Setup book now button
        binding.btnBookNow.setOnClickListener {
            navigateToBooking()
        }
        
        // Setup chat button
        binding.btnChat.setOnClickListener {
            navigateToChat()
        }
    }
    
    private fun navigateToBooking() {
        val intent = Intent(this, BookingActivity::class.java).apply {
            putExtra("guideId", guideId)
        }
        startActivity(intent)
    }
    
    private fun navigateToChat() {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("guideId", guideId)
        }
        startActivity(intent)
    }
} 