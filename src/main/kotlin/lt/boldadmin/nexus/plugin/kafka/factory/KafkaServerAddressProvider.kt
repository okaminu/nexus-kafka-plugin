package lt.boldadmin.nexus.plugin.kafka.factory

import lt.boldadmin.nexus.plugin.kafka.KafkaServerAddressNotSetException


class KafkaServerAddressProvider {
    val url get() = System.getenv("KAFKA_SERVER_URL") ?: throw KafkaServerAddressNotSetException
}