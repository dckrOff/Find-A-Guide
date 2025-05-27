package uz.dckroff.findaguide.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityChatBinding
import uz.dckroff.findaguide.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var guideId: String? = null
    
    private val viewModel: ChatViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get guide ID from intent
        guideId = intent.getStringExtra(EXTRA_GUIDE_ID)
        
        setupToolbar()
        setupMessageInput()
        setupRecyclerView()
        observeViewModel()
        
        // Загружаем сообщения
        guideId?.let { viewModel.loadMessages(it) }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Загружаем информацию о гиде для тулбара
        guideId?.let { viewModel.loadGuideInfo(it) }
    }
    
    private fun setupMessageInput() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                guideId?.let { viewModel.sendMessage(it, it, message) }
                binding.etMessage.text?.clear()
            }
        }
    }
    
    private fun setupRecyclerView() {
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        
        // В реальном приложении здесь был бы адаптер для сообщений
        // binding.rvMessages.adapter = MessagesAdapter()
    }
    
    private fun observeViewModel() {
        // Наблюдаем за информацией о гиде
        viewModel.guide.observe(this) { guide ->
            binding.tvGuideName.text = guide.name
            binding.tvStatus.text = if (guide.isOnline) getString(R.string.online) else getString(R.string.offline)
        }
        
        // Наблюдаем за сообщениями
        viewModel.messages.observe(this) { messages ->
            // В реальном приложении здесь бы обновлялся адаптер с сообщениями
            // (binding.rvMessages.adapter as? MessagesAdapter)?.submitList(messages)
            
            // Прокручиваем список к последнему сообщению
            if (messages.isNotEmpty()) {
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }
        
        // Наблюдаем за статусом отправки сообщения
        viewModel.messageSent.observe(this) { sent ->
            if (sent) {
                // Сообщение успешно отправлено
                binding.etMessage.text?.clear()
            }
        }
        
        // Наблюдаем за статусом загрузки
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
        
        // Наблюдаем за ошибками
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    companion object {
        const val EXTRA_GUIDE_ID = "extra_guide_id"
    }
} 