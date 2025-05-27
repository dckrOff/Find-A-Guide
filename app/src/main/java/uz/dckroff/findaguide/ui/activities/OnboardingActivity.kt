package uz.dckroff.findaguide.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityOnboardingBinding
import uz.dckroff.findaguide.ui.adapters.OnboardingAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupButtons()
    }
    
    private fun setupViewPager() {
        val adapter = OnboardingAdapter()
        binding.viewPager.adapter = adapter
        
        binding.dotsIndicator.attachTo(binding.viewPager)
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtonsVisibility(position)
            }
        })
    }
    
    private fun setupButtons() {
        binding.btnSkip.setOnClickListener {
            navigateToAuth()
        }
        
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < 2) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                navigateToAuth()
            }
        }
    }
    
    private fun updateButtonsVisibility(position: Int) {
        if (position == 2) {
            binding.btnNext.text = getString(R.string.get_started)
            binding.btnSkip.visibility = android.view.View.GONE
        } else {
            binding.btnNext.text = getString(R.string.next)
            binding.btnSkip.visibility = android.view.View.VISIBLE
        }
    }
    
    private fun navigateToAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
} 