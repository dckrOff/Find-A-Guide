package uz.dckroff.findaguide.di

import uz.dckroff.findaguide.repository.AuthRepository
import uz.dckroff.findaguide.repository.BookingRepository
import uz.dckroff.findaguide.repository.ChatRepository
import uz.dckroff.findaguide.repository.DestinationRepository
import uz.dckroff.findaguide.repository.GuideRepository
import uz.dckroff.findaguide.repository.ReviewRepository
import uz.dckroff.findaguide.repository.UserRepository
import uz.dckroff.findaguide.repository.impl.FirebaseAuthRepository
import uz.dckroff.findaguide.repository.impl.FirebaseBookingRepository
import uz.dckroff.findaguide.repository.impl.FirebaseChatRepository
import uz.dckroff.findaguide.repository.impl.FirebaseDestinationRepository
import uz.dckroff.findaguide.repository.impl.FirebaseGuideRepository
import uz.dckroff.findaguide.repository.impl.FirebaseReviewRepository
import uz.dckroff.findaguide.repository.impl.FirebaseUserRepository

/**
 * Модуль для предоставления репозиториев
 */
object RepositoryModule {
    
    /**
     * Предоставляет репозиторий для гидов
     */
    fun provideGuideRepository(): GuideRepository {
        return FirebaseGuideRepository()
    }
    
    /**
     * Предоставляет репозиторий для пользователей
     */
    fun provideUserRepository(): UserRepository {
        return FirebaseUserRepository()
    }
    
    /**
     * Предоставляет репозиторий для аутентификации
     */
    fun provideAuthRepository(): AuthRepository {
        return FirebaseAuthRepository()
    }
    
    /**
     * Предоставляет репозиторий для бронирований
     */
    fun provideBookingRepository(): BookingRepository {
        return FirebaseBookingRepository()
    }
    
    /**
     * Предоставляет репозиторий для направлений
     */
    fun provideDestinationRepository(): DestinationRepository {
        return FirebaseDestinationRepository()
    }
    
    /**
     * Предоставляет репозиторий для отзывов
     */
    fun provideReviewRepository(): ReviewRepository {
        return FirebaseReviewRepository()
    }
    
    /**
     * Предоставляет репозиторий для чатов
     */
    fun provideChatRepository(): ChatRepository {
        return FirebaseChatRepository()
    }
} 