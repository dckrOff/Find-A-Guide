package uz.dckroff.findaguide.ui.fragments

import android.app.AlertDialog
import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentBookingsBinding
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.ui.activities.BookingDetailsActivity
import uz.dckroff.findaguide.ui.activities.ChatActivity
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
                    0 -> showUpcomingBookings()
                    1 -> showPastBookings()
                    else -> showUpcomingBookings()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Устанавливаем заголовки вкладок
        binding.tabLayout.getTabAt(0)?.text = getString(R.string.upcoming)
        binding.tabLayout.getTabAt(1)?.text = getString(R.string.past)
    }
    
    private fun setupAdapter() {
        bookingAdapter = BookingAdapter(
            onBookingClick = { booking ->
                navigateToBookingDetails(booking)
            },
            onCancelClick = { booking ->
                showCancelBookingDialog(booking)
            }
        )
        
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = bookingAdapter
    }
    
    private fun observeViewModel() {
        // Наблюдаем за списком предстоящих бронирований
        viewModel.upcomingBookings.observe(viewLifecycleOwner) { bookings ->
            if (binding.tabLayout.selectedTabPosition == 0) {
                updateBookingsList(bookings)
            }
        }
        
        // Наблюдаем за списком прошедших бронирований
        viewModel.pastBookings.observe(viewLifecycleOwner) { bookings ->
            if (binding.tabLayout.selectedTabPosition == 1) {
                updateBookingsList(bookings)
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
        
        // Наблюдаем за успешными сообщениями
        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUpcomingBookings() {
        // Показываем предстоящие бронирования
        val bookings = viewModel.upcomingBookings.value ?: emptyList()
        updateBookingsList(bookings)
        
        // Обновляем UI для предстоящих бронирований
        binding.tvNoBookings.text = getString(R.string.no_upcoming_bookings)
    }

    private fun showPastBookings() {
        // Показываем прошедшие бронирования
        val bookings = viewModel.pastBookings.value ?: emptyList()
        updateBookingsList(bookings)
        
        // Обновляем UI для прошедших бронирований
        binding.tvNoBookings.text = getString(R.string.no_past_bookings)
    }
    
    private fun updateBookingsList(bookings: List<Booking>) {
        bookingAdapter.submitList(bookings)
        
        // Показываем сообщение, если нет бронирований
        binding.tvNoBookings.isVisible = bookings.isEmpty()
        binding.rvBookings.isVisible = bookings.isNotEmpty()
    }
    
    private fun showCancelBookingDialog(booking: Booking) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.cancel)
            .setMessage(R.string.confirm_cancel_booking)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.cancelBooking(booking.id)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun navigateToBookingDetails(booking: Booking) {
        val intent = Intent(requireContext(), BookingDetailsActivity::class.java).apply {
            putExtra("bookingId", booking.id)
        }
        startActivity(intent)
    }
    
    private fun navigateToChat(guideId: String) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("guideId", guideId)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 