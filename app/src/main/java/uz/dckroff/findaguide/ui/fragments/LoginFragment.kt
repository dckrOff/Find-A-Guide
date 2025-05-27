package uz.dckroff.findaguide.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.dckroff.findaguide.databinding.FragmentLoginBinding
import uz.dckroff.findaguide.ui.activities.AuthActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
            // Placeholder for login logic
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            
            if (email.isNotEmpty() && password.isNotEmpty()) {
                (activity as? AuthActivity)?.showLoading(true)
                
                // Simulate login delay
                view?.postDelayed({
                    (activity as? AuthActivity)?.showLoading(false)
                    (activity as? AuthActivity)?.navigateToMain()
                }, 1500)
            }
        }
    }
    
    private fun setupForgotPassword() {
        binding.tvForgotPassword.setOnClickListener {
            // Placeholder for forgot password
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 