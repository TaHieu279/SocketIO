package com.tavanhieu.socketio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.json.JSONObject
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var lstNameUser: ListView
    lateinit var lstMessage: ListView
    lateinit var edtMessage: EditText
    lateinit var btnRegister: ImageButton
    lateinit var btnSendMessage: ImageButton
    var arrNameUser = ArrayList<String>()
    var arrMessage  = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//-------------------------------------------------------------------------------------------------------------
        anhXa()
        //Đổ ra danh sách người dùng:
        val adapterName = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrNameUser)
        lstNameUser.adapter = adapterName
        //Đổ ra danh sác tin nhắn:
        val adapterMessage = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrMessage)
        lstMessage.adapter = adapterMessage

//-------------------------------------------------------------------------------------------------------------
        SocketHandler.setSocket()
        val msocket = SocketHandler.getSocket()
        msocket.connect()

//-------------------------------------------------------------------------------------------------------------
        //Nhận kết quả từ Server trả về xem có đki được hay k?
        msocket.on("server-send-data") {
            if(it[0] != null) {
                runOnUiThread {
                    val ob: JSONObject = it[0] as JSONObject
                    val kiemtra = ob.getBoolean("kiemtra") //Kiểm tra xem tài khoản đã có hay chưa.

                    if(!kiemtra) {
                        Toast.makeText(this, "Đăng kí thành công", Toast.LENGTH_SHORT).show()
                    } else
                        Toast.makeText(this, "Vui lòng sử dụng tên khác", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Nhận danh sách các user đã đăng kí để đổ ra listView:
        msocket.on("server-send-register") { it2 ->
            if(it2[0] != null) {
                runOnUiThread {
                    val ob: JSONObject = it2[0] as JSONObject
                    val arrayJson = ob.getJSONArray("username")

                    arrNameUser.clear()
                    for (i in 0 until arrayJson.length()) {
                        arrNameUser.add(arrayJson.getString(i))
                    }
                    adapterName.notifyDataSetChanged()
                }
            }
        }
        //Gửi tài khoản lên server để đki:
        btnRegister.setOnClickListener {
            if(edtMessage.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Bạn chưa nhập tên đăng ký", Toast.LENGTH_SHORT).show()
            } else {
                msocket.emit("client-send-data", edtMessage.text.toString().trim())
            }
        }

//--------------------------------------------------------------------------------------------------------------
        //Nhận tin nhắn từ các user gửi lên và add vào Message:
        msocket.on("server-send-message") {
            if(it[0] != null) {
                runOnUiThread {
                    val ob:JSONObject = it[0] as JSONObject
                    val mes = ob.getString("message")
                    arrMessage.add(mes)
                    adapterMessage.notifyDataSetChanged()
                }
            }
        }
        //Gửi message lên server để hiển thị với mọi người:
        btnSendMessage.setOnClickListener {
            if(edtMessage.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Bạn chưa nhập tin nhắn", Toast.LENGTH_SHORT).show()
            } else {
                msocket.emit("client-send-message", edtMessage.text.toString().trim())
            }
        }
    }

    fun anhXa() {
        lstMessage      = findViewById(R.id.lstMessage)
        lstNameUser     = findViewById(R.id.lstNameUser)
        edtMessage      = findViewById(R.id.edtMessage)
        btnRegister     = findViewById(R.id.btnRegister)
        btnSendMessage  = findViewById(R.id.btnSendMessage)
    }
}