package es.uca.tfg.conexionmorada.sistemaCompanero.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadChat
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadMensaje
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.storage.Storage

class MensajeAdapter: RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    private var mensajes: List<PayloadMensaje> = emptyList()

    private lateinit var context: Context



    fun setData(list: List<PayloadMensaje>) {
        mensajes = list
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeAdapter.MensajeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mensaje_cardview, parent, false)
        setContext(parent.context)
        return MensajeAdapter.MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeAdapter.MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.mensaje.text = mensaje.mensaje
        holder.date.text = mensaje.dateCreated

    }

    override fun getItemCount(): Int {
        return mensajes.size
    }

    class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var mensaje = itemView.findViewById<TextView>(R.id.txtLastMensaje)
        var date = itemView.findViewById<TextView>(R.id.txtDate)

    }
}