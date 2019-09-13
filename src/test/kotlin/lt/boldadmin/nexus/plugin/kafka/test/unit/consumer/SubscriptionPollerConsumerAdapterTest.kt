package lt.boldadmin.nexus.plugin.kafka.test.unit.consumer

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import lt.boldadmin.nexus.plugin.kafka.consumer.CollaboratorLocationConsumer
import lt.boldadmin.nexus.plugin.kafka.consumer.SubscriptionPollerConsumerAdapter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SubscriptionPollerConsumerAdapterTest {

    @Test
    fun `Notifies subscribers on collaborator location update by coordinates `() {
        val collaboratorLocationConsumerSpy: CollaboratorLocationConsumer = mockk()
        every { collaboratorLocationConsumerSpy.consumeCoordinates() } returns Unit
        every { collaboratorLocationConsumerSpy.consumeMessages() } returns Unit
        val subscriptionPoller = SubscriptionPollerConsumerAdapter(collaboratorLocationConsumerSpy)

        subscriptionPoller.pollInNewThread()

        verify { collaboratorLocationConsumerSpy.consumeCoordinates() }
        verify { collaboratorLocationConsumerSpy.consumeMessages() }
    }
}