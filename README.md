# Проект 1-го спринта

### Описание
Репозиторий предназначен для сдачи проекта 1-го спринта

### Структура репозитория
- `/consumer/PullConsumerFactory.java` - реализация консюмера модели pull
- `/consumer/PushConsumerFactory.java` - реализация консюмера модели push
- `/producer/ProducerFactory.java` - реализация продюсера

### Как запустить контейнер
Сборка толстого jar файла:

```
gradlew clean shadowJar
```

Запустите локально Docker:

```
docker-compose up
```

Команды для работы с Kafka:

```
docker-compose exec kafka-0 kafka-topics.sh --create --topic orders --bootstrap-server kafka-0:9092 --partitions 3 --replication-factor 2
docker-compose exec kafka-0 kafka-topics.sh --describe --topic orders --bootstrap-server kafka-0:9092
```