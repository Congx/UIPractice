package com.example.uipractice.net

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.base.net.RetrofitServer
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_websocket.*
import okhttp3.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer


class WebsocketActivity : AppCompatActivity() {

    private lateinit var mWebSocket: WebSocket
    var msgCount = 0
    var mockWebServer: MockWebServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_websocket)
        startServer.setOnClickListener {
            startServer()
        }

        startClient.setOnClickListener {
            startClient()
        }

        closeServer.setOnClickListener {
            closeServer()
        }

        sendMessage.setOnClickListener {
            sendMessage()
        }
    }

    fun startServer() {
        if (mockWebServer == null) {
            mockWebServer = MockWebServer()
        }
        mockWebServer?.enqueue(MockResponse().withWebSocketUpgrade(object :
            WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.e("Websocket","server onOpen")

                Log.e("Websocket","server request header: + ${response.request.headers}")
                Log.e("Websocket","server response header: + ${response.headers}")

                Log.e("Websocket","server response:$response")

            }

            override fun onMessage(webSocket: WebSocket, string: String) {
                Log.e("Websocket","message:$string")

                //接受到5条信息后，关闭消息定时发送器
                if (msgCount === 5) {
//                    mTimer.cancel()
                    webSocket.close(1000, "close by server")
                    return
                }
                Log.e("Websocket","server response-$string")

            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.e("Websocket","server onClosing")
                Log.e("Websocket","server code:$code reason:$reason")
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.e("Websocket","server onClosed")
                Log.e("Websocket","server code:$code reason:$reason")
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                //出现异常会进入此回调
                Log.e("Websocket","server onFailure")
                Log.e("Websocket","throwable:${t.message}")
                Log.e("Websocket","response:${response?.body.toString()}")

            }
        }))
    }

    fun closeServer() {
        mWebSocket?.close(1000,"close")

        mWebSocket?.cancel()
    }

    fun sendMessage() {
        mWebSocket.send("${msgCount++}")
    }

    fun startClient() {
//        val hostName = mockWebServer?.hostName
//        val port = mockWebServer?.port
//        Log.e("Websocket","hostName:$hostName")
//        Log.e("Websocket","port:$port")

//        var wsUrl = "ws://$hostName:$port/"
        var wsUrl = "ws://echo.websocket.org"

        var client = OkHttpClient()

        //构造request对象
        val request: Request = Request.Builder()
            .url(wsUrl)
            .build()

        //建立连接
        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mWebSocket = webSocket
                Log.e("Websocket","client onOpen")

                Log.e("Websocket","client request header: + ${response.request.headers}")
                Log.e("Websocket","client response header: + ${response.headers}")

                Log.e("Websocket","client response:$response")

            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.e("Websocket","server response-$text")

                Log.e("Websocket",Thread.currentThread().name)
            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.e("Websocket","client onClosing")
                Log.e("Websocket","client code:$code reason:$reason")
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.e("Websocket","client onClosed")
                Log.e("Websocket","client code:$code reason:$reason")
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                //出现异常会进入此回调
                Log.e("Websocket","client onFailure")
                Log.e("Websocket","throwable:${t.message}")
                Log.e("Websocket","response:${response?.body.toString()}")
            }
        })
    }

}
