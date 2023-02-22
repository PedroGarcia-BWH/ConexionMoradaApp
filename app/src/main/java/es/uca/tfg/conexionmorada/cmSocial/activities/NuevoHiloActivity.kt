package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage
import es.uca.tfg.conexionmorada.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NuevoHiloActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_hilo)

        Storage().photoAccount(findViewById(R.id.perfilUser), Firebase.auth.currentUser?.uid.toString())
        var exit = findViewById<ImageView>(R.id.exit)
        Utils.exit(this, exit)

        mensajeClicked()
        addHiloListener()

    }

    private fun mensajeClicked() {
        var mensaje = findViewById<TextInputEditText>(R.id.mensaje)
        mensaje.requestFocus()
    }

    private fun addHiloListener(){
        var createHilo = findViewById<Button>(R.id.createHilo)
        var mensaje = findViewById<TextInputEditText>(R.id.mensaje)

        createHilo.setOnClickListener {
            if(mensaje.text.toString().isEmpty()){

            }else{
                var payloadHilo = PayloadHilo(null, Firebase.auth.currentUser?.uid.toString(), mensaje.text.toString(),null, Date(), 0, 0, false, false)
                var call = APIRetrofit().addHilo(payloadHilo)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful){
                            //Toast.makeText(this@NuevoHiloActivity, "Hilo creado correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@NuevoHiloActivity, "No se pudo crear el hilo, intentelo de nuevo", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

}