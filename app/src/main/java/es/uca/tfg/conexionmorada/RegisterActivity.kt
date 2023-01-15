package es.uca.tfg.conexionmorada

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.firestore.User

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val regEmail = findViewById<TextView>(R.id.emailRegister)
        val regUsername = findViewById<TextView>(R.id.nickNameRegister)
        val regPassword = findViewById<TextView>(R.id.passwordRegister)
        val regConfPassword = findViewById<TextView>(R.id.passwordConfirm)
        val regRegistro = findViewById<Button>(R.id.confirmRegister)
        val regRedes = findViewById<CheckBox>(R.id.redesSociales)
        val regApuestas = findViewById<CheckBox>(R.id.Apuestas)
        val regVideojuegos = findViewById<CheckBox>(R.id.Videojuegos)
        val regNose = findViewById<CheckBox>(R.id.NoSabe)
        val progressBar = findViewById<ProgressBar>(R.id.reg_progress)
        val regCondition = findViewById<CheckBox>(R.id.chCondition)

        val tCondition = findViewById<TextView>(R.id.textConditions)
        tCondition.setOnClickListener {
            val condAct = Intent(this, ConditionsActivity::class.java)
            startActivity(condAct)
        }

        /* Intercambio de bloqueos entre checkbox */
        val noResponse = findViewById<CheckBox>(R.id.NoSabe)

        noResponse.setOnCheckedChangeListener { buttonView, isChecked ->
            regRedes.isEnabled = !isChecked
            regApuestas.isEnabled = !isChecked
            regVideojuegos.isEnabled = !isChecked
        }

        regRedes.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regApuestas.isChecked && !regVideojuegos.isChecked
        }
        regApuestas.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regRedes.isChecked && !regVideojuegos.isChecked
        }
        regVideojuegos.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regApuestas.isChecked && !regRedes.isChecked
        }

        //comprobar automaticamente que password y confirmacion coinciden
        regConfPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (regPassword.text.toString() != regConfPassword.text.toString()) {
                    regConfPassword.error = "Las contraseñas no coinciden"
                }
            }
        }

        regEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!regEmail.text.toString().contains("@") || !regEmail.text.toString().contains(".")
                    || regEmail.text.toString().contains(" ")) {
                    regEmail.error = "El email no es válido"
                }
            }
        }

        regRegistro.setOnClickListener(View.OnClickListener {
            if(!regCondition.isChecked){
                Toast.makeText(
                    applicationContext,
                    "Debe aceptar las condiciones de uso",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                Singin(
                    regEmail.text.toString(),
                    regPassword.text.toString(),
                    regUsername.text.toString(),
                    regRedes.isChecked,
                    regApuestas.isChecked,
                    regVideojuegos.isChecked,
                    regNose.isChecked
                )
            }
        })
    }

    fun Singin(email: String, password: String, username: String, bVgenero: Boolean, bVsexual: Boolean,
               bIgualdad: Boolean, bNoResponder: Boolean) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val bSuccess = User.createUser(email, username, bVgenero, bVsexual, bIgualdad, bNoResponder)
                    if(bSuccess){
                        Toast.makeText(
                            baseContext, "Registro completado con éxito.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(
                            baseContext, "Error al crear el usuario.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }
}
