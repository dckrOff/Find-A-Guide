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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentMapBinding
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.viewmodel.MapViewModel
import kotlin.random.Random

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MapViewModel by viewModels()
    
    private var googleMap: GoogleMap? = null
    private var selectedMarker: Marker? = null
    private var guideMarkers = mutableMapOf<String, Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Setup filter button
        binding.fabFilter.setOnClickListener {
            // Show filter dialog
            showFilterDialog()
        }
        
        // Setup guide card buttons
        binding.btnViewProfile.setOnClickListener {
            navigateToGuideDetails()
        }
        
        binding.btnBookNow.setOnClickListener {
            navigateToBooking()
        }
        
        // Initially hide the guide info card
        binding.cardGuideInfo.visibility = View.GONE
    }
    
    private fun observeViewModel() {
        // Наблюдаем за списком гидов на карте
        viewModel.guides.observe(viewLifecycleOwner) { guides ->
            if (googleMap != null) {
                updateMapMarkers(guides)
            }
        }
        
        // Наблюдаем за выбранным гидом
        viewModel.selectedGuide.observe(viewLifecycleOwner) { guide ->
            guide?.let {
                showGuideInfo(it)
            }
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
    
    private fun showFilterDialog() {
        // Placeholder for filter dialog
        // This would be implemented in later stages
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Set default location (New York)
        val defaultLocation = LatLng(40.7128, -74.0060)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        
        // Set marker click listener
        googleMap?.setOnMarkerClickListener { marker ->
            selectedMarker = marker
            val guideId = marker.tag as? String
            guideId?.let {
                val guide = viewModel.guides.value?.find { guide -> guide.id == it }
                guide?.let { selectedGuide ->
                    viewModel.setSelectedGuide(selectedGuide)
                }
            }
            true
        }
        
        // Загружаем гидов на карте
        viewModel.loadAllGuides()
    }
    
    private fun updateMapMarkers(guides: List<Guide>) {
        // Очищаем существующие маркеры
        googleMap?.clear()
        guideMarkers.clear()
        
        // Добавляем маркеры для каждого гида
        guides.forEach { guide ->
            // В реальном приложении здесь будут реальные координаты
            // Для примера используем случайные координаты вокруг Нью-Йорка
            val lat = 40.7128 + (Random.nextDouble() * 0.04 - 0.02)
            val lng = -74.0060 + (Random.nextDouble() * 0.04 - 0.02)
            val location = LatLng(lat, lng)
            
            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(guide.name)
                    .snippet("Rating: ${guide.rating}")
            )
            
            marker?.tag = guide.id
            marker?.let { guideMarkers[guide.id] = it }
        }
    }
    
    private fun showGuideInfo(guide: Guide) {
        // Show guide info card
        binding.cardGuideInfo.visibility = View.VISIBLE
        binding.tvGuideName.text = guide.name
        binding.tvRating.text = "Rating: ${guide.rating}"
        
        // Перемещаем карту к выбранному гиду
        guideMarkers[guide.id]?.let { marker ->
            googleMap?.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
        }
    }
    
    private fun navigateToGuideDetails() {
        viewModel.selectedGuide.value?.let { guide ->
            val bundle = Bundle().apply {
                putString("guideId", guide.id)
            }
            findNavController().navigate(R.id.guideDetailsActivity, bundle)
        }
    }
    
    private fun navigateToBooking() {
        viewModel.selectedGuide.value?.let { guide ->
            val bundle = Bundle().apply {
                putString("guideId", guide.id)
            }
            findNavController().navigate(R.id.bookingActivity, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 