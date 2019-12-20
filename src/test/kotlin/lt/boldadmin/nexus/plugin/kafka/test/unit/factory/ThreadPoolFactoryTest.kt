package lt.boldadmin.nexus.plugin.kafka.test.unit.factory

import lt.boldadmin.nexus.plugin.kafka.factory.ThreadPoolFactory
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ThreadPoolFactoryTest {

    @Test
    fun `Creates thread pool`() {
        val threadPool = ThreadPoolFactory().create(3)

        assertNotNull(threadPool)
        threadPool.shutdown()
    }
}
