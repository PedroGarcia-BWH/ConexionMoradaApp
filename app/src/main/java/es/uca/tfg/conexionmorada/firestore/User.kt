package es.uca.tfg.conexionmorada.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class User {
    companion object {
        val db = Firebase.firestore
        private lateinit var auth: FirebaseAuth

         private var success = false
        fun createUser(email: String, username:String, bVgenero: Boolean, bVsexual:Boolean,
                       bIgualdad: Boolean, bNoResponder:Boolean): Boolean {
            auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val userMap = hashMapOf(
                "email" to email,
                "username" to username,
                "Igualdad" to bIgualdad,
                "Violencia de genero" to bVgenero,
                "Violencia sexual" to bVsexual,
                "No responder" to bNoResponder
            )
            if (user != null) {
                db.collection("users").document(user.uid).set(userMap)
                return true;
            }else{
                return false
            }
        }

        fun login(email: String, password: String): Boolean {
            auth = Firebase.auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) success = true
                }
            return success
        }

        fun getUser(): FirebaseUser? {
            auth = Firebase.auth
            return auth.currentUser
        }

        fun getDatosUser(): DocumentReference? {
            auth = Firebase.auth
            val user = auth.currentUser
            return user?.let { db.collection("users").document(it.uid) }
        }

    }

}