package com.impala.rdlms.utils

import android.content.Context
import android.location.Location
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class SocketManager private constructor() {
    private var socket: Socket? = null
    private var onConnectCallback: (() -> Unit)? = null

    init {
        initializeSocket()
    }

    companion object {
        private var instance: SocketManager? = null

        fun getInstance(): SocketManager {
            if (instance == null) {
                instance = SocketManager()
            }
            return instance as SocketManager
        }
    }

    private fun initializeSocket() {
        val options = IO.Options()
        options.reconnection = true

        try {
            socket = IO.socket("http://174.138.120.140:6044", options)
//            socket = IO.socket("http://172.16.16.53:6044", options)
            setupSocketListeners()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSocketListeners() {
        socket?.on(Socket.EVENT_CONNECT) {
            onConnectCallback?.invoke()
        }
        socket?.on(Socket.EVENT_DISCONNECT, onDisconnect)

        socket?.on(Socket.EVENT_CONNECT_ERROR, onError)
    }

    // Set a callback function to be called when the connection is established
    fun setOnConnectCallback(callback: () -> Unit) {
        onConnectCallback = callback
    }

    fun connect() {
        socket?.connect()

    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }

    fun sendSocketInfoFromPrefs(context: Context) {
        val socketId = socket?.id()
        val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val dataToSend = JSONObject()
        dataToSend.put("socket_id", socketId)
        dataToSend.put("user_id", sharedPreferences.getString("id", ""))
        socket?.emit("send_socket_info", dataToSend)
    }

    fun sendLocationViaSocket(context: Context, location: Location) {
        val jsonLocation = JSONObject().apply {
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("altitude", location.altitude)
            put("accuracy", location.accuracy)
            put("bearing", location.bearing)
            put("speed", location.speed)
        }
        val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val jsonUserDetails = JSONObject().apply {
            put("sap_id", sharedPreferences.getString("sap_id",""))
            put("full_name", sharedPreferences.getString("full_name", ""))
            put("user_type", sharedPreferences.getString("user_type", ""))
            put("mobile_number", sharedPreferences.getString("mobile_number", ""))
        }

        socket?.emit("coordinates_android", JSONObject().apply {
            put("location", jsonLocation)
            put("user_details", jsonUserDetails)
        })
    }

    fun sendMessage(message: String) {
        socket?.emit("message", message)
    }

    private val onConnect = Emitter.Listener {
        // Handle the connection event
        Log.d("socket","connected")
    }

    private val onDisconnect = Emitter.Listener {
        // Handle the disconnection event
        Log.d("socket","disconnected")
    }

    private val onError = Emitter.Listener {
        val error = it[0] as JSONObject
        // Handle the error event
    }

}


