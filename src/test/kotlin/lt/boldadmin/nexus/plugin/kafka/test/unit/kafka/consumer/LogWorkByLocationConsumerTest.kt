package lt.boldadmin.nexus.plugin.kafka.test.unit.kafka.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import lt.boldadmin.nexus.api.service.worklog.status.location.WorklogLocationService
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.kafka.kafka.consumer.CollaboratorLocationConsumer
import lt.boldadmin.nexus.plugin.kafka.kafka.consumer.Consumer
import lt.boldadmin.nexus.plugin.kafka.kafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.kafka.kafka.factory.ConsumerPropertiesFactory
import org.junit.Before
import org.junit.Test
import java.util.*

class LogWorkByLocationConsumerTest {

    @MockK
    private lateinit var consumerPropertiesFactoryStub: ConsumerPropertiesFactory

    @MockK
    private lateinit var consumerSpy: Consumer

    @MockK
    private lateinit var locationServiceSpy: WorklogLocationService

    private lateinit var consumer: CollaboratorLocationConsumer

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        consumer = CollaboratorLocationConsumer(consumerPropertiesFactoryStub, consumerSpy, locationServiceSpy)
    }

    @Test
    fun `Logs work on collaborator location update `() {
        val subscribedFunction = slot<(Pair<String, Coordinates>) -> Unit>()
        val coordinates = Coordinates(1.0, 1.0)
        every { consumerPropertiesFactoryStub.create(CollaboratorCoordinatesDeserializer::class.java) } returns Properties()
        every { consumerSpy.consume(any(), capture(subscribedFunction), any()) } returns Unit
        every { locationServiceSpy.logWork("collabId", coordinates) } returns Unit

        consumer.consume()
        subscribedFunction.captured(Pair("collabId", coordinates))

        verify { locationServiceSpy.logWork("collabId", coordinates) }
    }

    @Test
    fun `Subscribes to collaborator location update`() {
        val properties = Properties()
        every { consumerPropertiesFactoryStub.create(CollaboratorCoordinatesDeserializer::class.java) } returns properties
        every { consumerSpy.consume(any(), any<(Pair<String, Coordinates>) -> Unit>(), any()) } returns Unit
        every { locationServiceSpy.logWork("collabId", any()) } returns Unit

        consumer.consume()

        verify { consumerSpy.consume<Pair<String, Coordinates>>("collaborator-location-update", any(), properties) }
    }
}