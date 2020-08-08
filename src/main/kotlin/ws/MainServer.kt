package ws

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.json.JSONObject
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class MainServer(address: InetSocketAddress) : WebSocketServer(address) {

    private var clients: MutableList<WsClient> = mutableListOf()
    private var webinarWsId: Int? = null


    private fun getBesideId(id: Int): MutableList<WsClient> {
        return this.clients.filter { el -> el.id != id }.toMutableList()
    }

    /**
     * return index
     */
    private fun getClientIndexById(id: Int): Int? {
        val index = this.clients.indexOfFirst { it.id == id }
        if (index == -1) return null

        return index
    }

    private fun getClientById(id: Int): WsClient? {
        return this.clients.find { it.id == id }
    }

    private fun stopWebinar() {
        val id = this.webinarWsId!!
        this.webinarWsId = null
        val json = Json(JsonConfiguration.Stable)
        val jsonData = json.stringify(StopWebinar.serializer(), StopWebinar("stopWebinar")).toByteArray()
        this.getBesideId(id).forEach { it.client.send(jsonData) }
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        if (conn == null) return

        val id = if (this.clients.count() > 0) (this.clients[this.clients.count() - 1].id + 1) else 1
        this.clients.add(WsClient(conn, id))
        conn.setAttachment(id)

        if (this.webinarWsId != null) {
            val mainClient = this.clients[this.getClientIndexById(this.webinarWsId!!)!!]

            val json = Json(JsonConfiguration.Stable)
            val jsonData =
                json.stringify(WebinarPeer.serializer(), WebinarPeer(mainClient.peerId, "webinarPeer")).toByteArray()
            conn.send(jsonData)

//            jsonData = json.stringify(Online.serializer(), Online(this.clients.count(), "online"))
//            mainClient.client.send(jsonData)
        }

        println("New incoming connection $id")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        val id: Int = conn?.getAttachment() ?: return
        if (id == this.webinarWsId) this.stopWebinar()

        val index = this.getClientIndexById(id) ?: return
        this.clients.removeAt(index)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {

    }

    override fun onMessage(conn: WebSocket?, m: ByteBuffer?) {
        if (conn == null || m == null) return

        val message = m.array().toString(Charsets.UTF_8)

        val jsonObject = JSONObject(message)
        if (!jsonObject.has("action")) return

        val clientIndex = this.getClientIndexById(conn.getAttachment()) ?: return

        val action = jsonObject.getString("action")!!

        if (action == "peerId" && jsonObject.has("peerId")) {
            this.clients[clientIndex].peerId = jsonObject.getString("peerId")

            println("Peer Id: " + this.clients[clientIndex].peerId)
            return
        }

        if (action == "startWebinar") {
            val id: Int = conn.getAttachment()
            val client = this.getClientById(id) ?: return
            this.webinarWsId = client.id

            println("Webinar started")
        }

        if (action == "connectWebinar") {
            if (this.clients[clientIndex].peerId.isEmpty() || this.webinarWsId == null) return

            val peerId = this.clients[clientIndex].peerId
            if (peerId.isEmpty()) return

            val json = Json(JsonConfiguration.Stable)
            val jsonData =
                json.stringify(ConnectWebinar.serializer(), ConnectWebinar(peerId, "connectWebinar")).toByteArray()
            this.getClientById(this.webinarWsId!!)!!.client.send(jsonData)

            println("Connection to webinar")
        }

    }

    override fun onStart() {
        println("WebSocket server started")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
    }


}