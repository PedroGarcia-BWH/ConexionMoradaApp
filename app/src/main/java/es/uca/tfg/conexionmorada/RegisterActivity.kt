package es.uca.tfg.conexionmorada

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.articles.interfaces.CRUDInterface
import es.uca.tfg.conexionmorada.articles.model.Article
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.Constants
import es.uca.tfg.conexionmorada.utils.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        val regCondition = findViewById<CheckBox>(R.id.chCondition)

        focusUsername(regUsername)

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

        regPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (regPassword.text.toString().length < 8 || !regPassword.text.toString().contains(Regex("[0-9]"))) {
                    regPassword.error = "La contraseña debe tener al menos 8 caracteres y contener al menos un dígito"
                }
            }
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
            }else if(regPassword.text.toString() != regConfPassword.text.toString() || regPassword.text.toString().equals("")) {
                Toast.makeText(
                    applicationContext,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (!regEmail.text.toString().contains("@") || !regEmail.text.toString().contains(".")
                || regEmail.text.toString().contains(" ")){
                Toast.makeText(
                    applicationContext,
                    "El email no es válido",
                    Toast.LENGTH_SHORT
                ).show()

            }else if(regUsername.error != null || regUsername.text.toString().equals("")) {
                Toast.makeText(
                    applicationContext,
                    "El nombre de usuario no es válido",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(regPassword.text.toString().length < 8 || !regPassword.text.toString().contains(Regex("[0-9]"))) {
                Toast.makeText(
                    applicationContext,
                    "La contraseña debe tener al menos 8 caracteres y contener al menos un dígito",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                var dialog = LoadingDialog(this)
                dialog.startLoadingDialog()
                Singin(
                    regEmail.text.toString(),
                    regPassword.text.toString(),
                    regUsername.text.toString(),
                    regRedes.isChecked,
                    regApuestas.isChecked,
                    regVideojuegos.isChecked,
                    regNose.isChecked
                )
                dialog.dismissDialog()
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
                    var call = APIRetrofit().addUsername(username)
                    if(bSuccess){
                        Toast.makeText(
                            baseContext, "Registro completado con éxito.",
                            Toast.LENGTH_SHORT
                        ).show()
                        var user = auth.currentUser
                        user!!.sendEmailVerification()
                        Log.d(TAG, "Email de verificacion.")
                                    //dialog
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Verificación de correo")
                        builder.setMessage("Se ha enviado un correo de verificación a su cuenta. Por favor, verifique su correo para poder iniciar sesión.")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        })
                        builder.show()

                    }else{
                        Toast.makeText(
                            baseContext, "Error al crear el usuario.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (call != null) {
                        call.enqueue(object : Callback<Boolean> {

                            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                if (response.isSuccessful) {
                                    Log.d("TAG", "onResponse: " + response.body())
                                }
                            }

                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                Log.d("TAG", "onFailure: " + t.message)
                            }
                        })
                    }



                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "No se ha podido registrar el usuario, el email ya está en uso o la contraseña es demasiado débil.",
                    Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun focusUsername(regUsername : TextView){
        regUsername.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                UsernameExist(regUsername)
            }
        }
    }

    fun UsernameExist(regUsername : TextView){
        var call = APIRetrofit().getUsername(regUsername.text.toString())
        if (call != null) {
            call.enqueue(object : Callback<Boolean> {

                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful){
                        if(response.body()!!){
                            regUsername.error = "El nombre de usuario ya existe"
                        }else{
                            Toast.makeText(applicationContext, "El nombre de usuario está disponible",
                                Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
