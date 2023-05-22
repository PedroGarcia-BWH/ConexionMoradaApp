package es.uca.tfg.conexionmorada.sistemaCompanero.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadSeguidores
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadChat
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage

class ChatAdapter: RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var chats: List<PayloadChat> = emptyList()
    private lateinit var Listener : ChatAdapter.onItemClickListener
    private lateinit var context: Context

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: ChatAdapter.onItemClickListener) {
        Listener = listener
    }

    fun setData(list: List<PayloadChat>) {
        chats = list
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_cardview, parent, false)
        setContext(parent.context)
        return ChatAdapter.ChatViewHolder(view, Listener)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        val chats = chats[position]

       holder.lastMensaje.text = chats.lastMensaje
        holder.date.text = chats.fecha

        Storage().photoAccount(holder.Perfil, chats.uuidUser)
        var data = User.getUsername(chats.uuidUser)
        data.addOnSuccessListener { document ->
            if (document != null) {
                    holder.nickname.text = document.getString("username")
            }
        }


        if(chats.nNotificaciones().toInt() == 0) {
            holder.imageNotificattion.visibility = View.INVISIBLE
            holder.txtNotificacionCount.visibility = View.INVISIBLE
        } else {
            holder.txtNotificacionCount.text = chats.nNotificaciones()
        }


    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatViewHolder(itemView: View, listener: ChatAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var nickname = itemView.findViewById<TextView>(R.id.nickname)
        var lastMensaje = itemView.findViewById<TextView>(R.id.txtLastMensaje)
        var date = itemView.findViewById<TextView>(R.id.txtDate)
        var Perfil = itemView.findViewById<ImageView>(R.id.Perfil)
        var imageNotificattion = itemView.findViewById<ImageView>(R.id.viewNotification)
        var txtNotificacionCount = itemView.findViewById<TextView>(R.id.txtNotificationCount)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}