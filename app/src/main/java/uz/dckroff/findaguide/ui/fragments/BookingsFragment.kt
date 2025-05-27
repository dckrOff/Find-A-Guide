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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentBookingsBinding
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.ui.adapters.BookingAdapter
import uz.dckroff.findaguide.viewmodel.BookingsViewModel

class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: BookingsViewModel by viewModels()
    
    private lateinit var bookingAdapter: BookingAdapter

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
        setupAdapter()
        observeViewModel()
        
        // Загружаем предстоящие бронирования по умолчанию
        viewModel.loadAllBookings()
    }
    
    private fun setupUI() {
        // Setup tabs
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.loadAllBookings()
                    1 -> viewModel.loadAllBookings()
                    else -> viewModel.loadAllBookings()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun setupAdapter() {
        bookingAdapter = BookingAdapter(
            onBookingClick = { booking ->
                navigateToBookingDetails(booking)
            },
            onCancelClick = { booking ->
                viewModel.cancelBooking(booking.id)
            }
        )
        
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = bookingAdapter
    }
    
    private fun observeViewModel() {
        // Наблюдаем за списком бронирований
        viewModel.upcomingBookings.observe(viewLifecycleOwner) { bookings ->
            bookingAdapter.submitList(bookings)
            
            // Показываем сообщение, если нет бронирований
            binding.tvNoBookings.isVisible = bookings.isEmpty()
            binding.rvBookings.isVisible = bookings.isNotEmpty()
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
    
    private fun navigateToBookingDetails(booking: Booking) {
        val bundle = Bundle().apply {
            putString("bookingId", booking.id)
        }
        findNavController().navigate(R.id.bookingDetailsActivity, bundle)
    }
    
    private fun navigateToChat(guideId: String) {
        val bundle = Bundle().apply {
            putString("guideId", guideId)
        }
        findNavController().navigate(R.id.chatActivity, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 