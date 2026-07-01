import avro from 'avsc';
import { Kafka } from 'kafkajs';
import { readFileSync } from 'node:fs';

process.env.KAFKAJS_NO_PARTITIONER_WARNING = 1;

const message = JSON.parse(readFileSync('./message.json'));

const type = avro.Type.forSchema({
  type: 'record',
  name: 'BankTransferPaymentCompletedEvent',
  namespace: 'nl.velocitymotors.carbooking.payment.avro',
  fields: [
    { name: 'paymentId', type: 'string' },
    { name: 'senderAccountNumber', type: 'string' },
    { name: 'paymentAmount', type: 'bytes' },
    { name: 'transactionDetails', type: 'string' },
  ],
});

const paymentAmount = Buffer.alloc(8);
paymentAmount.writeBigInt64BE(BigInt(Math.round(message.paymentAmount * 100)));

const value = type.toBuffer({ ...message, paymentAmount });

const producer = new Kafka({ brokers: ['localhost:9092'] }).producer();
await producer.connect();
await producer.send({ topic: 'bank-transfer-payment-events', messages: [{ value }] });
await producer.disconnect();

console.log('message sent:', message.transactionDetails);
