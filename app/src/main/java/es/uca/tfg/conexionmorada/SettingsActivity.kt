package es.uca.tfg.conexionmorada

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.ui.MainActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        auth = FirebaseAuth.getInstance()

        logout()
        exit()

    }

    fun logout() {
        var logout = findViewById<CardView>(R.id.logout)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        logout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Cerrar sesión")
            builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
            builder.setCancelable(true)

            builder.setPositiveButton("Si") { dialog, which ->
                val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                editor.putString("email", null)
                editor.putString("password", null)
                editor.putString("genero", null)
                editor.putString("sexual", null)
                editor.putString("igualdad", null)
                editor.apply()
                editor.commit()

                auth.signOut()
                //placeholder
                setResult(Activity.RESULT_OK)
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.cancel()
            }

            builder.create().show()
        }
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener {
            finish()
        }
    }
}