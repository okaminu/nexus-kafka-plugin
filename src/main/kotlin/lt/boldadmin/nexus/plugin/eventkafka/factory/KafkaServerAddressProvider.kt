package lt.boldadmin.nexus.plugin.eventkafka.factory

import lt.boldadmin.nexus.plugin.eventkafka.KafkaServerAddressNotSetException


class KafkaServerAddressProvider {
    val url get() = System.getenv("KAFKA_SERVER_URL") ?: throw KafkaServerAddressNotSetException
}