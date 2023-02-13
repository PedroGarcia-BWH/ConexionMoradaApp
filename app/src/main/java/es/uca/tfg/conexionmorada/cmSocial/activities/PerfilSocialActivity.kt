package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.storage.Storage
import es.uca.tfg.conexionmorada.utils.Utils

class PerfilSocialActivity : AppCompatActivity() {
    lateinit var uuid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_social)

        var exit = findViewById<ImageView>(R.id.Exit)
        Utils.exit(this, exit)

        uuid = intent.getStringExtra("uuid").toString()

        if(uuid.equals(Firebase.auth.currentUser?.uid)) {
            var follow = findViewById<Button>(R.id.Follow)
            follow.visibility = Button.INVISIBLE
        }

        setData()
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
}