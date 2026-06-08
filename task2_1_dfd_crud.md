# task2_1. DFD и CRUD-анализ

Проект: ПС «Организация волонтерской помощи»

Источник задания: [task2_1.md](https://github.com/olgmina/SWEngineering-technics.github.io/blob/gh-pages/2_Design/task2_1.md)

## 1. DFD уровня 0
![DFD уровня 0](https://github.com/Ekaterina01122002/volunteer-help-system/blob/main/pictures/DFD%20уровня%200.png)

## 2. DFD уровня 1

```mermaid
flowchart LR
    Applicant["Заявитель\n(источник данных)"]
    Coordinator["Координатор"]
    VolunteerActor["Волонтер"]
    Admin["Администратор / оператор"]
    Weather["OpenWeatherMap API / заглушка"]

    P1(("1. Обработать заявки"))
    P2(("2. Управлять волонтерами"))
    P3(("3. Назначить волонтера"))
    P4(("4. Проверить погоду"))
    P5(("5. Проверить просрочки"))
    P6(("6. Сформировать уведомление"))
    P7(("7. Показать статистику и фильтры"))

    D1[("D1 Заявки")]
    D2[("D2 Волонтеры")]
    D3[("D3 Погодные предупреждения")]
    D4[("D4 Уведомления")]

    Applicant -->|данные заявки| Admin
    Admin -->|создать/изменить заявку| P1
    Coordinator -->|создать/изменить/отменить| P1
    P1 -->|заявка| D1
    P1 -->|уличная заявка| P4
    P4 -->|адрес| Weather
    Weather -->|погода| P4
    P4 -->|предупреждение| D3
    P4 -->|обновленная заявка| D1

    Admin -->|данные волонтера| P2
    P2 -->|волонтер| D2

    Coordinator -->|выбранные заявка и волонтер| P3
    P3 -->|читать заявку| D1
    P3 -->|читать волонтера| D2
    P3 -->|заявка в работе| D1

    VolunteerActor -->|выполнение заявки| P1
    P5 -->|читать новые заявки| D1
    Coordinator -->|команда проверки| P5
    P5 -->|просроченная заявка| D1
    P5 -->|данные просрочки| P6
    P6 -->|сообщение| D4
    P6 -->|уведомление| Coordinator

    Coordinator -->|параметры фильтра| P7
    P7 -->|читать заявки| D1
    P7 -->|читать волонтеров| D2
    P7 -->|список и счетчики| Coordinator
```

Хранилища на DFD показаны как логические группы данных. В физическом JSON-файле `WeatherWarning` хранится внутри соответствующей записи `HelpRequest` в поле `weatherWarning`, а не в отдельном top-level массиве.

## 3. DFD уровня 2

### 3.1 Декомпозиция процесса «3. Назначить волонтера»
![Декомпозиция процесса «3. Назначить волонтера»](https://github.com/Ekaterina01122002/volunteer-help-system/blob/main/pictures/Декомпозиция%20процесса%20«3.%20Назначить%20волонтера».png)

### 3.2 Декомпозиция процесса «5. Проверить просрочки»

```mermaid
flowchart LR
    C["Координатор"] -->|запуск проверки| P51(("5.1 Выбрать новые заявки"))
    D1[("D1 Заявки")]
    D4[("D4 Уведомления")]
    P51 -->|читать заявки| D1
    P51 -->|заявки без волонтера| P52(("5.2 Проверить возраст > 3 дней"))
    P52 -->|просроченные заявки| P53(("5.3 Установить статус Просрочена"))
    P53 -->|данные просрочки| P54(("5.4 Сформировать сообщение"))
    P53 -->|обновить заявку| D1
    P54 -->|уведомление| D4
    P54 -->|результат проверки| C
```

## 4. Информационная модель

```mermaid
erDiagram
    HELP_REQUEST ||--o| WEATHER_WARNING : contains
    HELP_REQUEST ||--o{ NOTIFICATION_MESSAGE : creates
    VOLUNTEER ||--o{ HELP_REQUEST : assigned_to

    HELP_REQUEST {
        string id
        string applicantName
        string applicantPhone
        HelpType helpType
        LocationType locationType
        string address
        string plannedDateTime
        RequestStatus status
        string assignedVolunteerId
        string createdAt
        string updatedAt
        string completedAt
        string cancelReason
        string statusHistory
        string notificationText
    }

    VOLUNTEER {
        string id
        string fullName
        string phone
        string skills
        boolean available
        int assignedCount
    }

    WEATHER_WARNING {
        string id
        string requestId
        double temperature
        string condition
        string warningText
        string checkedAt
    }

    NOTIFICATION_MESSAGE {
        string id
        string requestId
        string coordinatorName
        string coordinatorPhone
        string messageText
        string createdAt
        string deliveryStatus
    }
```

История статусов не выделяется в отдельную сущность ER-модели. Она хранится как атрибут `statusHistory` внутри сущности `HelpRequest`, поэтому отдельного DFD-хранилища `D5 История статусов` и отдельной строки `StatusHistory` в CRUD-матрице нет.

## 5. CRUD-матрица

| Сущность | Create | Read | Update | Delete | Функции |
| --- | --- | --- | --- | --- | --- |
| HelpRequest / Заявка | + | + | + | - | обработать заявки, назначить волонтера, проверить просрочки, показать фильтры, вести встроенную историю статусов |
| Volunteer / Волонтер | + | + | + | + | управлять волонтерами, назначить волонтера, показать фильтры |
| WeatherWarning / Погодное предупреждение | + | + | + | - | проверить погоду, обработать заявки |
| NotificationMessage / Уведомление | + | + | - | - | сформировать уведомление, показать уведомления |

## 6. Спецификация функций

| Функция DFD уровня 1 | Входные данные | Выходные данные | CRUD-операции |
| --- | --- | --- | --- |
| 1. Обработать заявки | данные заявки, команда редактирования/отмены/завершения | заявка, карточка заявки, новый статус | HelpRequest C/R/U |
| 2. Управлять волонтерами | ФИО, телефон, навыки, доступность | запись волонтера | Volunteer C/R/U/D |
| 3. Назначить волонтера | выбранная заявка, выбранный волонтер | заявка в статусе «В работе» | HelpRequest R/U, Volunteer R/U |
| 4. Проверить погоду | адрес и тип места заявки | погодное предупреждение или пустой результат | HelpRequest R/U, WeatherWarning C/R/U |
| 5. Проверить просрочки | список новых заявок, текущая дата | просроченные заявки | HelpRequest R/U |
| 6. Сформировать уведомление | данные просроченной заявки, координатор | текст сообщения координатору | NotificationMessage C/R, HelpRequest U |
| 7. Показать статистику и фильтры | статус, тип помощи, дата, волонтер, поисковая строка | отфильтрованный список, счетчики | HelpRequest R, Volunteer R, NotificationMessage R |

Примечание: операции изменения истории статусов выполняются как обновление атрибута `statusHistory` сущности `HelpRequest`.

## 7. Согласование с информационной моделью

| Проверка | Статус | Комментарий |
| --- | --- | --- |
| Каждое хранилище DFD соответствует сущности ER | Да | D1 соответствует HelpRequest, D2 Volunteer, D3 WeatherWarning, D4 NotificationMessage; D3 является логической группой, физически вложенной в HelpRequest |
| Каждая сущность ER имеет хотя бы одну CRUD-операцию | Да | Все сущности используются в CRUD-матрице |
| Нет функций, работающих с несуществующими сущностями | Да | Все функции используют сущности модели приложения |
| Нет хранилищ, связанных напрямую без процесса | Да | Все потоки проходят через процессы DFD |
| CRUD-операции покрыты функциями DFD | Да | Для каждой операции указана функция-исполнитель |
| История статусов согласована с моделью хранения | Да | `statusHistory` хранится внутри HelpRequest и обновляется через операции HelpRequest U |

## 8. Трассировка требований

| Требование | Use case | Процесс DFD | Сущности |
| --- | --- | --- | --- |
| FR-01 | UC-01 | 1. Обработать заявки | HelpRequest |
| FR-07, FR-08 | UC-02 | 3. Назначить волонтера | HelpRequest, Volunteer |
| FR-11, FR-12 | UC-04 | 5. Проверить просрочки | HelpRequest |
| FR-13 | UC-04 | 6. Сформировать уведомление | NotificationMessage, HelpRequest |
| FR-14, FR-15 | UC-01 | 4. Проверить погоду | WeatherWarning, HelpRequest |
| FR-20 - FR-24 | UC-02, UC-04 | 7. Показать статистику и фильтры | HelpRequest, Volunteer |
