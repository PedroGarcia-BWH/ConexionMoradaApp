package es.uca.tfg.conexionmorada.storage

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class Storage {

    fun photoAccount(imageView : ImageView, uuid : String){
        //Glide.with(this).load(storage.reference.child("perfil/${user?.uid}")).into(profile!!)
        var imageref = Firebase.storage.reference.child("perfil/${uuid}")
        imageref.downloadUrl.addOnSuccessListener {Uri->

            Glide.with(imageView.context)
                .load(Uri.toString())
                .into(imageView)
        }
    }
}