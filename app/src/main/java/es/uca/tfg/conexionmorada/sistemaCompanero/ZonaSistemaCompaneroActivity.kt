package es.uca.tfg.conexionmorada.sistemaCompanero

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.articles.adapter.ArticleAdapter
import es.uca.tfg.conexionmorada.sistemaCompanero.adapter.ChatAdapter
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadChat
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit

class ZonaSistemaCompaneroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zona_sistema_companero)

        var button = findViewById<ImageView>(R.id.seePoints)
        button.setOnClickListener {
            val intent = Intent(this, HistoryPointCompaneroActivity::class.java)
            startActivity(intent)
        }
        exit()

        val handler = Handler()

      /*  val runnable = object : Runnable {
            override fun run() {
                var call = APIRetrofit(this@ZonaSistemaCompaneroActivity).getAllChatsByUuid(FirebaseAuth.getInstance().uid.toString())
                call.enqueue(object : retrofit2.Callback<List<PayloadChat>> {
                    override fun onResponse(
                        call: retrofit2.Call<List<PayloadChat>>,
                        response: retrofit2.Response<List<PayloadChat>>
                    ) {
                        if (response.isSuccessful) {
                            val chats = response.body()
                            addDataChats(chats!!)

                        }
                    }

                    override fun onFailure(call: retrofit2.Call<List<PayloadChat>>, t: Throwable) {
                        Toast.makeText(this@ZonaSistemaCompaneroActivity, "No se ha podido cargar los chats, intentelo de nuevo", Toast.LENGTH_SHORT).show()
                    }
                })

                handler.postDelayed(this, 5000) // Ejecutar el código cada 5 segundos
            }
        }
        //primera llamada
        handler.postDelayed(runnable,1)*/

        var call = APIRetrofit(this@ZonaSistemaCompaneroActivity).getAllChatsByUuid(FirebaseAuth.getInstance().uid.toString())
        call.enqueue(object : retrofit2.Callback<List<PayloadChat>> {
            override fun onResponse(
                call: retrofit2.Call<List<PayloadChat>>,
                response: retrofit2.Response<List<PayloadChat>>
            ) {
                if (response.isSuccessful) {
                    val chats = response.body()
                    if(chats!!.size == 0){
                        var noChatImage = findViewById<ImageView>(R.id.noChatImage)
                        var noChat = findViewById<TextView>(R.id.noChat)
                        noChatImage.visibility = ImageView.VISIBLE
                        noChat.visibility = TextView.VISIBLE
                    }
                    addDataChats(chats!!)

                }
            }

            override fun onFailure(call: retrofit2.Call<List<PayloadChat>>, t: Throwable) {
                Toast.makeText(this@ZonaSistemaCompaneroActivity, "No se ha podido cargar los chats, intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addDataChats(chats: List<PayloadChat>){
        var rvChats = findViewById<RecyclerView>(R.id.rvChats)
        rvChats.layoutManager = LinearLayoutManager(this)
        var adapter = ChatAdapter()
        rvChats.adapter = adapter
        (rvChats.adapter as ChatAdapter).setData(chats)
        //Toast.makeText(activity, articles.size, Toast.LENGTH_SHORT).show()
            adapter.setOnItemClickListener(object : ChatAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                chatSelected(chats[position])
            }
        })
    }

    private fun chatSelected(chat: PayloadChat) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("id", chat.id)
        intent.putExtra("uuid", chat.uuidUser)
        startActivity(intent)
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener {
            finish()
        }
    }
}