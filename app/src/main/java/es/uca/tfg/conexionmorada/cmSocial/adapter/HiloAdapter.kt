package es.uca.tfg.conexionmorada.cmSocial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo


class HiloAdapter(): RecyclerView.Adapter<HiloAdapter.HiloViewHolder>() {

    private var hilos: List<PayloadHilo> = emptyList()
    private lateinit var Listener : HiloAdapter.onItemClickListener
    private lateinit var context: Context

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: HiloAdapter.onItemClickListener) {
        Listener = listener
    }

    fun setData(list: List<PayloadHilo>) {
        hilos = list
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HiloAdapter.HiloViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hilo_cardview, parent, false)
        setContext(parent.context)
        return HiloAdapter.HiloViewHolder(view, Listener)
    }

    override fun onBindViewHolder(holder: HiloAdapter.HiloViewHolder, position: Int) {
        val hilo = hilos[position]
        holder.nickname.text = hilo.autorUuid
        holder.cuerpoMensaje.text = hilo.mensaje
        //holder.horaMensaje.text = hilo.dateCreation.toString()
        //Glide.with(context).load(article.urlFrontPage).into(holder.mensajePerfil)

        /*holder.title.text = article.title
        holder.description.text = article.description
        //imageview
        Glide.with(context).load(article.urlFrontPage).into(holder.image)
        //creation date*/
    }

    override fun getItemCount(): Int {
        return hilos.size
    }

    class HiloViewHolder(itemView: View, listener: HiloAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var nickname = itemView.findViewById<TextView>(R.id.nickname)
        var cuerpoMensaje = itemView.findViewById<TextView>(R.id.cuerpoMensaje)
        var horaMensaje = itemView.findViewById<TextView>(R.id.horaMensaje)
        var mensajePerfil = itemView.findViewById<ImageView>(R.id.mensajePerfil)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}