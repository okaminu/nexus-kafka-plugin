package lt.boldadmin.nexus.plugin.kafka.serializer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lt.boldadmin.nexus.api.type.valueobject.Message
import org.apache.kafka.common.serialization.Serializer

class CollaboratorMessageSerializer: Serializer<Message> {
    override fun serialize(topic: String?, message: Message?): ByteArray =
        jacksonObjectMapper().writeValueAsBytes(message)
}
