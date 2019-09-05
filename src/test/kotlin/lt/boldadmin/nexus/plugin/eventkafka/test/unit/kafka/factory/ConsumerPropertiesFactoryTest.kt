package lt.boldadmin.nexus.plugin.eventkafka.test.unit.kafka.factory

import io.mockk.every
import io.mockk.mockk
import lt.boldadmin.nexus.plugin.eventkafka.kafka.KafkaServerAddressProvider
import lt.boldadmin.nexus.plugin.eventkafka.kafka.factory.ConsumerPropertiesFactory
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.Test
import java.math.BigInteger
import java.util.*
import kotlin.test.assertEquals

class ConsumerPropertiesFactoryTest {

    @Test
    fun `Creates properties`() {
        val addressProviderSpy = mockk<KafkaServerAddressProvider>()
        every { addressProviderSpy.url } returns "url"
        val expectedProperties = Properties().apply {
            this["bootstrap.servers"] = "url"
            this["key.deserializer"] = StringDeserializer::class.java
            this["value.deserializer"] = BigInteger::class.java
            this["group.id"] = "consumer"
        }

        val actualProperties = ConsumerPropertiesFactory(addressProviderSpy).create(BigInteger::class.java)

        assertEquals(expectedProperties, actualProperties)
    }
}