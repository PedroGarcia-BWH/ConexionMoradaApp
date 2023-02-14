package es.uca.tfg.conexionmorada.settings

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import es.uca.tfg.conexionmorada.LoginActivity
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.utils.storage.Storage

class ChangePhotoActivity : AppCompatActivity() {
    private var user = Firebase.auth.currentUser
    lateinit var profile : ImageView
    var storage = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_photo)
        profile = findViewById(R.id.DNI)
       
        Storage().photoAccount(profile, user?.uid!!)
        var change = findViewById<AppCompatButton>(R.id.confirmChange)

        //on click
        change.setOnClickListener() {
            //change photo
            var openGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, 1000)
        }
        exit()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                val profileUpdates = userProfileChangeRequest {
                    var storageRef = storage.reference
                    val photoRef = storageRef.child("perfil/${user?.uid}")
                    var uploadTask = photoRef.putFile(data?.data!!)
                    uploadTask.addOnSuccessListener { taskSnapshot ->

                        Log.d(TAG, "Upload success")
                    }.addOnFailureListener { exception ->
                        Log.d(TAG, "Upload failed")
                    }

                    //descargamos el archivo
                    val urlTask = uploadTask.continueWithTask() { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        photoRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Glide.with(applicationContext).load(task.result).into(profile!!)
                            Log.d(TAG, "Download success" + task.result)
                            successDialog("Foto de perfil cambiada correctamente, inicie sesión de nuevo")
                        } else {
                            Log.d(TAG, "Download failed")
                        }
                    }
                }
                user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }


            }
        }
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener(){
            finish()
        }
    }

    fun successDialog(textSuccess: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambio de foto de perfil")
        builder.setMessage(textSuccess)
        builder.setCancelable(true)

        builder.setPositiveButton("Ok") { dialog, which ->
            finishAffinity()
            //loginActivity()
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        builder.show()
    }
}

