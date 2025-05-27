package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentLoginBinding
import uz.dckroff.findaguide.ui.activities.AuthActivity
import uz.dckroff.findaguide.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupLoginButton()
        setupForgotPassword()
    }
    
    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(email, password)) {
                (activity as? AuthActivity)?.showLoading(true)
                viewModel.loginWithEmail(email, password)
            }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true
        
        // Проверяем email
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.email_required)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }
        
        // Проверяем пароль
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.password_required)
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.password_too_short)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        
        return isValid
    }
    
    private fun setupForgotPassword() {
        binding.tvForgotPassword.setOnClickListener {
            showResetPasswordDialog()
        }
    }
    
    private fun showResetPasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_reset_password, null)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.reset_password)
            .setView(dialogView)
            .setPositiveButton(R.string.send) { dialog, _ ->
                val emailEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etResetEmail)
                val email = emailEditText?.text.toString().trim()
                
                if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    viewModel.resetPassword(email)
                } else {
                    Toast.makeText(requireContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show()
                }
                
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 