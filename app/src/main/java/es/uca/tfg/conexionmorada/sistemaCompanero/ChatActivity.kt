package es.uca.tfg.conexionmorada.sistemaCompanero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.sistemaCompanero.adapter.ChatAdapter
import es.uca.tfg.conexionmorada.sistemaCompanero.adapter.MensajeAdapter
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadMensaje
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var id = intent.getStringExtra("id")

        addDataCabecera()

        checkLeidoMensajes(id!!)

        val handler = Handler()

        val runnable = object : Runnable {
            override fun run() {

                callData(id!!)

                handler.postDelayed(this, 5000) // Ejecutar el código cada 5 segundos
            }
        }
        //primera llamada
        handler.postDelayed(runnable,1)

        var button = findViewById<FloatingActionButton>(R.id.sendMesage)
        button.setOnClickListener(){
            var mensaje = findViewById<TextView>(R.id.Mensage)
            if(mensaje.text.isEmpty()){
                Toast.makeText(this, "Por favor escriba un mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var call = APIRetrofit(this).addMensajeChat(PayloadMensaje(
                id!!, //id del chat
                mensaje.text.toString(),
                FirebaseAuth.getInstance().currentUser!!.uid)
            )
            call.enqueue(object : retrofit2.Callback<Boolean> {
                override fun onResponse(
                    call: retrofit2.Call<Boolean>,
                    response: retrofit2.Response<Boolean>
                ) {
                    if (response.isSuccessful) {
                       if(response.body() == true){
                           mensaje.text = ""
                           callData(id)
                       }
                    }
                }

                override fun onFailure(call: retrofit2.Call<Boolean>, t: Throwable) {
                    Log.d("ERROR", "Error en la comunicacion")
                    finish()
                }
            })
        }

        exit()

    }


    private fun addDataCabecera() {
        var uuid = intent.getStringExtra("uuid")
        Storage().photoAccount(findViewById(R.id.perfil2), uuid!!)
        var data = User.getUsername(uuid!!)
        data.addOnSuccessListener { document ->
            if (document != null) {
                findViewById<TextView>(R.id.nickname2).text = document.getString("username")
            }
        }

    }

    private fun addDataRecyclerView(mensajes: List<PayloadMensaje>) {
        var rvMensajes = findViewById<RecyclerView>(R.id.rvMensajes)
        rvMensajes.layoutManager = LinearLayoutManager(this)
        var adapter = MensajeAdapter()
        rvMensajes.adapter = adapter
        (rvMensajes.adapter as MensajeAdapter).setData(mensajes)

    }


    private fun checkLeidoMensajes(id: String){
        var call = APIRetrofit(this).checkMensajesChat(id, FirebaseAuth.getInstance().currentUser!!.uid)
            call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: retrofit2.Call<Void>,
                response: retrofit2.Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("MENSAJES", "Mensajes leidos")
                }
            }

            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
               Log.d("ERROR", "Error en la comunicaion")
            }
        })
    }

    private fun callData(id: String){
        var call = APIRetrofit(this).getAllMensajesChat(id!!)
        call.enqueue(object : retrofit2.Callback<List<PayloadMensaje>> {
            override fun onResponse(
                call: retrofit2.Call<List<PayloadMensaje>>,
                response: retrofit2.Response<List<PayloadMensaje>>
            ) {
                if (response.isSuccessful) {
                    val mensajes = response.body()
                    addDataRecyclerView(mensajes!!)
                }
            }

            override fun onFailure(call: retrofit2.Call<List<PayloadMensaje>>, t: Throwable) {
                Log.d("ERROR", "Error en la comunicacion")
                //finish()
            }
        })
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener {
            finish()
        }
    }
}