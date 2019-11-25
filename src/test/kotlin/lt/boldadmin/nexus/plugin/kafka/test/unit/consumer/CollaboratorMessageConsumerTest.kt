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
    fun `Set up`() {
        consumer = CollaboratorMessageConsumer(
            consumerPropertiesFactoryStub,
            consumerSpy,
            listOf(messageSubscribersSpy, messageSubscribersSpy)
        )
    }

    @Test
    fun `Notifies subscribers on collaborator message event`() {
        val expectedMessage = Message("123", "333", "@project")
        val subscribedFunctions = slot<Collection<(Message) -> Unit>>()
        every { consumerPropertiesFactoryStub.create(CollaboratorMessageDeserializer::class.java) } returns Properties()
        every { consumerSpy.consume("collaborator-message", capture(subscribedFunctions), any()) } just Runs
        every { messageSubscribersSpy.notify(expectedMessage) } just Runs

        consumer.consumeMessages()
        subscribedFunctions.captured.forEach { it(expectedMessage) }

        verify(exactly = 2) { messageSubscribersSpy.notify(expectedMessage) }
    }
}
