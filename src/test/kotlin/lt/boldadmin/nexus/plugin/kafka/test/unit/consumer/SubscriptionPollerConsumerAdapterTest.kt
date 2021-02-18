package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorCoordinatesConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorMessageConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.SubscriptionPollerConsumerAdapter
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

    @MockK
    private lateinit var executorServiceStub: ExecutorService

    private lateinit var subscriptionPoller: SubscriptionPollerConsumerAdapter

    @BeforeEach
    fun `Set Up`() {
        subscriptionPoller = SubscriptionPollerConsumerAdapter(
            coordinatesConsumerSpy,
            messageConsumerSpy,
            executorServiceStub
        )

        every { coordinatesConsumerSpy.consumeCoordinates() } just Runs
        every { coordinatesConsumerSpy.consumeAbsent() } just Runs
        every { messageConsumerSpy.consumeMessages() } just Runs

        val slot = slot<Runnable>()
        every { executorServiceStub.submit(capture(slot)) } answers {
            slot.captured.run()
            mockk()
        }
    }

    @Test
    fun `Polls consumers for message events`() {
        subscriptionPoller.poll()
        verify { messageConsumerSpy.consumeMessages() }
    }

    @Test
    fun `Polls consumers for coordinate update events`() {
        subscriptionPoller.poll()
        verify { coordinatesConsumerSpy.consumeCoordinates() }
    }

    @Test
    fun `Polls consumers for coordinate absence events`() {
        subscriptionPoller.poll()
        verify { coordinatesConsumerSpy.consumeAbsent() }
    }
}
