package lt.boldadmin.nexus.plugin.kafka.test.unit.event.publisher

import io.mockk.*
import io.mockk.impl.annotations.MockK
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaProducerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.ProducerPropertiesFactory
import lt.boldadmin.nexus.plugin.kafka.serializer.CollaboratorCoordinatesSerializer
import lt.boldadmin.nexus.plugin.kafka.serializer.MessageSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.Before
import org.junit.Test
import java.util.*

class CollaboratorLocationPublisherAdapterTest {

    @MockK
    private lateinit var producerPropertiesFactory: ProducerPropertiesFactory

    @MockK
    private lateinit var factorySpy: KafkaProducerFactory

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Updates collaborator location by coordinates`() {
        val properties = Properties()
        val collaboratorId = "collaboratorId"
        val coordinates = Coordinates(123.0, 123.0)
        val producerSpy: KafkaProducer<String, Pair<String, Coordinates>> = mockk()
        every { factorySpy.create<Pair<String, Coordinates>>(properties) } returns producerSpy
        every { producerPropertiesFactory.create(CollaboratorCoordinatesSerializer::class.java) } returns properties
        every { producerSpy.send(any()) } returns mockk()

        CollaboratorCoordinatesProducer(
            factorySpy,
            producerPropertiesFactory
        ).publish(collaboratorId, coordinates)

        val collaboratorCoordinates = Pair(collaboratorId, coordinates)
        verify {
            producerSpy.send(eq(ProducerRecord("collaborator-location-update-by-coordinates", collaboratorCoordinates)))
        }
    }

    @Test
    fun `Updates collaborator location by message`() {
        val properties = Properties()
        val producerSpy: KafkaProducer<String, Message> = mockk()
        val message = Message("123", "86099999", "projectName")
        every { factorySpy.create<Message>(properties) } returns producerSpy
        every { producerPropertiesFactory.create(MessageSerializer::class.java) } returns properties
        every { producerSpy.send(any()) } returns mockk()

        CollaboratorCoordinatesProducer(
            factorySpy,
            producerPropertiesFactory
        ).publish(message)

        verify { producerSpy.send(eq(ProducerRecord("collaborator-location-update-by-message", message))) }
    }
}