package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.nexus.api.event.subscriber.CollaboratorCoordinatesSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorLocationConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.Consumer
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.kafka.factory.ConsumerPropertiesFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CollaboratorLocationConsumerTest {

    @MockK
    private lateinit var consumerPropertiesFactoryStub: ConsumerPropertiesFactory

    @MockK
    private lateinit var consumerSpy: Consumer

    @MockK
    private lateinit var coordinatesSubscriberSpy: CollaboratorCoordinatesSubscriber

    private lateinit var consumer: CollaboratorLocationConsumer

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        consumer = CollaboratorLocationConsumer(
            consumerPropertiesFactoryStub,
            consumerSpy,
            listOf(coordinatesSubscriberSpy, coordinatesSubscriberSpy)
        )
    }

    @Test
    fun `Notifies subscribers on collaborator coordinates update `() {
        val subscribedFunction = slot<(Pair<String, Coordinates>) -> Unit>()
        val coordinates = Coordinates(1.0, 1.0)
        val collaboratorId = "123456"
        every { consumerSpy.consume(any(), capture(subscribedFunction), any()) } returns Unit
        every { coordinatesSubscriberSpy.notify(collaboratorId, coordinates) } returns Unit
        every {
            consumerPropertiesFactoryStub.create(CollaboratorCoordinatesDeserializer::class.java)
        } returns Properties()

        consumer.consumeCoordinates()
        subscribedFunction.captured(Pair(collaboratorId, coordinates))

        verify(exactly = 2) { coordinatesSubscriberSpy.notify(collaboratorId, coordinates) }
    }
}
