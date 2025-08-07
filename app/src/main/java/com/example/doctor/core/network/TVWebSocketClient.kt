package com.example.doctor.core.network

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI

class TVWebSocketClient(
    serverUri: URI,
    val onDataReceived: (String) -> Unit,
    val onConnectionEstablished: (() -> Unit)? = null
) : WebSocketClient(serverUri) {
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("TVWebSocketClient", "Conexión abierta con el servidor WebSocket")
        Log.d("TVWebSocketClient", "Listo para recibir mensajes...")
        onConnectionEstablished?.invoke()
    }
    override fun onMessage(message: String) {
        Log.d("TVWebSocketClient", "Mensaje recibido: $message")
        Log.d("TVWebSocketClient", "Longitud del mensaje: ${message.length} caracteres")
        // Enviar el mensaje como String para que el ViewModel lo procese
        onDataReceived(message)
    }
    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("TVWebSocketClient", "Conexión cerrada: código=$code, razón=$reason, remoto=$remote")
    }
    override fun onError(ex: Exception?) {
        Log.e("TVWebSocketClient", "Error en WebSocket", ex)
    }
} 