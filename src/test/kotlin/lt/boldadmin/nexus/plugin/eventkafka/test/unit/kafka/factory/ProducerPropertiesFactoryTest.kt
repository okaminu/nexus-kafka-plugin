package lt.boldadmin.nexus.plugin.eventkafka.test.unit.kafka.factory

import io.mockk.every
import io.mockk.mockk
import lt.boldadmin.nexus.plugin.eventkafka.kafka.KafkaServerAddressProvider
import lt.boldadmin.nexus.plugin.eventkafka.kafka.factory.ProducerPropertiesFactory
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.Test
import java.math.BigInteger
import java.util.*
import kotlin.test.assertEquals

class ProducerPropertiesFactoryTest {

    @Test
    fun `Creates properties`() {
        val addressProviderSpy = mockk<KafkaServerAddressProvider>()
        every { addressProviderSpy.url } returns "url"
        val expectedProperties = Properties().apply {
            this["bootstrap.servers"] = "url"
            this["key.serializer"] = StringSerializer::class.java
            this["value.serializer"] = BigInteger::class.java
        }

        val actualProperties = ProducerPropertiesFactory(
            addressProviderSpy
        ).create(BigInteger::class.java)

        assertEquals(expectedProperties, actualProperties)
    }
}