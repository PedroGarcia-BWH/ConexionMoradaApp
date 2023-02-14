package es.uca.tfg.conexionmorada.utils.storage

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class Storage {
    var storage = Firebase.storage

    fun photoAccount(imageView : ImageView, uuid : String) {
        //Glide.with(this).load(storage.reference.child("perfil/${user?.uid}")).into(profile!!)
        var imageref = Firebase.storage.reference.child("perfil/${uuid}")
        imageref.downloadUrl.addOnSuccessListener { Uri ->

            Glide.with(imageView.context)
                .load(Uri.toString())
                .into(imageView)
        }
    }

        fun uploadDNIUser(imageView: ImageView, uuid: String, data: Intent): Boolean{
            var result = true
            var storageRef = storage.reference.child("DNI/${uuid}")
            var uploadTask = storageRef.putFile(data?.data!!)
            uploadTask.addOnSuccessListener { taskSnapshot ->

                Log.d(ContentValues.TAG, "Upload success")
            }.addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Upload failed")
                result = false
            }

            //descargamos el archivo
            val urlTask = uploadTask.continueWithTask() { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(imageView.context).load(task.result).into(imageView!!)
                    Log.d(ContentValues.TAG, "Download success" + task.result)
                } else {
                    result = false
                    Log.d(ContentValues.TAG, "Download failed")
                }
            }
            return result
        }
}
