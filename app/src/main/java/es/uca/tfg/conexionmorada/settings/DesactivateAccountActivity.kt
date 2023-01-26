package es.uca.tfg.conexionmorada.settings

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R

class DesactivateAccountActivity : AppCompatActivity() {
    private var auth = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desactivate_account)

        var desactivateAccount = findViewById<AppCompatButton>(R.id.deleteAccount)
        desactivateAccount.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Desactivar cuenta")
            builder.setMessage("¿Estás seguro de que quieres desactivar tu cuenta? Esta acción no se puede deshacer.")
            builder.setCancelable(true)

            builder.setPositiveButton("Ok") { dialog, which ->
                auth?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User account deleted.")
                        }
                    }
                finish()
            }
            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }
            builder.show()

        }
        exit()
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener(){
            finish()
        }
    }
}