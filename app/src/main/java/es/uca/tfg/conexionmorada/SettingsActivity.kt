package es.uca.tfg.conexionmorada

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
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

    }

    fun logout() {
        var logout = findViewById<ImageButton>(R. id.ButtonLogout)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        logout.setOnClickListener {
            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("email", null)
            editor.putString("password", null)
            editor.apply()
            editor.commit()

            auth.signOut()
            //placeholder
            setResult(Activity.RESULT_OK)
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}