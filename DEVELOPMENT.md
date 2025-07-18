# Chat-With-AI - Обновленное приложение

## 🎉 Что нового в переработанной версии

### ✅ Архитектурные улучшения
- **Чистая архитектура**: Разделение на слои domain, data, presentation
- **Room Database**: Локальное хранение чатов и сообщений
- **Dependency Injection (Hilt)**: Автоматическое управление зависимостями
- **Repository Pattern**: Единая точка доступа к данным
- **MVVM с StateFlow**: Реактивное управление состоянием

### ✅ Новый современный дизайн
- **Material Design 3**: Обновленная цветовая схема
- **Анимации**: Плавные переходы и интерактивные элементы
- **Адаптивный UI**: Поддержка темной и светлой тем
- **Градиенты**: Современная индиго-голубая палитра

### ✅ Расширенный функционал
- **Управление чатами**: Создание, удаление, архивирование, закрепление
- **Поиск**: По чатам и сообщениям
- **История**: Сохранение всех разговоров
- **Статусы сообщений**: Отправлено, доставлено, ошибка
- **Индикатор печати**: Анимированный индикатор при генерации ответа

## 🏗️ Архитектура приложения

```
app/src/main/java/com/byteflipper/imageai/
├── core/                           # Ядро приложения
│   ├── data/
│   │   ├── local/                  # Room база данных
│   │   │   ├── entities/           # Entity классы
│   │   │   ├── dao/                # Data Access Objects
│   │   │   ├── converters/         # Type converters
│   │   │   └── ChatDatabase.kt     # Основная база данных
│   │   ├── mapper/                 # Маппинг между слоями
│   │   └── repository/             # Реализация репозиториев
│   ├── di/                         # Dependency Injection модули
│   ├── domain/
│   │   ├── model/                  # Доменные модели
│   │   └── repository/             # Интерфейсы репозиториев
│   ├── navigation/                 # Навигация
│   ├── presentation/               # UI состояния
│   └── theme/                      # Дизайн система
├── feature_chat/
│   └── presentation/
│       ├── chat_list/              # Экран списка чатов
│       │   ├── components/         # UI компоненты
│       │   ├── ChatListScreen.kt
│       │   └── ChatListViewModel.kt
│       └── chat_detail/            # Экран деталей чата
│           ├── components/         # UI компоненты
│           ├── ChatDetailScreen.kt
│           └── ChatDetailViewModel.kt
├── ChatApplication.kt              # Application класс с Hilt
└── MainActivity.kt                 # Основная активность с навигацией
```

## 🚀 Запуск приложения

### Требования
- Android Studio Arctic Fox или новее
- Android SDK 27 (минимум) - 35 (целевой)
- Kotlin 1.8+
- API ключ Google Gemini

### Настройка
1. Клонируйте репозиторий
2. Откройте проект в Android Studio
3. Добавьте API ключ Gemini в `local.properties`:
   ```
   apiKey=ваш_api_ключ_gemini
   ```
4. Синхронизируйте проект с Gradle
5. Запустите приложение

## 📱 Функции приложения

### Экран списка чатов
- ✅ Создание новых чатов
- ✅ Поиск по названию и содержимому
- ✅ Закрепление важных чатов
- ✅ Архивирование старых чатов
- ✅ Удаление чатов
- ✅ Отображение времени последнего сообщения
- ✅ Счетчик сообщений

### Экран чата
- ✅ Отправка текстовых сообщений
- ✅ Прикрепление изображений
- ✅ Анализ изображений с помощью Gemini Vision
- ✅ Индикатор печати
- ✅ Статусы доставки сообщений
- ✅ Повтор отправки при ошибке
- ✅ Удаление сообщений
- ✅ Автопрокрутка к новым сообщениям

### Дизайн и анимации
- ✅ Material Design 3
- ✅ Темная и светлая темы
- ✅ Плавные анимации переходов
- ✅ Интерактивные элементы
- ✅ Адаптивные цвета
- ✅ Градиентные фоны
- ✅ Современная типографика

## 🛠️ Технологический стек

### Основные технологии
- **Kotlin** - основной язык
- **Jetpack Compose** - современный UI toolkit
- **Material Design 3** - дизайн система
- **Navigation Compose** - навигация

### Архитектура
- **MVVM** - архитектурный паттерн
- **Clean Architecture** - разделение на слои
- **Repository Pattern** - абстракция данных
- **StateFlow** - реактивное программирование

### База данных
- **Room** - локальная база данных
- **kotlinx-datetime** - работа с датами
- **Type Converters** - сериализация данных

### Dependency Injection
- **Hilt** - внедрение зависимостей
- **Dagger** - DI фреймворк

### AI и API
- **Google Gemini** - генеративный ИИ
- **Gemini Vision** - анализ изображений

### UI и анимации
- **Compose Animation** - анимации
- **Accompanist** - дополнительные UI компоненты
- **Coil** - загрузка изображений
- **Lottie** - векторные анимации (готов к использованию)

## 🎨 Дизайн система

### Цветовая палитра
- **Primary**: Indigo (#6366F1)
- **Secondary**: Cyan (#06B6D4)
- **Background**: Light Gray (#FAFAFA) / Dark Blue (#0F0F23)
- **Surface**: White (#FFFFFF) / Dark Gray (#1A1A2E)

### Типографика
- **Headlines**: Робустные заголовки
- **Body**: Читаемый основной текст
- **Captions**: Вспомогательная информация

### Формы и отступы
- **Радиус**: 4dp - 24dp для разных элементов
- **Отступы**: 8dp, 16dp, 24dp система
- **Тени**: Subtle elevation для глубины

## 🔮 Планы развития

### Ближайшие обновления
- [ ] Экспорт чатов в разные форматы
- [ ] Настройки пользователя
- [ ] Дополнительные ИИ провайдеры (OpenAI, Claude)
- [ ] Голосовые сообщения
- [ ] Группировка чатов по категориям

### Долгосрочные планы
- [ ] Синхронизация между устройствами
- [ ] Плагины и расширения
- [ ] Продвинутая аналитика
- [ ] Интеграция с внешними сервисами
- [ ] Локальные ИИ модели

## 🤝 Участие в разработке

Проект открыт для предложений и улучшений:
1. Форкните репозиторий
2. Создайте feature branch
3. Внесите изменения
4. Создайте Pull Request

## 📄 Лицензия

Исходный код предназначен исключительно для личного использования. 
Коммерческое использование запрещено без разрешения автора.

---

**Создано с ❤️ для изучения современной Android разработки** 