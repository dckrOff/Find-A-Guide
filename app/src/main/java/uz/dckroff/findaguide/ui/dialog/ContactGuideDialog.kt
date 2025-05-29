package uz.dckroff.findaguide.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.DialogContactGuideBinding
import uz.dckroff.findaguide.model.Guide

/**
 * Диалог для отображения вариантов связи с гидом
 */
class ContactGuideDialog(
    context: Context,
    private val guide: Guide
) : Dialog(context) {

    private val binding: DialogContactGuideBinding = DialogContactGuideBinding.inflate(
        LayoutInflater.from(context)
    )

    init {
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val desiredWidth = (screenWidth * 0.9).toInt()

        window?.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        setupContactOptions()

        // Кнопка закрытия диалога
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupContactOptions() {
        // Настройка видимости и действий для каждого варианта связи

        // Телефон
        if (guide.phone.isNotEmpty()) {
            binding.cardPhone.visibility = View.VISIBLE
            binding.cardPhone.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${guide.phone}")
                }
                try {
                    context.startActivity(intent)
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open phone app", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.cardPhone.visibility = View.GONE
        }

        // Telegram
        if (guide.telegram.isNotEmpty()) {
            binding.cardTelegram.visibility = View.VISIBLE
            binding.cardTelegram.setOnClickListener {
                val username = guide.telegram.replace("@", "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://t.me/$username")
                }
                try {
                    context.startActivity(intent)
                    dismiss()
                } catch (e: Exception) {
                    // Если приложение Telegram не установлено, открываем веб-версию
                    val webIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://t.me/$username")
                    }
                    try {
                        context.startActivity(webIntent)
                        dismiss()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open Telegram", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        } else {
            binding.cardTelegram.visibility = View.GONE
        }

        // WhatsApp
        if (guide.whatsapp.isNotEmpty()) {
            binding.cardWhatsapp.visibility = View.VISIBLE
            binding.cardWhatsapp.setOnClickListener {
                val phoneNumber = guide.whatsapp.replace("+", "").replace(" ", "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
                }
                try {
                    context.startActivity(intent)
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.cardWhatsapp.visibility = View.GONE
        }

        // Instagram
        if (guide.instagram.isNotEmpty()) {
            binding.cardInstagram.visibility = View.VISIBLE
            binding.cardInstagram.setOnClickListener {
                val username = guide.instagram.replace("@", "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://instagram.com/$username")
                }
                try {
                    context.startActivity(intent)
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open Instagram", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.cardInstagram.visibility = View.GONE
        }

        // Email
        if (guide.email.isNotEmpty()) {
            binding.cardEmail.visibility = View.VISIBLE
            binding.cardEmail.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${guide.email}")
                    putExtra(Intent.EXTRA_SUBJECT, "Booking Inquiry")
                }
                try {
                    context.startActivity(intent)
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open email app", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.cardEmail.visibility = View.GONE
        }

        // Проверяем, есть ли хотя бы один контакт
        val hasAnyContact = guide.phone.isNotEmpty() ||
                guide.telegram.isNotEmpty() ||
                guide.whatsapp.isNotEmpty() ||
                guide.instagram.isNotEmpty() ||
                guide.email.isNotEmpty()

        if (!hasAnyContact) {
            // Если нет контактной информации, показываем сообщение
            Toast.makeText(context, R.string.no_contact_info, Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}