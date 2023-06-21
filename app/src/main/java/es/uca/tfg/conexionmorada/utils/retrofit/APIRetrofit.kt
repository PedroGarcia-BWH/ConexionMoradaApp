package es.uca.tfg.conexionmorada.utils.retrofit

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.articles.interfaces.CRUDInterface
import es.uca.tfg.conexionmorada.articles.data.Article
import es.uca.tfg.conexionmorada.articles.data.PayloadArticle
import es.uca.tfg.conexionmorada.cmSocial.data.*
import es.uca.tfg.conexionmorada.cmSocial.interfaces.cmSocialInterface
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadChat
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadMensaje
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero
import es.uca.tfg.conexionmorada.sistemaCompanero.interfaces.SistemaCompaneroInterface
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername
import es.uca.tfg.conexionmorada.usernames.interfaces.UsernameInterface
import es.uca.tfg.conexionmorada.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIRetrofit {
    var retrofit: Retrofit

    constructor(context: Context){
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(BasicAuthInterceptor(context.getString(R.string.username), context.getString(R.string.password)))
        retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    fun getUsername(username : String): Call<Boolean>? {
        var crudInterface = retrofit.create(UsernameInterface::class.java)
        return crudInterface.existUsername(username)
    }

    fun addUsername(username : String) {
        var crudInterface = retrofit.create(UsernameInterface::class.java)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            var call = crudInterface.addUsername(PayloadUsername(Firebase.auth.currentUser!!.uid, username, token))

            if (call != null) {
                call.enqueue(object : Callback<Boolean> {

                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            Log.d("TAG", "onResponse: " + response.body())
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Log.d("TAG", "onFailure: " + t.message)
                    }
                })
            }
        })
    }

    fun deleteUsername(username : String): Call<Boolean>? {
        var crudInterface = retrofit.create(UsernameInterface::class.java)
        return crudInterface.deleteUsername(username)
    }

    fun updateToken(payloadUsername: PayloadUsername): Call<Boolean> {
        var crudInterface = retrofit.create(UsernameInterface::class.java)
        return crudInterface.updateToken(payloadUsername)
    }

    fun lastArticles(payloadArticle: PayloadArticle): Call<List<Article>>  {
        var crudInterface = retrofit.create(CRUDInterface::class.java)
        return crudInterface.lastArticles(payloadArticle)
    }

    fun searchArticles(query: String, numberArticles: Int, comunidad:String, city:String): Call<List<Article>> {
        var crudInterface = retrofit.create(CRUDInterface::class.java)
        return  crudInterface.query(query, numberArticles, comunidad, city)
    }

    fun addUserApp(uuid: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addUserApp(uuid)
    }

    fun getSeguidores(uuid: String): Call<Int> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return  crudInterface.getSeguidores(uuid)
    }

    fun getSeguidos(uuid: String): Call<Int> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return  crudInterface.getSeguidos(uuid)
    }

    fun addSeguidor(payloadSeguidores: PayloadSeguidores): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addSeguidor(payloadSeguidores)
    }

    fun deleteSeguidor(uuidSeguido: String, uuidSeguidor: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.deleteSeguidor(uuidSeguido, uuidSeguidor)
    }

    fun seguidorExist(uuidSeguidor: String, uuidSeguido: String): Call<Boolean> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getSeguidorExist(uuidSeguidor, uuidSeguido)
    }

    fun addHilo(payloadHilo: PayloadHilo) : Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addHilo(payloadHilo)
    }

    fun getLastHilos(uuid:String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getLastHilos(uuid)
    }

    fun getLastHilosSeguidos(uuid:String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getLastHilosSeguidos(uuid)
    }

    fun searchHilos(query: String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.searchHilos(query, Firebase.auth.currentUser!!.uid)
    }

    fun searchUsuarios(query: String): Call<List<PayloadUsername>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.searchUsuarios(query)
    }

    fun getHilosUser(uuid: String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getHilosUser(uuid)
    }

    fun getRespuestas(idHilo: String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getRespuestas(idHilo, Firebase.auth.currentUser!!.uid)
    }

    fun addLike(payloadHilo: PayloadHilo): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addLike(payloadHilo)
    }

    fun deleteLike(idHilo: String, uuid: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.deleteLike(idHilo, uuid)
    }

    fun addDislike(payloadHilo: PayloadHilo): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addDislike(payloadHilo)
    }

    fun deleteDislike(idHilo: String, uuid: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.deleteDislike(idHilo, uuid)
    }

    fun getLike(idHilo: String, uuid: String): Call<Boolean> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getLike(idHilo, uuid)
    }

    fun getDislike(idHilo: String, uuid: String): Call<Boolean> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getDislike(idHilo, uuid)
    }

    fun getNotificationsHilo(uuid: String): Call<List<PayloadNotificationHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getNotificationsHilo(uuid)
    }

    fun getNotificationsPersona(uuid: String): Call<List<PayloadNotificationPersona>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getNotificationsPersona(uuid)
    }

    fun deleteNotifications(uuid: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.deleteNotifications(uuid)
    }

    fun addReporte(payloadReporte: PayloadReporte): Call<Boolean> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addReporte(payloadReporte)
    }


    fun getAllPuntoCompaneroActive(): Call<List<PayloadPuntoCompanero>> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.allPointsActive
    }

    fun addPuntoCompanero(payloadPuntoCompanero: PayloadPuntoCompanero): Call<Void> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.addPuntoCompanero(payloadPuntoCompanero)
    }

    fun aceptPuntoCompanero(id:String, uuid: String): Call<Boolean>{
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.aceptPuntoCompanero(id, uuid)
    }

    fun getAllPuntoCompaneroByUuid(uuid: String): Call<List<PayloadPuntoCompanero>> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.getAllPointsByUuid(uuid)
    }

    fun getPuntoCompaneroById(id: String): Call<PayloadPuntoCompanero> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.getPointById(id)
    }

    fun getAllChatsByUuid(uuid: String): Call<List<PayloadChat>> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.getAllChatsByUuid(uuid)
    }

    fun checkMensajesChat(id: String, uuid: String): Call<Void> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.checkMensajesChat(id, uuid)
    }
    fun getAllMensajesChat(id: String): Call<List<PayloadMensaje>> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.getAllMensajesByChatId(id)
    }

    fun addMensajeChat(payloadMensaje: PayloadMensaje): Call<Boolean> {
        var crudInterface = retrofit.create(SistemaCompaneroInterface::class.java)
        return crudInterface.addMensaje(payloadMensaje)
    }
}