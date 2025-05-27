package uz.dckroff.findaguide.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import uz.dckroff.findaguide.databinding.FragmentProfileBinding
import uz.dckroff.findaguide.ui.activities.AuthActivity
import uz.dckroff.findaguide.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
        
        // Загружаем профиль пользователя
        viewModel.loadUserProfile()
    }
    
    private fun setupUI() {
        // Setup edit profile button
        binding.btnEditProfile.setOnClickListener {
            toggleEditMode(true)
        }
        
        // Setup save button
        binding.btnSave.setOnClickListener {
            // Save profile changes
            viewModel.updateUserProfile(
                name = binding.etName.text.toString(),
                email = binding.etEmail.text.toString(),
                photoUrl = null
            )
            toggleEditMode(false)
        }
        
        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            // Cancel changes
            toggleEditMode(false)
            viewModel.loadUserProfile()
        }
        
        // Setup logout button
        binding.btnLogout.setOnClickListener {
            // Logout user
            viewModel.logout()
        }
    }
    
    private fun observeViewModel() {
        // Наблюдаем за данными профиля
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phone ?: "")
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
        
        // Наблюдаем за статусом выхода
        viewModel.isLoggedOut.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                // Переход на экран авторизации
                startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                activity?.finish()
            }
        }
    }
    
    private fun toggleEditMode(isEditMode: Boolean) {
        // Toggle between view mode and edit mode
        binding.viewProfile.visibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.viewEditProfile.visibility = if (isEditMode) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 