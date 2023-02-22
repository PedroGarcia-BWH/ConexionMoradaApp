package es.uca.tfg.conexionmorada.cmSocial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadNotificationPersona
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.storage.Storage


class NotificationPersonaAdapter : RecyclerView.Adapter<NotificationPersonaAdapter.NotificacionPersonaViewHolder>() {

    private var notificaciones: List<PayloadNotificationPersona> = emptyList()
    private lateinit var Listener : NotificationPersonaAdapter.onItemClickListener
    private lateinit var context: Context

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: NotificationPersonaAdapter.onItemClickListener) {
        Listener = listener
    }

    fun addData(notifiacion: PayloadNotificationPersona) {
        notificaciones = notificaciones + notifiacion
        notifyDataSetChanged()
    }

    fun setData(list: List<PayloadNotificationPersona>) {
        notificaciones = list
        notifyDataSetChanged()
    }

    fun getData(): List<PayloadNotificationPersona> {
        return notificaciones
    }

    fun clearData() {
        notificaciones = emptyList()
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationPersonaAdapter.NotificacionPersonaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_persona_cardview, parent, false)
        setContext(parent.context)
        return NotificationPersonaAdapter.NotificacionPersonaViewHolder(view, Listener)
    }

    override fun onBindViewHolder(holder: NotificationPersonaAdapter.NotificacionPersonaViewHolder, position: Int) {
        val notificacion = notificaciones[position]
        Storage().photoAccount(holder.imagen, notificacion.username)
        var data = User.getUsername(notificacion.username)
        data.addOnSuccessListener { document ->
            if (document != null) {
                holder.nickname.text = document.getString("username")
            }
        }

        //holder.mensaje.text = notificacion.mensaje
        holder.fecha.text = notificacion.dateCreation
    }

    override fun getItemCount(): Int {
        return notificaciones.size
    }

    class NotificacionPersonaViewHolder(itemView: View, listener: NotificationPersonaAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var imagen = itemView.findViewById<ImageView>(R.id.Perfil)
        var nickname = itemView.findViewById<TextView>(R.id.nickname)
        var mensaje = itemView.findViewById<TextView>(R.id.mensaje)
        var fecha = itemView.findViewById<TextView>(R.id.horaMensaje)
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}