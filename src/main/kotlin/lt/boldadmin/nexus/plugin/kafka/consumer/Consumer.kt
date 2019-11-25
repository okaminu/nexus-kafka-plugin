package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.plugin.kafka.factory.KafkaConsumerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.LoggerFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import java.time.Duration.ofSeconds
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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

    protected open fun executeInfinitely(function: () -> Unit) {
        while (true) function()
    }

    protected open fun create(): ExecutorService = Executors.newFixedThreadPool(8)

    private fun <T> consume(topic: String, functions: Collection<(T) -> Unit>, record: ConsumerRecord<String, T>) {
        create().apply {
            functions.forEach {
                submit { executeWithExceptionLogging({ it(record.value()) }, topic) }
            }
        }
    }

    private fun executeWithExceptionLogging(function: () -> Unit, topic: String) {
        try {
            function()
        } catch (ex: Exception) {
            loggerFactory
                .create(Consumer::class.java)
                .error("Subscriber encountered exception on event $topic. ${ex.message}", ex)
        }
    }
}
