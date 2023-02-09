package es.uca.tfg.conexionmorada.verify

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.LoginActivity
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.storage.Storage
import retrofit2.Call
import retrofit2.Response

class VerifyActivity : AppCompatActivity() {
    lateinit var confirmButton : AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        confirmButton = findViewById<AppCompatButton>(R.id.confirm)
        confirmButton.isEnabled = false

        uploadPhoto()
        confirmButton.setOnClickListener() {
            var result = User.updateUserVerificado()
            if(result) {
                Toast.makeText(this, "Verificación realizada con éxito", Toast.LENGTH_SHORT).show()
                //añadimos el usuario a la base de datos para asociar la app
                var call = APIRetrofit().addUserApp(Firebase.auth.currentUser!!.uid)
                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful){
                            Log.d(ContentValues.TAG, "Usuario añadido correctamente")
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d(ContentValues.TAG, "No se pudo añadir el usuario")
                    }
                })
                finishAffinity()
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Error al realizar la verificación, intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun uploadPhoto() {
        var upload = findViewById<AppCompatButton>(R.id.upload)

        upload.setOnClickListener() {
            var openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                var imageView = findViewById<ImageView>(R.id.DNI)
                var user = Firebase.auth.currentUser
                var result = Storage().uploadDNIUser(imageView, user?.uid.toString(), data!!)
                if(result) {
                    confirmButton.isEnabled = true
                }
            }

        }
    }

}
