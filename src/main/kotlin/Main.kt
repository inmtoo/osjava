import ws.MainServer
import java.net.InetSocketAddress

class Main {

    companion object {
        const val IP_ADDRESS = "192.168.1.50"
        const val PORT = 8080

        @JvmStatic
        fun main(args: Array<String>) {
            Main()
        }

    }

    private val server = MainServer(InetSocketAddress(IP_ADDRESS, PORT))

    init {
        Mysql()
        this@Main.server.start()

    }

}