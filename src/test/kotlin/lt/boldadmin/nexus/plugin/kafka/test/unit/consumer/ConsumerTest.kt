package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.nexus.plugin.kafka.consumer.Consumer
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaConsumerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.LoggerFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.*

@ExtendWith(MockKExtension::class)
class ConsumerTest {

    @MockK
    private lateinit var consumerFactoryStub: KafkaConsumerFactory

    @MockK
    private lateinit var loggerFactorySpy: LoggerFactory

    @MockK
    private lateinit var kafkaConsumerSpy: KafkaConsumer<String, String>

    private lateinit var consumer: Consumer

    @BeforeEach
    fun `Set up`() {
        consumer = object: Consumer(consumerFactoryStub, loggerFactorySpy) {
            override fun executeInfinitely(function: () -> Unit) {
                function()
            }
        }
        every { kafkaConsumerSpy.subscribe(any<Collection<String>>()) } just Runs
        every { consumerFactoryStub.create<String>(any()) } returns kafkaConsumerSpy
    }

    @Test
    fun `Subscribes to topic with properties`() {
        val properties = Properties()
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(emptyList())

        consumer.consume<String>("topic", {}, properties)

        verify { kafkaConsumerSpy.subscribe(listOf("topic")) }
    }

    @Test
    fun `Polls for events each second`() {
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(emptyList())

        consumer.consume<String>("topic", {}, Properties())

        verify { kafkaConsumerSpy.poll(ofSeconds(1)) }
    }

    @Test
    fun `Executes subscription function with event data`() {
        val actualValues = mutableListOf<String>()
        val expectedValues = listOf("hello1", "hello2")
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(expectedValues)

        consumer.consume<String>("topic", { actualValues.add(it) }, Properties())

        assertEquals(expectedValues, actualValues)
    }

    @Test
    fun `Executes polling infinitely`() {
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(emptyList())

        Thread { run { Consumer(consumerFactoryStub, loggerFactorySpy).consume<String>("topic", {}, Properties()) } }
            .apply {
                start()
                join(100)
            }

        verify(atLeast = 3) { kafkaConsumerSpy.poll(ofSeconds(1)) }
    }

    @Test
    fun `Logs exception and it's message`() {
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(listOf("data"))
        val loggerSpy: Logger = mockk()
        every { loggerSpy.error(any(), any<Exception>()) } returns Unit
        every { loggerFactorySpy.create(Consumer::class.java) } returns loggerSpy

        consumer.consume<String>("topic", { throw Exception("message") }, Properties())

        val capturedArgs = mutableListOf<String>()
        verify { loggerSpy.error(capture(capturedArgs), any<Exception>()) }
        assertTrue(capturedArgs.single().contains("message"))
        assertTrue(capturedArgs.single().contains("topic"))
    }

    private fun createConsumerRecords(values : Collection<String>): ConsumerRecords<String, String> {
        val records = values.map { ConsumerRecord("", 0, 0, "", it) }.toList()
        return ConsumerRecords(mutableMapOf(TopicPartition("", 0) to records))
    }
}
