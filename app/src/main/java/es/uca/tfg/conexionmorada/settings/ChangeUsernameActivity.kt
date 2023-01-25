package es.uca.tfg.conexionmorada.settings

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.retrofit.APIRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeUsernameActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_username)
        var username = findViewById<EditText>(R.id.usernameChange)
        var confirmUsername = findViewById<AppCompatButton>(R.id.confirmChange)
        username.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                UsernameExist(username)
            }
        }

        confirmUsername.setOnClickListener(){
            if(username.text.length < 4) {
                errorDialog("El nombre de usuario debe tener al menos 4 caracteres")
            }else if(username.text.contains(" ")) {
                errorDialog("El nombre de usuario no puede contener espacios")
            }else if(username.error != null){
                errorDialog("El nombre de usuario ya existe")
            }else {
                UpdateUsername(username.text.toString())
            }
        }

        exit()
    }

    fun UpdateUsername(username: String){
        db.collection("users").document(user!!.uid).update("username", username)
        Log.d("Username", "Username cambiado")
        successDialog(username)
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener(){
            finish()
        }
    }

    fun successDialog(textSuccess: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambio nombre de usuario")
        builder.setMessage(textSuccess)
        builder.setCancelable(true)

        builder.setPositiveButton("Ok") { dialog, which ->
            finish()
        }
        builder.show()
    }

    fun errorDialog(textError: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(textError)
        builder.setCancelable(true)

        builder.setPositiveButton("Ok") { dialog, which ->
            finish()
        }
        builder.show()
    }

    fun UsernameExist(username: EditText) {
        var call = APIRetrofit().getUsername(username.text.toString())
        if (call != null) {
            call.enqueue(object : Callback<Boolean> {

                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful){
                        if(response.body()!!){
                            username.error = "El nombre de usuario ya existe"
                        }else{
                            username.error = null
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