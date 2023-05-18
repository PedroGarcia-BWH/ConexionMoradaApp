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

       /* holder.nickname.text = .username
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
        })*/

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
        var followButton = itemView.findViewById<Button>(R.id.follow)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}