package lt.boldadmin.nexus.plugin.eventkafka.kafka.factory

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*

object KafkaConsumerFactory {
    fun <T>create(properties: Properties) = KafkaConsumer<String, T>(properties)
}