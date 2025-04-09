# Проект 1-го спринта

### Описание
Репозиторий предназначен для сдачи проекта 1-го спринта

### Структура репозитория
- `/config/KafkaPullConsumerConfig.java` - реализация консюмера модели pull
- `/config/KafkaPushConsumerConfig.java` - реализация консюмера модели push
- `/config/KafkaProducerConfig.java` - реализация продюсера

### Как запустить контейнер
Сборка толстого jar файла:

```
gradlew clean kafka-standart:bootJar
```

Запустите локально Docker:

```shell
cd infra; docker-compose up -d
```

Команды для работы с Kafka:

```
docker-compose exec kafka-0 kafka-topics.sh --create --topic orders --bootstrap-server kafka-0:9092 --partitions 3 --replication-factor 2
docker-compose exec kafka-0 kafka-topics.sh --describe --topic orders --bootstrap-server kafka-0:9092
```