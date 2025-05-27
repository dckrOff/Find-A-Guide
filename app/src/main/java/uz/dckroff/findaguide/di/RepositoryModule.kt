package uz.dckroff.findaguide.di

import uz.dckroff.findaguide.repository.*
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
     * Предоставляет репозиторий для работы с пользователями
     */
    fun provideUserRepository(): UserRepository {
        return FirebaseUserRepository()
    }
    
    /**
     * Предоставляет репозиторий для работы с аутентификацией
     */
    fun provideAuthRepository(): AuthRepository {
        return FirebaseAuthRepository()
    }
    
    /**
     * Предоставляет репозиторий для работы с гидами
     */
    fun provideGuideRepository(): GuideRepository {
        return FirebaseGuideRepository()
    }
    
    /**
     * Предоставляет репозиторий для работы с бронированиями
     */
    fun provideBookingRepository(): BookingRepository {
        return FirebaseBookingRepository()
    }
    
    /**
     * Предоставляет репозиторий для работы с отзывами
     */
    fun provideReviewRepository(): ReviewRepository {
        return FirebaseReviewRepository()
    }
    
    /**
     * Предоставляет репозиторий для работы с чатами
     */
    fun provideChatRepository(): ChatRepository {
        return FirebaseChatRepository()
    }
    
    /**
     * Предоставляет репозиторий для работы с направлениями
     */
    fun provideDestinationRepository(): DestinationRepository {
        return FirebaseDestinationRepository()
    }
} 