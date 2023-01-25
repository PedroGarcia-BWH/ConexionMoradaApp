package es.uca.tfg.conexionmorada.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import es.uca.tfg.conexionmorada.LoginActivity
import es.uca.tfg.conexionmorada.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        auth = FirebaseAuth.getInstance()

        logout()
        changePhotoNavigate()
        changeUsernameNavegate()
        changePasswordNavegate()
        changePreferenciesNavegate()
        desactivateAccountNavegate()
        notificationsNavigate()
        exit()

    }

    fun changePhotoNavigate() {
        var changePhoto = findViewById<CardView>(R.id.changePhotoCardView)
        changePhoto.setOnClickListener {
            var intent = Intent(this, ChangePhotoActivity::class.java)
            startActivity(intent)
        }
    }
    fun changeUsernameNavegate(){
        var changeUsername = findViewById<CardView>(R.id.changeUsernameCardView)
        changeUsername.setOnClickListener {
            var intent = Intent(this, ChangeUsernameActivity::class.java)
            startActivity(intent)
        }
    }

    fun changePasswordNavegate(){
        var changePassword = findViewById<CardView>(R.id.changePasswordCardview)
        changePassword.setOnClickListener {
            var intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    fun changePreferenciesNavegate(){
        var changePreferencies = findViewById<CardView>(R.id.preferencesCardview)
        changePreferencies.setOnClickListener {
            var intent = Intent(this, ChangePreferenciesActivity::class.java)
            startActivity(intent)
        }
    }

    fun desactivateAccountNavegate(){
        var desactivateAccount = findViewById<CardView>(R.id.desactivateAccountCardview)
        desactivateAccount.setOnClickListener {
            var intent = Intent(this, DesactivateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    fun notificationsNavigate() {
        var notifications = findViewById<CardView>(R.id.notificationCardview)
        notifications.setOnClickListener {
            var intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }
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