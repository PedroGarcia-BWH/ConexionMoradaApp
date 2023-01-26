package es.uca.tfg.conexionmorada.settings

import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R

class ChangePreferenciesActivity : AppCompatActivity() {
    var auth = Firebase.auth.currentUser
    var db = Firebase.firestore

    var generoBox : CheckBox? = null
    var sexualBox : CheckBox? = null
    var igualdadBox : CheckBox? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_preferencies)

        var changePreferencies = findViewById<AppCompatButton>(R.id.confirmChange)
        generoBox = findViewById<CheckBox>(R.id.generoBox)
        sexualBox = findViewById<CheckBox>(R.id.sexualBox)
        igualdadBox = findViewById<CheckBox>(R.id.IgualdadBox)

        //Mostrar preferencias actuales
        preferenciasActuales()

        changePreferencies.setOnClickListener {
            db.collection("users").document(auth!!.uid).update("Violencia de genero", generoBox!!.isChecked)
            db.collection("users").document(auth!!.uid).update("Violencia sexual", sexualBox!!.isChecked)
            db.collection("users").document(auth!!.uid).update("Igualdad", igualdadBox!!.isChecked)

            //log
            Log.d(TAG, "Preferencias cambiadas")
            Toast.makeText(this, "Preferencias cambiadas correctamente", Toast.LENGTH_SHORT).show()

            preferenciasActuales()
        }
        exit()
    }

    fun preferenciasActuales() {
        db.collection("users").document(auth!!.uid).get().addOnSuccessListener { document ->
            if (document != null) {
                generoBox!!.isChecked = document.getBoolean("Violencia de genero")!!
                sexualBox!!.isChecked = document.getBoolean("Violencia sexual")!!
                igualdadBox!!.isChecked = document.getBoolean("Igualdad")!!
            }
        }
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener(){
            finish()
        }
    }
}