package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.FragmentRegisterBinding
import uz.dckroff.findaguide.ui.activities.AuthActivity
import uz.dckroff.findaguide.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRegisterButton()
    }
    
    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            if (validateInput(name, email, password, confirmPassword)) {
                (activity as? AuthActivity)?.showLoading(true)
                viewModel.registerWithEmail(email, password, name)
            }
        }
    }
    
    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true
        
        // Проверяем имя
        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.name_required)
            isValid = false
        } else {
            binding.tilName.error = null
        }
        
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
        
        // Проверяем подтверждение пароля
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = getString(R.string.confirm_password_required)
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.passwords_do_not_match)
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }
        
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 