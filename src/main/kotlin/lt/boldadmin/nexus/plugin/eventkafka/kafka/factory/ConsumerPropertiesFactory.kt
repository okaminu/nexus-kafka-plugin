package lt.boldadmin.nexus.plugin.eventkafka.kafka.factory

import lt.boldadmin.nexus.plugin.eventkafka.kafka.KafkaServerAddressProvider
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*

class ConsumerPropertiesFactory(private val addressProvider: KafkaServerAddressProvider) {
    fun <T>create(valueDeserializerClass: Class<T>) = Properties().apply {
        this["bootstrap.servers"] = addressProvider.url
        this["key.deserializer"] = StringDeserializer::class.java
        this["value.deserializer"] = valueDeserializerClass
        this["group.id"] = "consumer"
    }
}