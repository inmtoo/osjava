import ws.MainServer
import java.net.InetSocketAddress

class Main {

    companion object {
        const val IP_ADDRESS = "127.0.0.1"
        const val PORT = 8080

        @JvmStatic
        fun main(args: Array<String>) {
            Main()
        }

    }

    private val server = MainServer(InetSocketAddress(IP_ADDRESS, PORT))

    init {
//        Mysql()
        this.server.start()

    }

}