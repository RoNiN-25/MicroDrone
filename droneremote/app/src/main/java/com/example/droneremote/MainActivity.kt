package com.example.droneremote

import android.graphics.Color import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*

class MainActivity : AppCompatActivity() {

    val SERVER_IP = "http://192.168.4.1:81"
    val client = OkHttpClient()
    var request: Request? = null
    val listener = WebSocketListen()
    var ws:WebSocket? = null
    var connected = 0
    var angle1 = 0
    var angle2 = 0
    var str1 = 0
    var str2 = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val joystick1 = j1 as JoystickView
        joystick1.isAutoReCenterButton = false
        joystick1.setOnMoveListener(object : JoystickView.OnMoveListener
        {
            override fun onMove(angle: Int, strength: Int)
            {
                angle1 = angle
                str1 = strength
                sendData()
            }
        })

        val joystick2 = j2 as JoystickView
        joystick2.setOnMoveListener(object : JoystickView.OnMoveListener
        {
            override fun onMove(angle: Int, strength: Int)
            {
                angle2 = angle
                str2 = strength
                sendData()
            }
        }
        )
    }




    fun sendData()
    {
        ws?.send("$angle1;$str1;$angle2;$str2;")
    }


    inner class WebSocketListen:WebSocketListener()
    {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send("Connecting....")
            super.onOpen(webSocket, response)
        }

    }


    fun connectServer(view:View)
    {
        if(connected == 0)
        {
            request = Request.Builder().url(SERVER_IP).build()
            ws = client.newWebSocket(request!!, listener)
            buConnect.text = "Disconnect"
            buConnect.setBackgroundColor(Color.RED)
            connected = 1

        }
        else
        {
            ws?.close(1000,"Done")
            buConnect.text = "Connect"
            buConnect.setBackgroundColor(Color.LTGRAY)
            connected = 0
        }
    }
}