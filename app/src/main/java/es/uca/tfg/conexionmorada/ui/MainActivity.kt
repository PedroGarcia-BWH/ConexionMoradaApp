package es.uca.tfg.conexionmorada.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.verify.VerifyDialog
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity(){

    private val LOCATION_SETTINGS_REQUEST_CODE = 123
    private val DELAY_TIME = 5000L // 5 segundos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUptabs()

        User.UserVerificado()?.get()?.addOnSuccessListener { document ->
            if (document.data != null) {
                val data_user = document.data
                if(data_user?.get("verificado") as Boolean != true) VerifyDialog(this).startDialog()

            } else {
                Log.d(ContentValues.TAG, "No such document")
            }
        }
    }

    private fun setUptabs() {
        val adapter = Controller(supportFragmentManager)
        adapter.addFragment(CMSocialFragment(), "")
        adapter.addFragment(HomeFragment(), "")
        adapter.addFragment(SistemaCompaneroFragment(), "")

        val viewpager = findViewById<ViewPager>(R.id.viewpager)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
        viewpager.setCurrentItem(1)

        tabs.getTabAt(0)?.setIcon(R.drawable.baseline_connect_without_contact_24) // Establecer icono para el primer tab
        tabs.getTabAt(1)?.setIcon(R.drawable.baseline_home_24) // Establecer icono para el segundo tab
        tabs.getTabAt(2)?.setIcon(R.drawable.baseline_social_distance_24) // Establecer icono para el tercer tab

        tabs.tabMode = TabLayout.MODE_FIXED // Establecer el modo de tabs fijo
        tabs.isInlineLabel = false // Ocultar los títulos de los tabs
    }


    fun recreateFragment(fragment : Fragment){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            supportFragmentManager.beginTransaction().detach(fragment).commitNow()
            supportFragmentManager.beginTransaction().attach(fragment).commitNow()
        }else{
            supportFragmentManager.beginTransaction().detach(fragment).attach(fragment).commitNow()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            Handler().postDelayed({
                restartActivity()
            }, DELAY_TIME)
        }
    }

    private fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}