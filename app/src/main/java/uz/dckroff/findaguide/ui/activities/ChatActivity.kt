package uz.dckroff.findaguide.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var guideId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get guide ID from intent
        guideId = intent.getStringExtra(EXTRA_GUIDE_ID)
        
        setupToolbar()
        setupMessageInput()
        loadMessages()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        
        // Set guide name and status
        binding.tvGuideName.text = "John Smith"
        binding.tvStatus.text = getString(R.string.online)
    }
    
    private fun setupMessageInput() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }
    }
    
    private fun loadMessages() {
        // In a real app, this would load from repository
        // For now, just show placeholder messages
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        
        // In Stage 1, we just show the UI without adapters
        // val messages = getPlaceholderMessages()
        // binding.rvMessages.adapter = MessagesAdapter(messages)
    }
    
    private fun sendMessage(text: String) {
        // In a real app, this would send to repository
        // For now, just add to the UI
        // val message = Message(text, true, System.currentTimeMillis())
        // (binding.rvMessages.adapter as? MessagesAdapter)?.addMessage(message)
        // binding.rvMessages.scrollToPosition((binding.rvMessages.adapter?.itemCount ?: 1) - 1)
    }
    
    companion object {
        const val EXTRA_GUIDE_ID = "extra_guide_id"
    }
} 