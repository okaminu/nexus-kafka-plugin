package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.nexus.api.event.subscriber.CollaboratorMessageSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorMessageConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.Consumer
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorMessageDeserializer
import lt.boldadmin.nexus.plugin.kafka.factory.ConsumerPropertiesFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CollaboratorMessageConsumerTest {

    @MockK
    private lateinit var consumerPropertiesFactoryStub: ConsumerPropertiesFactory

    @MockK
    private lateinit var consumerSpy: Consumer

    @MockK
    private lateinit var messageSubscribersSpy: CollaboratorMessageSubscriber

    private lateinit var consumer: CollaboratorMessageConsumer

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        consumer = CollaboratorMessageConsumer(
            consumerPropertiesFactoryStub,
            consumerSpy,
            listOf(messageSubscribersSpy, messageSubscribersSpy)
        )
    }

    @Test
    fun `Notifies subscribers on collaborator message event`() {
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
