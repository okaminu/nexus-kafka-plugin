package lt.boldadmin.nexus.plugin.kafka.test.unit.factory

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import lt.boldadmin.nexus.plugin.kafka.factory.ConsumerPropertiesFactory
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaServerAddressProvider
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigInteger
import java.util.*

@ExtendWith(MockKExtension::class)
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