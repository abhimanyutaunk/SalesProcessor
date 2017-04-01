# Sales Processor

A small message processing application

**Processing requirements**:

- All sales must be recorded

- All messages must be processed

- After every 10th message received your application should log a report detailing the number of sales of each product and their total value.

- After 50 messages your application should log that it is pausing, stop accepting new messages and log a report of the adjustments that have been made to each sale type while the application was running.

**Sales and Messages**:
- A sale has a product type field and a value – you should choose sensible types for these.
- Any number of different product types can be expected. There is no fixed set.
- A message notifying you of a sale could be one of the following types
  - Message Type 1 – contains the details of 1 sale E.g apple at 10p
  - Message Type 2 – contains the details of a sale and the number of occurrences of that sale. E.g 20 sales of apples at 10p each.
  - Message Type 3 – contains the details of a sale and an adjustment operation to be applied to all stored sales of this product type. Operations can be add, subtract, or multiply e.g Add 20p apples would instruct your application to add 20p to each sale of apples you have recorded.

**Assumptions**:
- Communication would be asynchronous
- Sales processor may stop or pause anytime. However, it should not loose messages sent by users. It should process such messages once it resumes.
- Messages should be consumed in the exact same order they were received
- Messages would be in JSON format e.g.
```
{
 productType:Apple,
 quantity:10,
 price:200
}
```
- Application should be scalable. i.e. It should be able to process millions of messages efficiently and in real time.

## Pre-requisites
- Java 8 (JDK 1.8.0_121)
- Apache Kafka 0.9.0.0
- Maven


### Step 1: Kafka 
Get Kafka up and running
```
$ cd kafka/kafka_2.11-0.9.0.0/ 
$ bin/zookeeper-server-start.sh config/zookeeper.properties
$ bin/kafka-server-start.sh config/server.properties
```

### Step 2: Topics
Create below topics:

sale: Notification of a single quantity sale of a product and it's price

multi-sale: Notification of sale where quantity is more than one of a product and it's price

adjustment: Adjustments (ADD/SUBTRACT/MULTIPLY) to existing product prices
```
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic sale
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic multi-sale
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic adjustment
```

### Step 3: Verify topics
Verify if topics are indeed created
```
$ bin/kafka-topics.sh --list --zookeeper localhost:2181```
```

### Step 4: Build
Compile and package up the sales processor application
```
$ cd Users/User/IdeaProjects/SalesProcessor/
$ mvn clean package
```

### Step 5: Stream 
Run producer to generate stream of sales messages

```
$ target/SalesProcessor producer
```

### Step 6: Process
Start sales processor consumer which will process the messages and generate reports at particular interval
```
$ target/SalesProcessor consumer
```

## Test
com.sales.process.TestProcessor will unit test logic around sale processing calculations.

## Output

Below is some snapshot from output of producer and consumer. Refer to com.sales.service.Producer to see which messages were sent to consumer.

Producer output snapshot:
```
Sent msg number 97
Sent msg number 98
Sent msg number 99
Sent msg number 100
```

(Sales Processor) Consumer output snapshot:
```
*********Adjustment Summary Report*********
        Apple -> ADD by 0.4000
        Apple -> ADD by 0.4000
        Apple -> ADD by 0.4000
        Apple -> ADD by 0.4000
        Apple -> ADD by 0.4000

******Product Summary Report********
[Type   | Quantity      | Value                 ]
[Apple  | 100           | 220.0000              ]
[Lemon  | 250           | 100.0000              ]
```

