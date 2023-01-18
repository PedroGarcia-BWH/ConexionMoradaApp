package es.uca.tfg.conexionmorada.articles.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.articles.model.Article

class ArticleAdapter(param: (Any) -> Unit) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    private var articles: List<Article> = emptyList()

    fun setData(list: List<Article>) {
        articles = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_cardview, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.title
        holder.description.text = article.description
        //imageview
        //creation date
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.title)
        val description = itemView.findViewById<TextView>(R.id.description)
    }
}