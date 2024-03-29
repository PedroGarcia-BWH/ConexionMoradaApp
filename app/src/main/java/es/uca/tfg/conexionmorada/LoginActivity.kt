package es.uca.tfg.conexionmorada


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import es.uca.tfg.conexionmorada.ui.MainActivity
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername
import es.uca.tfg.conexionmorada.utils.LoadingDialog
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit

class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail: TextView
    private lateinit var txtPassword: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtEmail = findViewById(R.id.email)
        txtPassword = findViewById(R.id.password)
        val logLogin = findViewById<Button>(R.id.btnAcept)

        sharedPreferences= this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)

        if(sharedPreferences.getString("email", null) != null && sharedPreferences.getString("password", null) != null){
            LoadingDialog(this).startLoadingDialog()
           login(sharedPreferences.getString("email", null)!!, sharedPreferences.getString("password", null)!!)
        }

        val registro =findViewById<TextView>(R.id.register)
        registro.setOnClickListener {
            val registerAct = Intent(this, RegisterActivity::class.java)
            startActivity(registerAct)
        }

        val regLogin = findViewById<TextView>(R.id.btnAcept)
        regLogin.setOnClickListener {
            if(txtEmail.text.toString().isEmpty() || txtPassword.text.toString().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Por favor, rellene los campos de correo electrónico y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                login(txtEmail.text.toString(), txtPassword.text.toString())
            }
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

        }

    }

    fun login(email: String, password: String) {
        var auth = Firebase.auth
        var dialog = LoadingDialog(this)
        dialog.startLoadingDialog()
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

                        FirebaseMessaging.getInstance().token
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val token = task.result
                                    var payloadUsername = PayloadUsername(auth.currentUser!!.uid, null, token)
                                    var call = APIRetrofit(this).updateToken(payloadUsername)
                                    call.enqueue(object : retrofit2.Callback<Boolean> {
                                        override fun onResponse(
                                            call: retrofit2.Call<Boolean>,
                                            response: retrofit2.Response<Boolean>
                                        ) {
                                            if (response.isSuccessful) {
                                                Log.d("Token", "Token actualizado")
                                            } else {
                                                Log.d("Token", "Token no actualizado")
                                            }
                                        }

                                        override fun onFailure(
                                            call: retrofit2.Call<Boolean>,
                                            t: Throwable
                                        ) {
                                            Log.d("Token", "Token no actualizado")
                                        }
                                    })
                                }
                            }

                        startActivity(homeAct)
                    }else{
                        dialog.dismissDialog()
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Verificación de correo")
                        builder.setMessage("La cuenta introducida no ha sido verificada. Por favor, verifique su correo electrónico.")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Aceptar", null)
                        builder.show()
                    }
                } else {
                    dialog.dismissDialog()
                    Toast.makeText(
                        applicationContext,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}