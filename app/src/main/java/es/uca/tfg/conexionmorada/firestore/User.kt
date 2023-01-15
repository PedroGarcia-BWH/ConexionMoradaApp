package es.uca.tfg.conexionmorada.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class User {
    companion object {
        val db = Firebase.firestore
        private lateinit var auth: FirebaseAuth
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
    }

}