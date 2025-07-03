# Проект 1-го спринта
![Apache Kafka](https://img.shields.io/badge/-Apache_Kafka-000?logo=apachekafka)
![Spring Boot](https://img.shields.io/badge/-Spring_Boot-6DB33F?logo=springboot)

### Описание
Репозиторий предназначен для сдачи проекта 1-го спринта

### Структура репозитория
- `src/main/java/ru/practicum/config/KafkaPullConsumerConfig.java` - реализация консюмера модели pull
- `src/main/java/ru/practicum/config/KafkaPushConsumerConfig.java` - реализация консюмера модели push
- `src/main/java/ru/practicum/config/KafkaProducerConfig.java` - реализация продюсера

### Как запустить контейнер
Сборка толстого jar файла:

```
gradlew clean kafka-standart:bootJar
```

Запустите локально Docker:

```shell
cd infra; docker-compose up -d
```

### Как протестировать работу
#### Создайте топик orders с 3 партициями и фактором репликации 2
```shell
docker exec -it kafka-0 kafka-topics.sh --create --topic orders --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
```

#### Определите текущее распределение партиций
```shell
docker exec -it kafka-0 kafka-topics.sh --describe --topic orders --bootstrap-server localhost:9092
```