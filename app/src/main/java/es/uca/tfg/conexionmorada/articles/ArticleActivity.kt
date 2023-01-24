package es.uca.tfg.conexionmorada.articles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import es.uca.tfg.conexionmorada.R
import java.util.Date

class ArticleActivity : AppCompatActivity() {
    private lateinit var title : String
    private lateinit var description : String
    private lateinit var body : String
    private lateinit var date : String
    private lateinit var category: String
    private lateinit var urlImage : String
    private lateinit var creationDate: Date
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        title = intent.getStringExtra("title").toString()
        description = intent.getStringExtra("description").toString()
        body = intent.getStringExtra("body").toString()
        date = intent.getStringExtra("date").toString()
        category = intent.getStringExtra("category").toString()
        urlImage = intent.getStringExtra("urlImg").toString()
        creationDate = intent.getSerializableExtra("creationDate") as Date

        setData()

    }

    fun setData() {
        var regTitle = findViewById<TextView>(R.id.tituloArticulo)
        var regDescription = findViewById<TextView>(R.id.subTituloArticulo)
        var regBody = findViewById<TextView>(R.id.cuerpoArticulo)
        var regImagen = findViewById<ImageView>(R.id.imagenArticulo)
        var regDate = findViewById<TextView>(R.id.creationDate)

        regTitle.text = title
        regDescription.text = description
        regBody.text = body
        Glide.with(this).load(urlImage).into(regImagen)
        regDate.text = creationDate.toString()

    }
}