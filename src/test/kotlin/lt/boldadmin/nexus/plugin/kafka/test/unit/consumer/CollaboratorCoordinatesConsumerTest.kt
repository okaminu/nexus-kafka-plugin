package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.nexus.api.event.subscriber.CollaboratorCoordinatesSubscriber
import lt.boldadmin.nexus.api.type.valueobject.location.Coordinates
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorCoordinatesConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.Consumer
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.kafka.factory.ConsumerPropertiesFactory
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CollaboratorCoordinatesConsumerTest {

    @MockK
    private lateinit var consumerPropertiesFactoryStub: ConsumerPropertiesFactory

    @MockK
    private lateinit var consumerSpy: Consumer

    @MockK
    private lateinit var coordinatesSubscriberSpy: CollaboratorCoordinatesSubscriber

    private lateinit var consumer: CollaboratorCoordinatesConsumer

    @BeforeEach
    fun `Set up`() {
        consumer = CollaboratorCoordinatesConsumer(
            consumerPropertiesFactoryStub,
            consumerSpy,
            listOf(coordinatesSubscriberSpy, coordinatesSubscriberSpy)
        )
    }

    @Test
    fun `Notifies subscribers on collaborator coordinates update`() {
        val coordinates = Coordinates(1.0, 1.0)
        val subscribedFunctions = slot<Collection<(Pair<String, Coordinates>) -> Unit>>()
        every { consumerSpy.consume("collaborator-coordinates-update", capture(subscribedFunctions), any()) } just Runs
        every { coordinatesSubscriberSpy.notify("collabId", coordinates) } just Runs
        every {
            consumerPropertiesFactoryStub.create(CollaboratorCoordinatesDeserializer::class.java)
        } returns Properties()

        consumer.consumeCoordinates()
        subscribedFunctions.captured.forEach { it(Pair("collabId", coordinates)) }

        verify(exactly = 2) { coordinatesSubscriberSpy.notify("collabId", coordinates) }
    }

    @Test
    fun `Notifies subscribers on collaborator coordinates absent`() {
        val subscribedFunctions = slot<Collection<(String) -> Unit>>()
        val collaboratorId = "123456"
        every {
            consumerSpy.consume("collaborator-coordinates-absent", capture(subscribedFunctions), any())
        } just Runs
        every { coordinatesSubscriberSpy.notifyAbsent(collaboratorId) } just Runs
        every {
            consumerPropertiesFactoryStub.create(StringDeserializer::class.java)
        } returns Properties()

        consumer.consumeAbsent()
        subscribedFunctions.captured.forEach { it(collaboratorId) }

        verify(exactly = 2) { coordinatesSubscriberSpy.notifyAbsent(collaboratorId) }
    }

}
