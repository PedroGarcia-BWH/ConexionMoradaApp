package es.uca.tfg.conexionmorada.cmSocial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.storage.Storage
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername

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