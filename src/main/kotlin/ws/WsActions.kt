package ws

import kotlinx.serialization.Serializable


interface Action {
    val action: String
}

@Serializable
data class WebinarPeer(val peerId: String, override val action: String) : Action

@Serializable
data class StopWebinar(override val action: String) : Action

@Serializable
data class PeerId(val peerId: String, override val action: String) : Action

@Serializable
data class StartWebinar(override val action: String) : Action

@Serializable
data class Online(val online: Int, override val action: String) : Action

@Serializable
data class ConnectWebinar(val peerId: String, override val action: String) : Action