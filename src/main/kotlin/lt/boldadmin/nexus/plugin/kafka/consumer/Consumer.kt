package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.plugin.kafka.factory.KafkaConsumerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.LoggerFactory
import java.time.Duration.ofSeconds
import java.util.*

open class Consumer(
    private val consumerFactory: KafkaConsumerFactory,
    private val loggerFactory: LoggerFactory
) {

    fun <T> consume(topic: String, function: (T) -> Unit, properties: Properties) {
        val consumer = consumerFactory.create<T>(properties)
        consumer.subscribe(listOf(topic))

        executeInfinitely {
            consumer.poll(ofSeconds(1)).forEach {
                logException({ function(it.value()) }, topic)
            }
        }
    }

    open fun executeInfinitely(function: () -> Unit) {
        while (true) {
            function()
        }
    }

    private fun logException(function: () -> Unit, topic: String) {
        try {
            function()
        } catch (ex: Exception) {
            loggerFactory.create(Consumer::class.java).error(
                "Subscriber encountered exception on event $topic. ${ex.message}",
                ex
            )
        }
    }
}
