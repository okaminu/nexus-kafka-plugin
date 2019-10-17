package lt.boldadmin.nexus.plugin.kafka.test.unit.event.publisher

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.kafka.event.publisher.CollaboratorCoordinatesPublisher
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaProducerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.ProducerPropertiesFactory
import lt.boldadmin.nexus.plugin.kafka.serializer.CollaboratorCoordinatesSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CollaboratorCoordinatesPublisherTest {

    @MockK
    private lateinit var propertiesFactoryStub: ProducerPropertiesFactory

    @MockK
    private lateinit var producerFactoryStub: KafkaProducerFactory

    private lateinit var publisher: CollaboratorCoordinatesPublisher

    @BeforeEach
    fun `Set up`() {
        publisher = CollaboratorCoordinatesPublisher(producerFactoryStub, propertiesFactoryStub)
    }

    @Test
    fun `Updates collaborator location by coordinates`() {
        val properties = Properties()
        val coordinates = Coordinates(123.0, 123.0)
        val producerSpy: KafkaProducer<String, Pair<String, Coordinates>> = mockk()
        every { producerFactoryStub.create<Pair<String, Coordinates>>(properties) } returns producerSpy
        every { propertiesFactoryStub.create(CollaboratorCoordinatesSerializer::class.java) } returns properties
        every { producerSpy.send(any()) } returns mockk()

        publisher.publish("collaboratorId", coordinates)

        verify {
            producerSpy.send(ProducerRecord("collaborator-coordinates-update", Pair("collaboratorId", coordinates)))
        }
    }

    @Test
    fun `Updates absent collaborator`() {
        val properties = Properties()
        val producerSpy: KafkaProducer<String, String> = mockk()
        every { producerFactoryStub.create<String>(properties) } returns producerSpy
        every { propertiesFactoryStub.create(StringSerializer::class.java) } returns properties
        every { producerSpy.send(any()) } returns mockk()

        publisher.publishAbsent("collaboratorId")

        verify {
            producerSpy.send(ProducerRecord("collaborator-coordinates-absent", "collaboratorId"))
        }
    }
}
