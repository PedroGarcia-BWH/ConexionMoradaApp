package es.uca.tfg.conexionmorada.cmSocial.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.like.LikeButton
import com.like.OnLikeListener
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.activities.PerfilSocialActivity
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage


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

    fun clearData() {
        hilos = emptyList()
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
        var data = User.getUsername(hilo.autorUuid)
        data.addOnSuccessListener { document ->
            if (document != null) {
                holder.nickname.text = document.getString("username")
            }
        }
        holder.cuerpoMensaje.text = hilo.mensaje
        Storage().photoAccount(holder.mensajePerfil, hilo.autorUuid)
        holder.horaMensaje.text = hilo.dateCreation.toString()

        if(hilo.autorUuid.equals(Firebase.auth.currentUser!!.uid)) {
            holder.likeButton.visibility = View.GONE
        }else{
            holder.likeButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {
                    var payloadHilo = PayloadHilo(hilo.idHilo, Firebase.auth.currentUser!!.uid)
                    var call = APIRetrofit().addLike(payloadHilo)
                    call.enqueue(object : retrofit2.Callback<Void> {
                        override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                            if (response.isSuccessful) {
                                println("Like añadido")
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                            println("Error al añadir like")
                        }
                    })
                }
                override fun unLiked(likeButton: LikeButton) {
                    var call = APIRetrofit().deleteLike(hilo.idHilo, Firebase.auth.currentUser!!.uid)
                    call.enqueue(object : retrofit2.Callback<Void> {
                        override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                            if (response.isSuccessful) {
                                println("Like eliminado")
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                            println("Error al eliminar like")
                        }
                    })
                }
            })

            if(hilo.liked) holder.likeButton.isLiked = true
        }

        holder.nickname.setOnClickListener {
            var intent = Intent(context, PerfilSocialActivity::class.java)
            intent.putExtra("uuid", hilo.autorUuid)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return hilos.size
    }

    class HiloViewHolder(itemView: View, listener: HiloAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var nickname = itemView.findViewById<TextView>(R.id.nickname)
        var cuerpoMensaje = itemView.findViewById<TextView>(R.id.cuerpoMensaje)
        var horaMensaje = itemView.findViewById<TextView>(R.id.horaMensaje)
        var mensajePerfil = itemView.findViewById<ImageView>(R.id.Perfil)
        var likeButton = itemView.findViewById<LikeButton>(R.id.like_button)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}