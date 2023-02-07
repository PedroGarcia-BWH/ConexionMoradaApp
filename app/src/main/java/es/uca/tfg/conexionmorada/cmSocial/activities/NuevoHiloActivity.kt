package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.storage.Storage
import es.uca.tfg.conexionmorada.utils.Utils

class NuevoHiloActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_hilo)

        Storage().photoAccount(findViewById(R.id.perfilUser), Firebase.auth.currentUser?.uid.toString())
        var exit = findViewById<ImageView>(R.id.exit)
        Utils.exit(this, exit)

        mensajeClicked()

    }

    private fun mensajeClicked() {
        var mensaje = findViewById<TextInputEditText>(R.id.mensaje)
        mensaje.requestFocus()
    }

}