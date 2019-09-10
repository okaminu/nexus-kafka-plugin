package lt.boldadmin.nexus.plugin.eventkafka.consumer

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration.ofSeconds
import java.util.*

open class Consumer {

    fun <T>consume(topic: String, function: (T) -> Unit, properties: Properties) {
        val consumer = KafkaConsumer<String, T>(properties)
        consumer.subscribe(listOf(topic))

        executeInfinitely { consumer.poll(ofSeconds(1)).forEach { function(it.value()) } }
    }

    open fun executeInfinitely(function: () -> Unit) {
        while (true) {
            function()
        }
    }
}