package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorCoordinatesConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorMessageConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.SubscriptionPollerConsumerAdapter
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.ExecutorService

@ExtendWith(MockKExtension::class)
class SubscriptionPollerConsumerAdapterTest {

    @MockK
    private lateinit var coordinatesConsumerSpy: CollaboratorCoordinatesConsumer

    @MockK
    private lateinit var messageConsumerSpy: CollaboratorMessageConsumer

    private lateinit var subscriptionPoller: SubscriptionPollerConsumerAdapter

    @BeforeEach
    fun `Set Up`() {
        every { coordinatesConsumerSpy.consumeCoordinates() } just Runs
        every { coordinatesConsumerSpy.consumeAbsent() } just Runs
        every { messageConsumerSpy.consumeMessages() } just Runs

        subscriptionPoller = object: SubscriptionPollerConsumerAdapter(coordinatesConsumerSpy, messageConsumerSpy) {
            override fun create(): ExecutorService {
                val slot = slot<Runnable>()
                return mockk<ExecutorService>().apply {
                    every { submit(capture(slot)) } answers {
                        slot.captured.run()
                        mockk()
                    }
                }
            }
        }
    }

    @Test
    fun `Polls consumers for message events`() {
        subscriptionPoller.pollInNewThread()
        verify { messageConsumerSpy.consumeMessages() }
    }

    @Test
    fun `Polls consumers for coordinate update events`() {
        subscriptionPoller.pollInNewThread()
        verify { coordinatesConsumerSpy.consumeCoordinates() }
    }

    @Test
    fun `Polls consumers for coordinate absence events`() {
        subscriptionPoller.pollInNewThread()
        verify { coordinatesConsumerSpy.consumeAbsent() }
    }

    @Test
    fun `Creates Executor Service`() {
        val poller = SubscriptionPollerConsumerAdapter(coordinatesConsumerSpy, messageConsumerSpy)

        val executorService = poller.create()

        assertNotNull(executorService)
    }
}
