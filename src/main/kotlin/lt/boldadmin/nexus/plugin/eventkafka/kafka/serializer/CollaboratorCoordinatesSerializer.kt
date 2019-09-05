package lt.boldadmin.nexus.plugin.eventkafka.kafka.serializer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import org.apache.kafka.common.serialization.Serializer

class CollaboratorCoordinatesSerializer: Serializer<Pair<String, Coordinates>> {
    override fun serialize(topic: String?, collaboratorCoordinates: Pair<String, Coordinates>?): ByteArray =
        jacksonObjectMapper().writeValueAsBytes(collaboratorCoordinates)

    override fun close() {}

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
}