package es.uca.tfg.conexionmorada.utils.storage

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.Blob
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import es.uca.tfg.conexionmorada.R
import java.io.IOException
import java.util.concurrent.TimeUnit


class Storage {
    var storage = Firebase.storage

    fun photoAccount(imageView : ImageView, uuid : String) {
        //Glide.with(this).load(storage.reference.child("perfil/${user?.uid}")).into(profile!!)
        var imageref = Firebase.storage.reference.child("perfil/${uuid}")
        imageref.downloadUrl.addOnSuccessListener { Uri ->

            Glide.with(imageView.context)
                .load(Uri.toString())
                .circleCrop()
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

    fun getImageArticle(url: String, imageView: ImageView) {
        var url = url.replace("%2F", "/")
        // Obtener el nombre del archivo de la URL
        val nombreArchivo = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('?'))
        var storageRef = storage.reference.child("portadaArticulos/${nombreArchivo}")

        storageRef.downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(imageView.context)
                .load(Uri.toString())
                .into(imageView)
        }
    }
}
