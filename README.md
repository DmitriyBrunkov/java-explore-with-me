# java-explore-with-me

Сервис для обмена информацией о событиях между пользователями и их координации

### Состоит из двух сервисов:

- Основной сервис
- Сервис статистики

### Основной сервис:

Реализует основную логику
API сервиса разделено на три части:

* **Административная:**
    * /admin/categories POST - Добавление новой категории
    * /admin/categories/{catId} DELETE - Удаление категории
    * /admin/categories/{catId} PATCH - Изменение категории
    * /admin/events GET - Получение событий
    * /admin/events/{eventId} PATCH - Редактирование события
    * /admin/users GET - Получение информации о пользователях
    * /admin/users POST - Добавление нового пользователя
    * /admin/users/{userId} DELETE - Удаление пользователя
    * /admin/compilations POST - Добавление новой подборки
    * /admin/compilations/{compId} DELETE - Удаление подборки
    * /admin/compilations/{compId} PATCH - Обновить подборки
* **Приватная:**
    * /users/{userId}/events GET - Получение событий пользователя
    * /users/{userId}/events POST - Добавление нового события
    * /users/{userId}/events/{eventId} GET - Получение информации о событии пользователя
    * /users/{userId}/events/{eventId} PATCH - Изменение события пользователя
    * /users/{userId}/events/{eventId}/requests GET - Получение информации о запросах на участие в событии пользователя
    * /users/{userId}/events/{eventId}/requests PATCH - Изменение статуса заявки на участие в событии пользователя
    * /users/{userId}/requests GET - Получение информации о заявках пользователя
    * /users/{userId}/requests POST - Добавление запроса от пользователя на участие в событии
    * /users/{userId}/requests/{requestId}/cancel PATCH - Отмена запроса пользователя на участие в событии
* **Публичная:**
    * /categories GET - Получение категорий
    * /categories/{catId} GET - Получение информации о категории по её id
    * /compilations GET - Получение подборок событий
    * /compilations/{compId} GET - Получение подборки событий по его id
    * /events GET - Получение событий с возможностью фильтрации
    * /events/{eventId} GET - Получение события по его id

### Сервис статистики:

Регистрирует обращения пользователей к спискам событий иформирует статистику

**API сервиса статистики**:

* /hit POST - Регистрация обращения
* /stats GET - Получение статистики по обращениям

### Дополнительная функциональность: Комментарии

API разделено на 3 части:

* **Административная:**
    * /admin/comments/{commentId} GET - Получение комментария по id
    * /admin/comments/{commentId} PATCH - Изменение комментария по id
    * /admin/comments/{commentId} DELETE - Удаление комментария по id
* **Приватная:**
    * /users/{userId}/comments GET - Получение своих комментариев пользователя
    * /users/{userId}/event/{eventId}/comments POST - Создание комментария
    * /users/{userId}/event/{eventId}/comments/{commentId} PATCH - Изменение комментария
    * /users/{userId}/event/{eventId}/comments/{commentId} DELETE - Удаление комментария
* **Публичная:**
    * /events/{eventId}/comments GET - Получение комментариев события

[Ссылка на PR]()