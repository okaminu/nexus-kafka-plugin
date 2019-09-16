package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.nexus.api.event.subscriber.CollaboratorCoordinatesSubscriber
import lt.boldadmin.nexus.api.event.subscriber.CollaboratorMessageSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorLocationConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.Consumer
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorMessageDeserializer
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

    @MockK
    private lateinit var messageSubscribersSpy: CollaboratorMessageSubscriber

    private lateinit var consumer: CollaboratorLocationConsumer

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        consumer = CollaboratorLocationConsumer(
            consumerPropertiesFactoryStub,
            consumerSpy,
            listOf(coordinatesSubscriberSpy, coordinatesSubscriberSpy),
            listOf(messageSubscribersSpy, messageSubscribersSpy)
        )
    }

    @Test
    fun `Notifies subscribers on collaborator location update by coordinates `() {
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

    @Test
    fun `Notifies subscribers on collaborator location update by message`() {
        val subscribedFunction = slot<(Message) -> Unit>()
        val message = Message("123", "333", "@project")
        every { consumerPropertiesFactoryStub.create(CollaboratorMessageDeserializer::class.java) } returns Properties()
        every { consumerSpy.consume(any(), capture(subscribedFunction), any()) } returns Unit
        every { messageSubscribersSpy.notify(message) } returns Unit

        consumer.consumeMessages()
        subscribedFunction.captured(message)

        verify(exactly = 2) { messageSubscribersSpy.notify(message) }
    }
}
