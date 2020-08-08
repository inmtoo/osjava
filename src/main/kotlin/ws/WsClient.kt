package ws

import org.java_websocket.WebSocket

class WsClient(val client: WebSocket, val id: Int) {
    var peerId: String = ""
}