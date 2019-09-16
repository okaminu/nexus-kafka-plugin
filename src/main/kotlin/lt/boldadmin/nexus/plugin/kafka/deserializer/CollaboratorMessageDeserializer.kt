package lt.boldadmin.nexus.plugin.kafka.deserializer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lt.boldadmin.nexus.api.type.valueobject.Message
import org.apache.kafka.common.serialization.Deserializer

class CollaboratorMessageDeserializer: Deserializer<Message> {
    override fun deserialize(topic: String?, data: ByteArray?): Message =
        jacksonObjectMapper().readValue(data, Message::class.java)
}
