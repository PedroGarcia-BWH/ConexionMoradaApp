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

    fun addData(hilo: PayloadHilo) {
        hilos = hilos + hilo
        notifyDataSetChanged()
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
        holder.numberLike.text = hilo.likes.toString()
        holder.numberDislike.text = hilo.dislikes.toString()

        if(hilo.autorUuid.equals(Firebase.auth.currentUser!!.uid)) {
            holder.likeButton.isEnabled = false
            holder.dislikeButton.isEnabled= false
        }else{
            holder.likeButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {
                    if(hilo.disliked){
                        deleteDislike(hilo, holder)
                        holder.dislikeButton.isLiked = false
                    }
                    addLike(hilo, holder)
                }
                override fun unLiked(likeButton: LikeButton) {
                    deleteLike(hilo, holder)
                }
            })

            holder.dislikeButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {
                    if(hilo.liked){
                        deleteLike(hilo, holder)
                        holder.likeButton.isLiked = false
                    }
                    addDislike(hilo, holder)
                }
                override fun unLiked(likeButton: LikeButton) {
                    deleteDislike(hilo, holder)
                }
            })

            if(hilo.liked) holder.likeButton.isLiked = true
            if(hilo.disliked) holder.dislikeButton.isLiked = true
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
        var dislikeButton = itemView.findViewById<LikeButton>(R.id.unlike_button)
        var numberLike = itemView.findViewById<TextView>(R.id.numberLike)
        var numberDislike = itemView.findViewById<TextView>(R.id.numberDislike)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    private fun addLike(hilo: PayloadHilo, holder: HiloViewHolder){
        var payloadHilo = PayloadHilo(hilo.idHilo, Firebase.auth.currentUser!!.uid)
        var call = APIRetrofit().addLike(payloadHilo)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    holder.numberLike.text = (hilo.likes + 1).toString()
                }
            }
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                println("Error al añadir like")
            }
        })
    }

    private fun deleteLike(hilo: PayloadHilo, holder: HiloViewHolder){
        var call = APIRetrofit().deleteLike(hilo.idHilo, Firebase.auth.currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    holder.numberLike.text = (hilo.likes - 1).toString()
                }
            }
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                println("Error al eliminar like")
            }
        })
    }

    private fun addDislike(hilo: PayloadHilo, holder: HiloViewHolder){
        var payloadHilo = PayloadHilo(hilo.idHilo, Firebase.auth.currentUser!!.uid)
        var call = APIRetrofit().addDislike(payloadHilo)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    holder.numberDislike.text = (hilo.dislikes + 1).toString()
                }
            }
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                println("Error al añadir dislike")
            }
        })
    }

    private fun deleteDislike(hilo: PayloadHilo, holder: HiloViewHolder){
        var call = APIRetrofit().deleteDislike(hilo.idHilo, Firebase.auth.currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    holder.numberDislike.text = (hilo.dislikes - 1).toString()
                }
            }
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                println("Error al eliminar dislike")
            }
        })
    }

}