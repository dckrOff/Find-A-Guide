package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.RangeSlider
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentSearchBinding
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.ui.adapters.GuideAdapter
import uz.dckroff.findaguide.viewmodel.SearchViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchViewModel by viewModels()
    
    private lateinit var guideAdapter: GuideAdapter
    
    // Параметры фильтрации
    private var selectedLanguages = mutableListOf<String>()
    private var selectedSpecializations = mutableListOf<String>()
    private var minPrice = 0
    private var maxPrice = 500

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupUI()
        setupListeners()
        observeViewModel()
        
        // Проверяем, передано ли местоположение из предыдущего экрана
        arguments?.getString("location")?.let { location ->
            binding.etLocation.setText(location)
            searchGuides()
        } ?: run {
            // Загружаем все гиды при первом открытии экрана
            viewModel.loadAllGuides()
        }
    }
    
    private fun setupAdapter() {
        guideAdapter = GuideAdapter { guide ->
            navigateToGuideDetails(guide)
        }
        binding.rvSearchResults.adapter = guideAdapter
    }

    private fun setupUI() {
        // Setup filter chips
        setupFilterChips()
        
        // Настройка слайдера цен
        binding.sliderPrice.valueFrom = 0f
        binding.sliderPrice.valueTo = 500f
        binding.sliderPrice.values = listOf(0f, 500f)
    }

    private fun setupListeners() {
        // Setup search functionality
        binding.btnApplyFilters.setOnClickListener {
            // Apply filters and search
            searchGuides()
        }
        
        // Слушатель для слайдера цен
        binding.sliderPrice.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minPrice = values[0].toInt()
            maxPrice = values[1].toInt()
        }
    }

    private fun setupFilterChips() {
        // Setup language filter chips
        binding.chipEnglish.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedLanguages, getString(R.string.english), isChecked)
        }
        binding.chipSpanish.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedLanguages, getString(R.string.spanish), isChecked)
        }
        binding.chipFrench.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedLanguages, getString(R.string.french), isChecked)
        }
        binding.chipGerman.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedLanguages, getString(R.string.german), isChecked)
        }

        // Setup specialization filter chips
        binding.chipHistory.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedSpecializations, getString(R.string.history), isChecked)
        }
        binding.chipFood.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedSpecializations, getString(R.string.food), isChecked)
        }
        binding.chipNature.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedSpecializations, getString(R.string.nature), isChecked)
        }
        binding.chipAdventure.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedList(selectedSpecializations, getString(R.string.adventure), isChecked)
        }
    }
    
    private fun updateSelectedList(list: MutableList<String>, item: String, isSelected: Boolean) {
        if (isSelected) {
            if (!list.contains(item)) {
                list.add(item)
            }
        } else {
            list.remove(item)
        }
    }

    private fun searchGuides() {
        val location = binding.etLocation.text.toString().trim()
        
        // Преобразуем списки в null, если они пусты
        val languages = if (selectedLanguages.isEmpty()) null else selectedLanguages
        val specializations = if (selectedSpecializations.isEmpty()) null else selectedSpecializations
        
        // Применяем фильтры
        viewModel.searchGuides(
            location = if (location.isEmpty()) null else location,
            languages = languages,
            specializations = specializations,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minRating = null // Не применяем фильтр по рейтингу
        )
    }
    
    private fun observeViewModel() {
        // Наблюдаем за результатами поиска
        viewModel.searchResults.observe(viewLifecycleOwner) { guides ->
            guideAdapter.submitList(guides)
            
            // Показываем сообщение, если нет результатов
            binding.tvNoResults.isVisible = guides.isEmpty()
            binding.rvSearchResults.isVisible = guides.isNotEmpty()
        }
        
        // Наблюдаем за статусом загрузки
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
        
        // Наблюдаем за ошибками
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToGuideDetails(guide: Guide) {
        val bundle = Bundle().apply {
            putString("guideId", guide.id)
        }
        findNavController().navigate(R.id.action_searchFragment_to_guideDetailsActivity, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 