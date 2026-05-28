Lab12
Лабораторна робота №12
Тема: багатопотоковість, логування та автономний запуск JavaFX-застосунку.
Варіант 7: система обліку співробітників.

Що реалізовано:
1. JavaFX-застосунок на основі ЛР11.
2. MySQL + JDBC для зберігання співробітників.
3. CRUD: додавання, перегляд, редагування, видалення.
4. Фонова задача Task для перевірки співробітників.
5. ProgressBar та Label для показу прогресу.
6. Logger для фіксації дій програми.
7. Підготовка до створення myapp.jar для JavaFX-App-Template.

Перевірки для варіанта 7:
- ім'я не порожнє;
- посада задана;
- зарплата > 0.

Перед запуском:
1. Запусти MySQL у XAMPP.
2. У файлі src/main/java/com/example/lab12/dao/Database.java перевір логін і пароль:
   USER = "root"
   PASSWORD = ""
   Для XAMPP зазвичай пароль порожній.
3. Запускай через Maven -> Plugins -> javafx -> javafx:run.

Логування:
- Логи виводяться у консоль IntelliJ.
- Також створюється файл logs/Lab12.log.

Автономний запуск через JavaFX-App-Template:
1. У Maven виконай Lifecycle -> package.
2. У папці target з'явиться файл myapp.jar.
3. Скопіюй target/myapp.jar у шаблон: JavaFX-App-Template/app/myapp.jar.
4. Запусти run.bat.
5. Для створення exe запусти build-exe.bat.

Важливо:
- pom.xml налаштований на Java/JavaFX 25, щоб jar підходив до шаблону з JDK 25 та JavaFX 25.
- Якщо запускаєш тільки через IntelliJ, можна не використовувати JavaFX-App-Template.
