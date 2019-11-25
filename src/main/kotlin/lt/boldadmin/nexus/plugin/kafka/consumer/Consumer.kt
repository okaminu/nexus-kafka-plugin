package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.plugin.kafka.factory.KafkaConsumerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.LoggerFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import java.time.Duration.ofSeconds
import java.util.*

open class Consumer(
    private val consumerFactory: KafkaConsumerFactory,
    private val loggerFactory: LoggerFactory
) {

    fun <T> consume(topic: String, functions: Collection<(T) -> Unit>, properties: Properties) {
        val consumer = consumerFactory.create<T>(properties)
        consumer.subscribe(listOf(topic))

        executeInfinitely {
            consumer.poll(ofSeconds(1)).forEach { consume<T>(topic, functions, it) }
        }
    }

    private fun <T> consume(topic: String, functions: Collection<(T) -> Unit>, record: ConsumerRecord<String, T>) {
        functions.forEach { function ->
            try {
                function(record.value())
            } catch (ex: Exception) {
                loggerFactory
                    .create(Consumer::class.java)
                    .error("Subscriber encountered exception on event $topic. ${ex.message}", ex)
            }
        }
    }

    open fun executeInfinitely(function: () -> Unit) {
        while (true) function()
    }

}
