package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentHomeBinding
import uz.dckroff.findaguide.model.Destination
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.ui.activities.MainActivity
import uz.dckroff.findaguide.ui.adapters.DestinationAdapter
import uz.dckroff.findaguide.ui.adapters.GuideAdapter
import uz.dckroff.findaguide.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var guideAdapter: GuideAdapter
    private lateinit var destinationAdapter: DestinationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupUI()
        setupListeners()
        observeViewModel()

        // Загружаем данные для главного экрана
        viewModel.loadHomeData()
    }

    private fun setupAdapters() {
        // Настраиваем адаптер для избранных гидов
        guideAdapter = GuideAdapter(
            onGuideClick = { guide ->
                navigateToGuideDetails(guide)
            },
            viewType = GuideAdapter.VIEW_TYPE_FEATURED
        )
        binding.rvFeaturedGuides.adapter = guideAdapter

        // Настраиваем адаптер для популярных направлений
        destinationAdapter = DestinationAdapter { destination ->
            navigateToSearch(destination)
        }
        binding.rvPopularDestinations.adapter = destinationAdapter
    }

    private fun setupUI() {
        // Placeholder for UI setup
        binding.tvWelcome.text = getString(R.string.welcome_tourist)
    }

    private fun setupListeners() {
        // Setup search button
        binding.btnSearch.setOnClickListener {
            val location = binding.etLocation.text.toString().trim()
            if (location.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString("location", location)
                }
                // Навигация с передачей аргумента
                findNavController().navigate(R.id.searchFragment, bundle)
            } else {
                // Вместо navigate(action), переключаемся на item bottomNavigation
                (activity as? MainActivity)?.switchToTab(R.id.searchFragment)
            }
        }

    }

    private fun observeViewModel() {
        // Наблюдаем за списком избранных гидов
        viewModel.featuredGuides.observe(viewLifecycleOwner) { guides ->
            guideAdapter.submitList(guides)
        }

        // Наблюдаем за списком популярных направлений
        viewModel.popularDestinations.observe(viewLifecycleOwner) { destinations ->
            destinationAdapter.submitList(destinations)
        }

        // Наблюдаем за статусом загрузки
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        // Наблюдаем за ошибками
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Log.e("FindAGuide", "observeViewModel: e: $errorMessage")
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToGuideDetails(guide: Guide) {
        val bundle = Bundle().apply {
            putString("guideId", guide.id)
        }
        findNavController().navigate(R.id.action_homeFragment_to_guideDetailsActivity, bundle)
    }

    private fun navigateToSearch(destination: Destination) {
        val bundle = Bundle().apply {
            putString("location", destination.name)
        }
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment, bundle)
    }

    private fun navigateToSearchWithLocation(location: String) {
        val bundle = Bundle().apply {
            putString("location", location)
        }
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 