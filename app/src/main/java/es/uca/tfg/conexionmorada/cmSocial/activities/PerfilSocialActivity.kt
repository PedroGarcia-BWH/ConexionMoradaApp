package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.adapter.HiloAdapter
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadSeguidores
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.Utils.Companion.hiloSelected

class PerfilSocialActivity : AppCompatActivity() {
    lateinit var uuid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_social)

        var exit = findViewById<ImageView>(R.id.Exit)
        var follow = findViewById<Button>(R.id.Follow)
        Utils.exit(this, exit)

        uuid = intent.getStringExtra("uuid").toString()

        if(uuid.equals(Firebase.auth.currentUser?.uid)) {
            follow.visibility = Button.INVISIBLE
        }else{
            var call = APIRetrofit().seguidorExist(uuid, Firebase.auth.currentUser?.uid.toString())
            call.enqueue(object : retrofit2.Callback<Boolean> {
                override fun onResponse(call: retrofit2.Call<Boolean>, response: retrofit2.Response<Boolean>) {
                    if (response.isSuccessful) {
                        if(response.body()!!){
                            follow.text = "Siguiendo"
                        }

                        follow.setOnClickListener(){

                            lateinit var followCall : retrofit2.Call<Void>
                            if(follow.text.equals("Siguiendo")) followCall = APIRetrofit().deleteSeguidor(uuid, Firebase.auth.currentUser?.uid.toString())
                            else followCall = APIRetrofit().addSeguidor(PayloadSeguidores(uuid, Firebase.auth.currentUser?.uid.toString()))

                            followCall.enqueue(object : retrofit2.Callback<Void> {
                                override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                                    if (response.isSuccessful) {
                                            var numberSeguidores = findViewById<TextView>(R.id.numberSeguidoresPerfil)
                                            if(follow.text.equals("Siguiendo")) {
                                                follow.text = "Seguir"
                                                numberSeguidores.text = (numberSeguidores.text.toString().toInt() - 1).toString()
                                            }
                                            else {
                                                follow.text = "Siguiendo"
                                                numberSeguidores.text = (numberSeguidores.text.toString().toInt() + 1).toString()
                                            }
                                    }
                                }
                                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                                    Toast.makeText(this@PerfilSocialActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                }
                override fun onFailure(call: retrofit2.Call<Boolean>, t: Throwable) {
                    Toast.makeText(this@PerfilSocialActivity, "Error al comprobar si es seguidor", Toast.LENGTH_SHORT).show()
                }
            })
        }



        setData()
        dataRecyclerView()
    }

    fun setData(){
        var username = findViewById<TextView>(R.id.usernamePerfil)
        var numberSeguidos = findViewById<TextView>(R.id.numberSeguidosPerfil)
        var numberSeguidores = findViewById<TextView>(R.id.numberSeguidoresPerfil)
        var imagenPerfil = findViewById<ImageView>(R.id.imagenPerfil)

        Storage().photoAccount(imagenPerfil, uuid)

        var data = User.getUsername(uuid)
        data.addOnSuccessListener { document ->
            if (document != null) {
                username.text = document.getString("username")
            }
        }

        var call = APIRetrofit().getSeguidores(uuid)
        if(call != null){
            call.enqueue(object : retrofit2.Callback<Int> {
                override fun onResponse(call: retrofit2.Call<Int>, response: retrofit2.Response<Int>) {
                    if (response.isSuccessful) {
                        numberSeguidores.text = response.body().toString()
                    }
                }
                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    numberSeguidores.text = "none"
                }
            })
        }

        call = APIRetrofit().getSeguidos(uuid)
        if(call != null){
            call.enqueue(object : retrofit2.Callback<Int> {
                override fun onResponse(call: retrofit2.Call<Int>, response: retrofit2.Response<Int>) {
                    if (response.isSuccessful) {
                        numberSeguidos.text = response.body().toString()
                    }
                }
                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    numberSeguidos.text = "none"
                }
            })
        }
    }

    fun dataRecyclerView(){
        var recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPerfil)
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var adapter = HiloAdapter()
        recyclerView.adapter = adapter

        var call = APIRetrofit().getHilosUser(uuid)

        call.enqueue(object : retrofit2.Callback<List<PayloadHilo>> {
            override fun onResponse(call: retrofit2.Call<List<PayloadHilo>>, response: retrofit2.Response<List<PayloadHilo>>) {
                if (response.isSuccessful) {
                    adapter.setData(response.body()!!)

                    adapter.setOnItemClickListener(object : HiloAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            hiloSelected(this@PerfilSocialActivity, response.body()!![position].idHilo)
                        }
                    })
                }
            }
            override fun onFailure(call: retrofit2.Call<List<PayloadHilo>>, t: Throwable) {
                Toast.makeText(this@PerfilSocialActivity, "Error al cargar los hilos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}