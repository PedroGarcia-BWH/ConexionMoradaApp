package es.uca.tfg.conexionmorada.cmSocial.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadReporte
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        Utils.exit(this, findViewById(R.id.Exit))

        var mensajeUuid = intent.getStringExtra("mensajeUuid").toString()
        var autorUuid = intent.getStringExtra("autorUuid").toString()


        var rbOther = findViewById<RadioButton>(R.id.rbOther)
        var otherMensajeLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.otherMensajeLayout)
        var sendReport = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAcept)
        var motivoSelect = findViewById<RadioGroup>(R.id.motivoSelect)

        rbOther.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                otherMensajeLayout.visibility = View.VISIBLE
            } else {
                otherMensajeLayout .visibility = View.INVISIBLE
            }
        }

        sendReport.setOnClickListener {
            val selectedId = motivoSelect.checkedRadioButtonId
            if (selectedId == -1) {
                // No se ha seleccionado ningún botón
                Toast.makeText(this, "Por favor seleccione un motivo", Toast.LENGTH_SHORT).show()
            } else {
                val radioButton = findViewById<RadioButton>(selectedId)
                if (radioButton == rbOther && otherMensajeLayout.editText?.text.toString().isNotEmpty() || radioButton != rbOther) {
                    var payloadReporte = PayloadReporte(autorUuid, Firebase.auth.currentUser?.uid.toString(), radioButton.text.toString(), otherMensajeLayout.editText?.text.toString(), mensajeUuid)
                    var call = APIRetrofit(this).addReporte(payloadReporte)
                    call.enqueue(object : Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if(response.isSuccessful){
                                if(response.body() == true){
                                    reportDialog()
                                }else{
                                   reportDialogError()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Toast.makeText(this@ReportActivity, "No se pudo procesar su petición, por favor inténtelo de nuevo", Toast.LENGTH_SHORT).show()
                        }
                    })

                }else {
                    // El botón seleccionado es "Otros" pero el campo de texto está vacío
                    Toast.makeText(this, "Por favor especifique el motivo", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun reportDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Envío de Reporte")
            .setMessage("Reporte enviado correctamente, muchas gracias por contribuir en la comunidad de Conexión Morada, recibirá un correo en las próximas 48 " +
                    "horas con la resolución de su reporte. Muchas gracias.")
            .setPositiveButton("Cerrar") { dialog, _ ->
                // Closes the current activity
                finish()
            }
            .create()
        dialog.show()
    }

    private fun reportDialogError() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Envío de Reporte")
            .setMessage("Ya existe un reporte sobre este hilo realizado por usted, por favor espere a que nuestro equipo resuelva el reporte.")
            .setPositiveButton("Cerrar") { dialog, _ ->
                // Closes the current activity
                finish()
            }
            .create()
        dialog.show()
    }
}