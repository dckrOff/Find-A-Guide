package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentBookingsBinding

class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
    }
    
    private fun setupUI() {
        // Setup tabs
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showUpcomingBookings()
                    1 -> showPastBookings()
                    else -> showUpcomingBookings()
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
        
        // Default to upcoming bookings
        showUpcomingBookings()
        
        // Setup RecyclerView click listener when we have the adapter
        binding.rvBookings.setOnClickListener {
            navigateToChat("sample_guide_id")
        }
    }
    
    private fun showUpcomingBookings() {
        // In a real app, this would fetch data from repository
        // For now, show placeholder data
        binding.tvNoBookings.visibility = View.GONE
        binding.rvBookings.visibility = View.VISIBLE
        
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        // In Stage 1, we just show the UI without adapters
        // binding.rvBookings.adapter = BookingsAdapter(getUpcomingBookingsPlaceholder())
    }
    
    private fun showPastBookings() {
        // In a real app, this would fetch data from repository
        // For now, show placeholder data
        binding.tvNoBookings.visibility = View.GONE
        binding.rvBookings.visibility = View.VISIBLE
        
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        // In Stage 1, we just show the UI without adapters
        // binding.rvBookings.adapter = BookingsAdapter(getPastBookingsPlaceholder())
    }
    
    private fun navigateToChat(guideId: String) {
        val bundle = Bundle().apply {
            putString("guideId", guideId)
        }
        findNavController().navigate(R.id.action_bookingsFragment_to_chatActivity, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 