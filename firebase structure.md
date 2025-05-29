# Структура Firebase для приложения "Find a Guide"

## Коллекции и их структура

### 1. Коллекция "guides"
```
guides/
  ├── documentId/
      ├── id: string
      ├── name: string
      ├── photo: string (URL изображения)
      ├── rating: number (float)
      ├── price: number (integer)
      ├── languages: array<string>
      ├── specializations: array<string>
      ├── description: string
      ├── location: string
      └── available: boolean
```

### 2. Коллекция "users"
```
users/
  ├── documentId/
      ├── id: string
      ├── name: string
      ├── email: string
      ├── photoUrl: string (URL изображения)
      ├── phone: string
      ├── country: string
      ├── language: string
      └── preferences: array<string>
```

### 3. Коллекция "bookings"
```
bookings/
  ├── documentId/
      ├── id: string
      ├── guideId: string
      ├── userId: string
      ├── date: timestamp
      ├── startTime: string
      ├── duration: number (integer)
      ├── price: number (integer)
      ├── status: string (PENDING, CONFIRMED, COMPLETED, CANCELLED)
      ├── notes: string
      ├── location: string
      └── createdAt: timestamp
```

### 4. Коллекция "reviews"
```
reviews/
  ├── documentId/
      ├── id: string
      ├── guideId: string
      ├── userId: string
      ├── bookingId: string
      ├── rating: number (integer)
      ├── comment: string
      └── date: timestamp
```

### 5. Коллекция "destinations"
```
destinations/
  ├── documentId/
      ├── id: string
      ├── name: string
      ├── country: string
      ├── description: string
      ├── imageUrl: string
      ├── guideCount: number (integer)
      └── rating: number (float)
```

### 6. Коллекция "chats"
```
chats/
  ├── documentId/
      ├── id: string
      ├── userId: string
      ├── guideId: string
      ├── lastMessage: string
      ├── lastMessageTime: timestamp
      └── unreadCount: number (integer)
```

### 7. Коллекция "messages"
```
messages/
  ├── documentId/
      ├── id: string
      ├── chatId: string
      ├── senderId: string
      ├── receiverId: string
      ├── text: string
      ├── timestamp: timestamp
      ├── isRead: boolean
      └── attachmentUrl: string (nullable)
```

## Примечания по работе с данными

1. **documentId** - это автоматически генерируемый Firebase ID документа
2. **id** - это дополнительный идентификатор, который мы генерируем для удобства использования в приложении
3. Все **timestamp** поля хранятся как Firebase Timestamp объекты
4. Для полей с URL изображений используются ссылки на хранилище (например, Unsplash)
5. Поля типа **array** содержат массивы строк
6. Поля типа **boolean** содержат значения true/false

## Связи между коллекциями

- **bookings** связаны с **guides** через поле guideId
- **bookings** связаны с **users** через поле userId
- **reviews** связаны с **guides** через поле guideId
- **reviews** связаны с **users** через поле userId
- **reviews** связаны с **bookings** через поле bookingId
- **chats** связаны с **guides** через поле guideId
- **chats** связаны с **users** через поле userId
- **messages** связаны с **chats** через поле chatId

## Индексирование

Для оптимальной производительности приложения рекомендуется создать следующие индексы:

1. Коллекция **bookings**:
   - Составной индекс по полям: guideId, status, date
   - Составной индекс по полям: userId, status, date

2. Коллекция **reviews**:
   - Составной индекс по полям: guideId, date
   - Составной индекс по полям: userId, date

3. Коллекция **messages**:
   - Составной индекс по полям: chatId, timestamp
