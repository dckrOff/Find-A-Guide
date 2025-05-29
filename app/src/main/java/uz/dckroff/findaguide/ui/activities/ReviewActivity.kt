package uz.dckroff.findaguide.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityReviewBinding
import uz.dckroff.findaguide.viewmodel.ReviewViewModel

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private val viewModel: ReviewViewModel by viewModels()
    
    private var bookingId: String = ""
    private var guideId: String = ""
    private var guideName: String = ""
    private var guidePhoto: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Получаем данные из Intent
        bookingId = intent.getStringExtra("bookingId") ?: ""
        guideId = intent.getStringExtra("guideId") ?: ""
        guideName = intent.getStringExtra("guideName") ?: ""
        guidePhoto = intent.getStringExtra("guidePhoto") ?: ""
        
        if (bookingId.isEmpty() || guideId.isEmpty()) {
            Toast.makeText(this, "Error: Missing booking or guide information", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupToolbar()
        setupUI()
        setupSubmitButton()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.rate_guide)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupUI() {
        // Устанавливаем данные гида
        binding.tvGuideName.text = guideName
        
        // Загружаем фото гида
        if (guidePhoto.isNotEmpty()) {
            Glide.with(this)
                .load(guidePhoto)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.ivGuidePhoto)
        }
        
        // Настраиваем рейтинг
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            updateSubmitButtonState()
            
            // Отображаем текстовое описание рейтинга
            val ratingText = when {
                rating <= 1 -> getString(R.string.rating_poor)
                rating <= 2 -> getString(R.string.rating_fair)
                rating <= 3 -> getString(R.string.rating_good)
                rating <= 4 -> getString(R.string.rating_very_good)
                else -> getString(R.string.rating_excellent)
            }
            
            binding.tvRatingDescription.text = ratingText
            binding.tvRatingDescription.isVisible = true
        }
    }
    
    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            val rating = binding.ratingBar.rating
            val comment = binding.etComment.text.toString().trim()
            
            if (rating > 0) {
                viewModel.submitReview(guideId, bookingId, rating, comment)
            } else {
                Toast.makeText(this, getString(R.string.please_select_rating), Toast.LENGTH_SHORT).show()
            }
        }
        
        // Начальное состояние кнопки
        updateSubmitButtonState()
    }
    
    private fun updateSubmitButtonState() {
        binding.btnSubmit.isEnabled = binding.ratingBar.rating > 0
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnSubmit.isEnabled = !isLoading && binding.ratingBar.rating > 0
        }
        
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.reviewSubmitted.observe(this) { submitted ->
            if (submitted) {
                Toast.makeText(this, getString(R.string.review_submitted_successfully), Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }
    }
} 