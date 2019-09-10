package lt.boldadmin.nexus.plugin.eventkafka.factory

import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class ProducerPropertiesFactory(private val addressProvider: KafkaServerAddressProvider) {
    fun <T>create(valueSerializerClass: Class<T>) = Properties().apply {
        this["bootstrap.servers"] = addressProvider.url
        this["key.serializer"] = StringSerializer::class.java
        this["value.serializer"] = valueSerializerClass
    }
}