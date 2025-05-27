package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    private var googleMap: GoogleMap? = null
    private var selectedMarker: Marker? = null

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
            navigateToGuideDetails()
        }
        
        // Initially hide the guide info card
        binding.cardGuideInfo.visibility = View.GONE
    }
    
    private fun showFilterDialog() {
        // Placeholder for filter dialog
        // This would be implemented in later stages
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Add some placeholder guide markers
        addGuidePlaceholders()
        
        // Set default location (New York)
        val defaultLocation = LatLng(40.7128, -74.0060)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }
    
    private fun addGuidePlaceholders() {
        // Add placeholder guides to the map
        val locations = listOf(
            LatLng(40.7128, -74.0060), // New York
            LatLng(40.7282, -73.9942), // Manhattan
            LatLng(40.7484, -73.9857), // Times Square
            LatLng(40.7527, -73.9772)  // Midtown
        )
        
        val names = listOf("John Smith", "Maria Garcia", "Alex Johnson", "Emma Wilson")
        val ratings = listOf(4.8, 4.5, 4.9, 4.7)
        
        for (i in locations.indices) {
            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(locations[i])
                    .title(names[i])
                    .snippet("Rating: ${ratings[i]}")
            )
            marker?.tag = "guide_id_$i" // Store guide ID for later use
        }
        
        // Set marker click listener
        googleMap?.setOnMarkerClickListener { marker ->
            selectedMarker = marker
            showGuideInfo(marker)
            true
        }
    }
    
    private fun showGuideInfo(marker: Marker) {
        // Show guide info card
        binding.cardGuideInfo.visibility = View.VISIBLE
        binding.tvGuideName.text = marker.title
        binding.tvRating.text = marker.snippet
    }
    
    private fun navigateToGuideDetails() {
        val guideId = selectedMarker?.tag as? String ?: "sample_guide_id"
        val bundle = Bundle().apply {
            putString("guideId", guideId)
        }
        findNavController().navigate(R.id.action_mapFragment_to_guideDetailsActivity, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 