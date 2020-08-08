package auth

import Mysql

class Auth {

    companion object {
        fun authUser(token: String, email: String) {

            val mysql = Mysql()
            val result = mysql.query("SELECT * FROM `users`")

        }
    }

}