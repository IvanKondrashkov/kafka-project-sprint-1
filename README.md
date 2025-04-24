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

#### Создайте топик orders с 3 партициями и фактором репликации 2
```shell
docker exec -it kafka-0 kafka-topics.sh --create --topic orders --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
```

#### Определите текущее распределение партиций
```shell
docker exec -it kafka-0 kafka-topics.sh --describe --topic orders --bootstrap-server localhost:9092
```