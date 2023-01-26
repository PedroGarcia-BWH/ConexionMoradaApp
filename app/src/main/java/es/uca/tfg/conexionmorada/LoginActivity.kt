package es.uca.tfg.conexionmorada


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.ui.HomeFragment
import es.uca.tfg.conexionmorada.ui.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail: TextView
    private lateinit var txtPassword: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtEmail = findViewById(R.id.email)
        txtPassword = findViewById(R.id.password)
        val logLogin = findViewById<Button>(R.id.login)
        progressBar = findViewById<ProgressBar>(R.id.indeterminateBarLogin)
        progressBar.setVisibility(View.GONE)

        sharedPreferences= this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)

        if(sharedPreferences.getString("email", null) != null && sharedPreferences.getString("password", null) != null){
           login(sharedPreferences.getString("email", null)!!, sharedPreferences.getString("password", null)!!)
        }

        val registro =findViewById<TextView>(R.id.register)
        registro.setOnClickListener {
            val registerAct = Intent(this, RegisterActivity::class.java)
            startActivity(registerAct)
        }

        val regLogin = findViewById<TextView>(R.id.login)
        regLogin.setOnClickListener {
           progressBar.setVisibility(View.VISIBLE)

            /*var success = User.login(txtEmail.text.toString(), txtPassword.text.toString())
            if (success) {
                val homeAct = Intent(this, MainActivity::class.java)
                startActivity(homeAct)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Usuario o contraseña incorrectos",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
            login(txtEmail.text.toString(), txtPassword.text.toString())

        }

    }

    fun login(email: String, password: String) {
        var auth = Firebase.auth

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if(auth.currentUser!!.isEmailVerified) {
                        var regSession = findViewById<CheckBox>(R.id.session)
                        val homeAct = Intent(this, MainActivity::class.java)

                        if (regSession.isChecked) {
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString("email", txtEmail.text.toString())
                            editor.putString("password", txtPassword.text.toString())
                            editor.apply()
                            editor.commit()
                        }

                        startActivity(homeAct)
                    }else{
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Verificación de correo")
                        builder.setMessage("La cuenta introducida no ha sido verificada. Por favor, verifique su correo electrónico.")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Aceptar", null)
                        builder.show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        progressBar.setVisibility(View.GONE)
    }
}