package es.uca.tfg.conexionmorada


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail: TextView
    private lateinit var txtPassword: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtEmail = findViewById(R.id.email)
        txtPassword = findViewById(R.id.password)
        val logLogin = findViewById<Button>(R.id.login)
        val progressBar = findViewById<ProgressBar>(R.id.progress)

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

    }
}