package es.uca.tfg.conexionmorada.articles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import es.uca.tfg.conexionmorada.R

class ArticleActivity : AppCompatActivity() {
    private lateinit var title : String
    private lateinit var description : String
    private lateinit var body : String
    private lateinit var date : String
    private lateinit var category: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        title = intent.getStringExtra("title").toString()
        description = intent.getStringExtra("description").toString()
        body = intent.getStringExtra("body").toString()
        date = intent.getStringExtra("date").toString()
        category = intent.getStringExtra("category").toString()

    }

    fun setData() {
        var regTitle = findViewById<TextView>(R.id.tituloArticulo)
        var regDescription = findViewById<TextView>(R.id.subTituloArticulo)
        var regBody = findViewById<TextView>(R.id.cuerpoArticulo)

        regTitle.text = title
        regDescription.text = description
        regBody.text = body

    }
}