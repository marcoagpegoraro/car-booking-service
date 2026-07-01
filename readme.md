# Car Booking Service

This is a microservice that is responsible for processing the requests to confirm the booking of a rental car.

It has three input adapters:
- HTTP endpoint responsible for confirming a car booking.
- Kafka consumer that consumes the bank-transfer payment-completed events and confirms the booking accordingly.
- Scheduler that runs daily to cancel any bank-transfer booking whose payment was not received at least 48 hours before the rental start date.

## Architecture

It was built using the hexagonal architecture pattern together with DDD. Each layer has a well defined responsibility, so domain leaking does not occur.

These are the main packages of the application:

- adapter
  - Responsible for the inputs of the application (HTTP, Scheduler and Kafka) and its outputs (database via JPA and the credit-card payment service via HTTP).
- domain
  - The heart of the application, free of framework/infrastructure concerns.
  - We have here the `Booking` aggregate: it contains all the logic to create a valid booking and to transition its status (`confirmPayment()` / `cancel()`) using plain Java, protecting its own invariants.
  - We also have the value object `RentalPeriod`, which holds the validation required in the assignment regarding the start and end dates (end after start, and a maximum of 21 days).
  - It also contains the enums and the domain exceptions.
- port
  - Package that contains all the contracts (interfaces) that bind the application layer together with the infrastructure layer.
- usecases
  - The business logic orchestration of the microservice; each use case has a long and explicit name explaining what it does.

## Technologies

- Java 25
- Spring Boot 4.0.1
- Postgres 16
- Kafka and Zookeeper
- Apache Avro
- Docker Compose for all the other services that the app needs to run
- OpenFeign for HTTP requests
- Spring Scheduling for the bank-transfer auto-cancellation
- Node.js to mock the payment service endpoint

## Running the application

You will need Docker and a JDK 25 installed on your machine.

First, start the dependencies (database, mocked payment service and Kafka/Zookeeper) with:

```
docker compose up -d
```

> Note: the `car-booking-service` itself is **not** part of Docker Compose — Compose only starts the dependencies above.

Then start the microservice from the project root:

```
./mvnw spring-boot:run
```

All configuration properties have sensible local defaults (see `application.properties`), so no environment variables are required to run it locally.

## API collection (Bruno)

An API collection is provided at [`docs/opencollection.yml`](docs/opencollection.yml), exported in the OpenCollection format. It can be imported into [Bruno](https://www.usebruno.com/).

It ships with a `dev` environment (`baseURL = http://localhost:8080`) and an example request for every scenario:
- booking with cash, bank transfer, and credit card (approved / refused reference);
- the validation errors (invalid enum value, start date after end date, more than 21 days, missing required field);
- direct calls to the mocked credit-card payment service (approved / rejected reference).

## Sending a Kafka payment event

The `bank-transfer-payment-events` topic (auto-created on startup) carries **Avro-encoded** `BankTransferPaymentCompletedEvent` messages — the schema lives in `src/main/avro/bank-transfer-payment-completed-event.avsc`.

Because the payload is Avro binary (not plain text/JSON), it cannot be produced with `kafka-console-producer`. A simple way to publish a valid test event is with a small Python script:

```python
# pip install fastavro kafka-python
import io, decimal
from fastavro import schemaless_writer
from kafka import KafkaProducer

schema = {
    "type": "record",
    "name": "BankTransferPaymentCompletedEvent",
    "namespace": "nl.velocitymotors.carbooking.payment.avro",
    "fields": [
        {"name": "paymentId", "type": "string"},
        {"name": "senderAccountNumber", "type": "string"},
        {"name": "paymentAmount", "type": {"type": "bytes", "logicalType": "decimal", "precision": 12, "scale": 2}},
        {"name": "transactionDetails", "type": "string"}
    ]
}

buffer = io.BytesIO()
schemaless_writer(buffer, schema, {
    "paymentId": "PAY-1",
    "senderAccountNumber": "NL00BANK0123456789",
    "paymentAmount": decimal.Decimal("250.00"),
    # Format: "<TxnRef> <BookingId>". The BookingId is the id returned when the booking was confirmed (e.g. 3).
    "transactionDetails": "TXN987654321 3"
})

producer = KafkaProducer(bootstrap_servers="localhost:9092")
producer.send("bank-transfer-payment-events", buffer.getvalue())
producer.flush()
```

When consumed, the service extracts the booking id from `transactionDetails` and moves that booking to `CONFIRMED`.

## The microservice in action

Here are a few screenshots showing the microservice working as intended.

### Bookings

Car booking paid with cash — confirmed immediately:
![booking_cash.png](docs/screenshots/booking_cash.png)

Car booking paid by bank transfer — created as `PENDING_PAYMENT`:
![booking_bank.png](docs/screenshots/booking_bank.png)

Car booking paid by credit card, payment approved — confirmed:
![booking_card_ok.png](docs/screenshots/booking_card_ok.png)

Car booking paid by credit card, payment refused:
![booking_card_nok.png](docs/screenshots/booking_card_nok.png)

### Validation errors

Invalid value for the payment mode enum:
![ve_enum.png](docs/screenshots/ve_enum.png)

Rental start date is after the end date:
![ve_greater.png](docs/screenshots/ve_greater.png)

Rental is longer than the 21-day maximum:
![ve_days.png](docs/screenshots/ve_days.png)

A required field is null:
![ve_null.png](docs/screenshots/ve_null.png)

### Mocked credit-card payment service

Approved payment reference:
![api_ok.png](docs/screenshots/api_ok.png)

Rejected payment reference:
![api_nok.png](docs/screenshots/api_nok.png)
