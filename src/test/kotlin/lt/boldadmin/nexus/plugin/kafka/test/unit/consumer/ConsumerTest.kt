package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import lt.boldadmin.nexus.plugin.kafka.consumer.Consumer
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaConsumerFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.*

@ExtendWith(MockKExtension::class)
class ConsumerTest {

    @MockK
    private lateinit var consumerFactoryStub: KafkaConsumerFactory

    @MockK
    private lateinit var kafkaConsumerSpy: KafkaConsumer<String, String>

    private lateinit var consumer: Consumer

    @BeforeEach
    fun setUp() {
        consumer = object: Consumer(consumerFactoryStub) {
            override fun executeInfinitely(function: () -> Unit) {
                function()
            }
        }
        every { kafkaConsumerSpy.subscribe(any<Collection<String>>()) } returns Unit
    }

    @Test
    fun `Subscribes to topic with properties`() {
        val properties = Properties()
        every { consumerFactoryStub.create<String>(properties) } returns kafkaConsumerSpy
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(emptyList())

        consumer.consume<String>("topic", {}, properties)

        verify { kafkaConsumerSpy.subscribe(listOf("topic")) }
    }

    @Test
    fun `Polls for events each second`() {
        every { consumerFactoryStub.create<String>(any()) } returns kafkaConsumerSpy
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(emptyList())

        consumer.consume<String>("topic", {}, Properties())

        verify { kafkaConsumerSpy.poll(ofSeconds(1)) }
    }

    @Test
    fun `Executes subscription function with event data`() {
        val actualValues = mutableListOf<String>()
        val expectedValues = listOf("hello1", "hello2")
        every { consumerFactoryStub.create<String>(any()) } returns kafkaConsumerSpy
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(expectedValues)

        consumer.consume<String>("topic", { actualValues.add(it)}, Properties())

        assertEquals(expectedValues, actualValues)
    }

    @Test
    fun `Executes polling infinitely`() {
        every { consumerFactoryStub.create<String>(any()) } returns kafkaConsumerSpy
        every { kafkaConsumerSpy.poll(any<Duration>()) } returns createConsumerRecords(emptyList())

        Thread { run { Consumer(consumerFactoryStub).consume<String>("topic", {}, Properties()) } }
            .apply {
                start()
                join(100)
            }

        verify(atLeast = 3) { kafkaConsumerSpy.poll(ofSeconds(1)) }
    }

    private fun createConsumerRecords(values : Collection<String>): ConsumerRecords<String, String> {
        val records = values.map { ConsumerRecord("", 0, 0, "", it) }.toList()
        return ConsumerRecords<String, String>(mutableMapOf(TopicPartition("", 0) to records))
    }
}