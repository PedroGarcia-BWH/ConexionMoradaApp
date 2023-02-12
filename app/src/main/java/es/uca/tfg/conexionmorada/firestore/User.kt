package es.uca.tfg.conexionmorada.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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
                "No responder" to bNoResponder,
                "verificado" to false
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

        fun  getDatosUser(uid: String): DocumentReference? {
            return db.collection("users").document(uid)
        }

        fun UserVerificado(): DocumentReference? {
            auth = Firebase.auth
            val user = auth.currentUser

            return db.collection("users").document(user!!.uid)

        }

        fun updateUserVerificado(): Boolean {
            auth = Firebase.auth
            val user = auth.currentUser
            var verificado = false
            if (user != null) {
                db.collection("users").document(user.uid).update("verificado", true)
                verificado = true
            }
            return verificado
        }

        fun getUsername(): Task<DocumentSnapshot> {
            auth = Firebase.auth
            val user = auth.currentUser
            var username: String? = null

            return db.collection("users").document(user!!.uid).get()
        }
    }

}