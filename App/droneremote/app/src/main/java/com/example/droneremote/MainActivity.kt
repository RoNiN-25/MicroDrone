package com.example.droneremote

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*

class MainActivity : AppCompatActivity() {

    private val SERVER_IP = "http://192.168.4.1:81"
    private val client = OkHttpClient()
    private var request: Request? = null
    private val listener = WebSocketListen()
    private var ws:WebSocket? = null
    private var connected = 0
    private var angle1 = 0
    private var angle2 = 0
    private var str1 = 0
    private var str2 = 0


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
        val data = convertData(angle1,str1,angle2,str2)
        val yaw = data[0]
        val throttle = data[1]
        val roll = data[2]
        val pitch = data[3]
        println("Yaw=$yaw\tThrottle=$throttle\tRoll=$roll\tpitch=$pitch")
//        ws?.send("$angle1;$str1;$angle2;$str2;")
        ws?.send("$yaw;$throttle;$roll;$pitch")
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
            buConnect.text = resources.getText(R.string.disconnect)
            buConnect.setBackgroundColor(Color.RED)
            connected = 1

        }
        else
        {
            ws?.close(1000,"Done")
            buConnect.text = resources.getString(R.string.connect)
            buConnect.setBackgroundColor(Color.LTGRAY)
            connected = 0
        }
    }

    private fun convertData(angle1:Int, strength1:Int, angle2:Int, strength2:Int):List<Int>
    {
        var ang1 = angle1.toDouble()
        var ang2 = angle2.toDouble()
        ang1 *= kotlin.math.PI/180
        ang2 *= kotlin.math.PI/180
        val x1 = (strength1 * kotlin.math.cos(ang1)).toInt()
        val y1 = (strength1 * kotlin.math.sin(ang1) + 100).toInt()
        val x2 = (strength2 * kotlin.math.cos(ang2)).toInt()
        val y2 = (strength2 * kotlin.math.sin(ang2)).toInt()
        return listOf(x1,y1,x2,y2)
    }
  }