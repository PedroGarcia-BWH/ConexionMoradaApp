package es.uca.tfg.conexionmorada.settings

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R

class ChangePhotoActivity : AppCompatActivity() {
    private var user = Firebase.auth.currentUser
    var profile : ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_photo)
        profile = findViewById<ImageView>(R.id.profile)
        var change = findViewById<AppCompatButton>(R.id.confirmChange)

        //on click
        change.setOnClickListener() {
            //change photo
            var openGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, 1000)
            //onActivityResult
            //cambiar imageview
            //profile.setImageURI(user?.photoUrl)

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                val profileUpdates = userProfileChangeRequest {
                    profile?.setImageURI(data?.data)
                    photoUri = data?.data
                }
                user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }
            }
        }
    }
}

