import org.json.JSONObject
import java.io.File
import java.sql.*

class Mysql(loadFromFile: Boolean = true) {

    private var user: String = "root"
    private var password: String = "root"
    private var dbName: String = "webinar"

    private var connection: Connection

    var affectedRows: Long = 0
    var insertedId: Long = 0
    lateinit var result: ResultSet


    init {
        val file = File("src/main/kotlin/files/db.json")
        if (loadFromFile && file.exists()) {
            val data = JSONObject(file.readText(Charsets.UTF_8))
            this.user = data.getString("user")
            this.password = data.getString("password")
            this.dbName = data.getString("db")
        }

        this.connection = DriverManager.getConnection(
            "jdbc:mysql://localhost/" + this.dbName + "?serverTimezone=UTC&useSSL=false",
            this.user,
            this.password
        )

        this.query("set global sql_mode=''")
    }

    fun query(sql: String, select: Boolean = false): Mysql {

        if (select) {
            val statement = this.connection.createStatement()
            this.result = statement.executeQuery(sql)
        } else {
            val statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            this.affectedRows = statement.executeUpdate().toLong()
            try {
                statement.generatedKeys.also {
                    it.next()
                    this.insertedId = it.getLong(1)
                }
            } catch (e: SQLException) {

            }

        }

        return this
    }

}