package com.tavanhieu.socketio

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {
    lateinit var msocket: Socket

    @Synchronized
    fun setSocket(){
        try {
            msocket = IO.socket("http://192.168.1.6:3000")
        } catch (e: URISyntaxException) { }
    }
    @Synchronized
    fun getSocket(): Socket {
        return msocket
    }
}