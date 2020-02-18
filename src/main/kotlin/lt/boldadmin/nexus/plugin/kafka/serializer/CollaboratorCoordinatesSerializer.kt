package lt.boldadmin.nexus.plugin.kafka.serializer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lt.boldadmin.nexus.api.type.valueobject.location.Coordinates
import org.apache.kafka.common.serialization.Serializer

class CollaboratorCoordinatesSerializer: Serializer<Pair<String, Coordinates>> {
    override fun serialize(topic: String?, collaboratorCoordinates: Pair<String, Coordinates>?): ByteArray =
        jacksonObjectMapper().writeValueAsBytes(collaboratorCoordinates)
}
