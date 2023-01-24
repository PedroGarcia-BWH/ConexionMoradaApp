package es.uca.tfg.conexionmorada.articles.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.articles.model.Article
import android.content.Context
import android.widget.ImageView

class ArticleAdapter(param: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    private var articles: List<Article> = emptyList()
    private lateinit var Listener : onItemClickListener
    private lateinit var context: Context

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        Listener = listener
    }


    fun setData(list: List<Article>) {
        articles = list
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_cardview, parent, false)
        setContext(parent.context)
        return ArticleViewHolder(view, Listener)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.title
        holder.description.text = article.description
        //imageview
        Glide.with(context).load(article.urlFrontPage).into(holder.image)
        //creation date
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    class ArticleViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.title)
        val description = itemView.findViewById<TextView>(R.id.description)
        val image = itemView.findViewById<ImageView>(R.id.Portada)
       init {
               itemView.setOnClickListener {
                   listener.onItemClick(adapterPosition)
               }
       }
    }
}
