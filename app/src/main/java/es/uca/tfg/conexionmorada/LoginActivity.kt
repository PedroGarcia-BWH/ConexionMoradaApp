package es.uca.tfg.conexionmorada


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtEmail = findViewById(R.id.email)
        txtPassword = findViewById(R.id.password)
        val logLogin = findViewById<Button>(R.id.login)
        progressBar = findViewById<ProgressBar>(R.id.indeterminateBarLogin)
        progressBar.setVisibility(View.GONE)

        //login button click
        logLogin.setOnClickListener(View.OnClickListener {
            val email: String
            val password: String
            email = txtEmail.getText().toString()
            password = txtPassword.getText().toString()
            if (email != "" && password != "") {
                progressBar.setVisibility(View.VISIBLE)
                //InicioSesion(email,password)
                progressBar.setVisibility(View.GONE)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Todos los campos son requeridos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

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

            var auth = Firebase.auth

           auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtPassword.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val homeAct = Intent(this, MainActivity::class.java)
                        startActivity(homeAct)
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
}