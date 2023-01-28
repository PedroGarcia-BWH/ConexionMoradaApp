package es.uca.tfg.conexionmorada.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R

class ChangePasswordActivity : AppCompatActivity() {

    private val user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        var password1 = findViewById<EditText>(R.id.passwordChange)
        var password2 = findViewById<EditText>(R.id.passwordChangeConfirm)

        password2.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!password1.text.toString().equals(password2.text.toString())) {
                    password2.error = "Las contraseñas no coinciden"
                }
            }
        }


        var confirmPassword = findViewById<AppCompatButton>(R.id.confirmChange)
        confirmPassword.setOnClickListener(){
            if(confirmPassword.text.length < 8) {
                dialogError("La contraseña debe tener al menos 8 caracteres")
            }else if(!password1.text.equals(password2.text)){
                dialogError("Las contraseñas no coinciden")
            }else if(password1.text.contains(" ")){
                dialogError("La contraseña no puede contener espacios")
            }else{
                user?.updatePassword(password1.text.toString())
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dialogSuccess("La contraseña se ha cambiado correctamente")
                        }
                    }
            }
        }

        exit()
    }

    fun dialogError(textError: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(textError)
        builder.setCancelable(true)

        builder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun dialogSuccess(textError: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Contraseña cambiada")
        builder.setMessage(textError)
        builder.setCancelable(true)

        builder.setPositiveButton("Ok") { dialog, which ->
            finish()
        }
        builder.show()
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener(){
            finish()
        }
    }

}