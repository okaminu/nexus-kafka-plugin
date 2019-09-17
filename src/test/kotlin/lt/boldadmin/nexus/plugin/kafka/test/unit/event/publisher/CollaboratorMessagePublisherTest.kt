package lt.boldadmin.nexus.plugin.kafka.test.unit.event.publisher

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.kafka.event.publisher.CollaboratorMessagePublisher
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaProducerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.ProducerPropertiesFactory
import lt.boldadmin.nexus.plugin.kafka.serializer.CollaboratorMessageSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CollaboratorMessagePublisherTest {

    @MockK
    private lateinit var producerPropertiesFactory: ProducerPropertiesFactory

    @MockK
    private lateinit var factorySpy: KafkaProducerFactory

    @Test
    fun `Publishes collaborator message`() {
        val properties = Properties()
        val producerSpy: KafkaProducer<String, Message> = mockk()
        val message = Message("123", "86099999", "projectName")
        every { factorySpy.create<Message>(properties) } returns producerSpy
        every { producerPropertiesFactory.create(CollaboratorMessageSerializer::class.java) } returns properties
        every { producerSpy.send(any()) } returns mockk()

        CollaboratorMessagePublisher(
            factorySpy,
            producerPropertiesFactory
        ).publish(message)

        verify { producerSpy.send(eq(ProducerRecord("collaborator-location-update-by-message", message))) }
    }
}
