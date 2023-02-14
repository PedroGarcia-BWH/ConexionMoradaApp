package es.uca.tfg.conexionmorada.cmSocial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadSeguidores
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.storage.Storage
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit

class PersonaAdapter: RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {

    private var personas: List<PayloadUsername> = emptyList()
    private lateinit var Listener : PersonaAdapter.onItemClickListener
    private lateinit var context: Context

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: PersonaAdapter.onItemClickListener) {
        Listener = listener
    }

    fun setData(list: List<PayloadUsername>) {
        personas = list
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaAdapter.PersonaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.persona_cardview, parent, false)
        setContext(parent.context)
        return PersonaAdapter.PersonaViewHolder(view, Listener)
    }

    override fun onBindViewHolder(holder: PersonaAdapter.PersonaViewHolder, position: Int) {
        val persona = personas[position]

        holder.nickname.text = persona.username
        Storage().photoAccount(holder.Perfil, persona.uuid)


        var call = APIRetrofit().seguidorExist(persona.uuid, Firebase.auth.currentUser?.uid.toString())
        call.enqueue(object : retrofit2.Callback<Boolean> {
            override fun onResponse(call: retrofit2.Call<Boolean>, response: retrofit2.Response<Boolean>) {
                if (response.isSuccessful) {
                    if(response.body()!!){
                        holder.followButton.text = "Siguiendo"
                    }

                    holder.followButton.setOnClickListener(){

                        lateinit var followCall : retrofit2.Call<Void>
                        if(holder.followButton.text.equals("Siguiendo")) followCall = APIRetrofit().deleteSeguidor(persona.uuid, Firebase.auth.currentUser?.uid.toString())
                        else followCall = APIRetrofit().addSeguidor(PayloadSeguidores(persona.uuid, Firebase.auth.currentUser?.uid.toString()))

                        followCall.enqueue(object : retrofit2.Callback<Void> {
                            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                                if (response.isSuccessful) {
                                    if(holder.followButton.text.equals("Siguiendo")) {
                                        holder.followButton.text = "Seguir"
                                    }
                                    else {
                                        holder.followButton.text = "Siguiendo"
                                    }
                                }
                            }
                            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                            }
                        })
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<Boolean>, t: Throwable) {
            }
        })

    }

    override fun getItemCount(): Int {
        return personas.size
    }

    class PersonaViewHolder(itemView: View, listener: PersonaAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var nickname = itemView.findViewById<TextView>(R.id.nickname)
        var Perfil = itemView.findViewById<ImageView>(R.id.Perfil)
        var followButton = itemView.findViewById<Button>(R.id.follow)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}