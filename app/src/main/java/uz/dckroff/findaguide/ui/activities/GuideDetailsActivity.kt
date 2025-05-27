package uz.dckroff.findaguide.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityGuideDetailsBinding
import uz.dckroff.findaguide.viewmodel.GuideDetailsViewModel

class GuideDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuideDetailsBinding
    private var guideId: String = ""
    
    private val viewModel: GuideDetailsViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get guide ID from intent
        guideId = intent.getStringExtra("guideId") ?: ""
        
        setupToolbar()
        setupButtons()
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
    
    private fun observeViewModel() {
        // Наблюдаем за данными гида
        viewModel.guide.observe(this) { guide ->
            binding.tvGuideName.text = guide.name
            binding.tvLocation.text = guide.location
            binding.tvRating.text = guide.rating.toString()
            binding.ratingBar.rating = guide.rating
            binding.tvReviewCount.text = "(${viewModel.reviews.value?.size ?: 0})"
            binding.tvPrice.text = "$${guide.price}/hour"
            binding.tvDescription.text = guide.description
            
            // Загружаем фото гида
            Glide.with(this)
                .load(guide.photo)
                .placeholder(R.drawable.placeholder_guide)
                .error(R.drawable.placeholder_guide)
                .into(binding.ivGuidePhoto)
            
            // Устанавливаем языки
            binding.chipEnglish.isChecked = guide.languages.contains("English")
            binding.chipSpanish.isChecked = guide.languages.contains("Spanish")
            binding.chipFrench.isChecked = guide.languages.contains("French")
            binding.chipGerman.isChecked = guide.languages.contains("German")
            
            // Устанавливаем специализации
            binding.chipHistory.isChecked = guide.specializations.contains("History")
            binding.chipFood.isChecked = guide.specializations.contains("Food")
            binding.chipNature.isChecked = guide.specializations.contains("Nature")
            binding.chipAdventure.isChecked = guide.specializations.contains("Adventure")
        }
        
        // Наблюдаем за отзывами
        viewModel.reviews.observe(this) { reviews ->
            // В реальном приложении здесь бы обновлялся адаптер с отзывами
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