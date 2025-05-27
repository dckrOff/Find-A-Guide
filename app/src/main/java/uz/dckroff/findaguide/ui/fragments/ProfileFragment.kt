package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.dckroff.findaguide.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
    }
    
    private fun setupUI() {
        // Load user profile data
        loadUserProfile()
        
        // Setup edit profile button
        binding.btnEditProfile.setOnClickListener {
            toggleEditMode(true)
        }
        
        // Setup save button
        binding.btnSave.setOnClickListener {
            // Save profile changes
            saveProfile()
            toggleEditMode(false)
        }
        
        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            // Cancel changes
            loadUserProfile()
            toggleEditMode(false)
        }
        
        // Setup logout button
        binding.btnLogout.setOnClickListener {
            // Logout user
            logout()
        }
    }
    
    private fun loadUserProfile() {
        // In a real app, this would load from repository
        // For now, just show placeholder data
        binding.tvName.text = "John Doe"
        binding.tvEmail.text = "john.doe@example.com"
        binding.etName.setText("John Doe")
        binding.etEmail.setText("john.doe@example.com")
        binding.etPhone.setText("+1 234 567 8900")
    }
    
    private fun toggleEditMode(isEditMode: Boolean) {
        // Toggle between view mode and edit mode
        binding.viewProfile.visibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.viewEditProfile.visibility = if (isEditMode) View.VISIBLE else View.GONE
    }
    
    private fun saveProfile() {
        // In a real app, this would save to repository
        // For now, just update the UI
        binding.tvName.text = binding.etName.text.toString()
        binding.tvEmail.text = binding.etEmail.text.toString()
    }
    
    private fun logout() {
        // In a real app, this would clear auth state and navigate to login
        // For now, just show a message
        // Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 