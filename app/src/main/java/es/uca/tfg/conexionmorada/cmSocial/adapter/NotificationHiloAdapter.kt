package es.uca.tfg.conexionmorada.cmSocial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadNotificationHilo
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.storage.Storage

class NotificationHiloAdapter : RecyclerView.Adapter<NotificationHiloAdapter.NotificacionHiloViewHolder>()  {
    private var notificaciones: List<PayloadNotificationHilo> = emptyList()
    private lateinit var Listener : NotificationHiloAdapter.onItemClickListener
    private lateinit var context: Context

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: NotificationHiloAdapter.onItemClickListener) {
        Listener = listener
    }

    fun addData(notifiacion: PayloadNotificationHilo) {
        notificaciones = notificaciones + notifiacion
        notifyDataSetChanged()
    }

    fun setData(list: List<PayloadNotificationHilo>) {
        notificaciones = list
        notifyDataSetChanged()
    }

    fun getData(): List<PayloadNotificationHilo> {
        return notificaciones
    }

    fun clearData() {
        notificaciones = emptyList()
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHiloAdapter.NotificacionHiloViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_hilo_cardview, parent, false)
        setContext(parent.context)
        return NotificationHiloAdapter.NotificacionHiloViewHolder(view, Listener)
    }

    override fun onBindViewHolder(holder: NotificationHiloAdapter.NotificacionHiloViewHolder, position: Int) {
        val notificacion = notificaciones[position]

        holder.nicknameNotificador.text = notificacion.username


        var data = User.getUsername(notificacion.autorUuid)
        data.addOnSuccessListener { document ->
            if (document != null) {
                holder.nicknameNotificado.text = document.getString("username")
            }
        }



        Storage().photoAccount(holder.imagenNotificador, notificacion.username)
        Storage().photoAccount(holder.imagenNotificado, notificacion.autorUuid)

        holder.fechaNotificador.text = notificacion.dateCreationNotificacion

        holder.mensajeNotificador.text = notificacion.mensajeNotificacion

        holder.mensajeNotificado.text = notificacion.mensajeHilo



    }

    override fun getItemCount(): Int {
        return notificaciones.size
    }

    class NotificacionHiloViewHolder(itemView: View, listener: NotificationHiloAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var imagenNotificador = itemView.findViewById<ImageView>(R.id.Perfil)
        var nicknameNotificador = itemView.findViewById<TextView>(R.id.nickname)
        var mensajeNotificador = itemView.findViewById<TextView>(R.id.cuerpoMensaje)
        var fechaNotificador = itemView.findViewById<TextView>(R.id.horaMensaje)

        var imagenNotificado = itemView.findViewById<ImageView>(R.id.PerfilHilo)
        var nicknameNotificado = itemView.findViewById<TextView>(R.id.nicknameHilo)
        var mensajeNotificado = itemView.findViewById<TextView>(R.id.cuerpoMensajeHilo)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}