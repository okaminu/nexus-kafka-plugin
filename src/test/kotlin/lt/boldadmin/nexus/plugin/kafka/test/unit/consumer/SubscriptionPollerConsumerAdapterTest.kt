package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorLocationConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorMessageConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.SubscriptionPollerConsumerAdapter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SubscriptionPollerConsumerAdapterTest {

    @MockK
    private lateinit var locationConsumerSpy: CollaboratorLocationConsumer

    @MockK
    private lateinit var messageConsumerSpy: CollaboratorMessageConsumer

    private lateinit var subscriptionPoller: SubscriptionPollerConsumerAdapter

    @BeforeEach
    fun setUp() {
        every { locationConsumerSpy.consumeCoordinates() } returns Unit
        every { locationConsumerSpy.consumerAbsent() } returns Unit
        every { messageConsumerSpy.consumeMessages() } returns Unit

        subscriptionPoller = object: SubscriptionPollerConsumerAdapter(locationConsumerSpy, messageConsumerSpy) {
            override fun runInThread(function: () -> Unit) { function() }
        }
    }

    @Test
    fun `Polls consumers for message events `() {
        subscriptionPoller.pollInNewThread()
        verify { messageConsumerSpy.consumeMessages() }
    }

    @Test
    fun `Polls consumers for coordinate update events `() {
        subscriptionPoller.pollInNewThread()
        verify { locationConsumerSpy.consumeCoordinates() }
    }

    @Test
    fun `Polls consumers for coordinate absence events `() {
        subscriptionPoller.pollInNewThread()
        verify { locationConsumerSpy.consumerAbsent() }
    }
}
