package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityRegistroCandidatoBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class RegistroCandidato : AppCompatActivity() {
    lateinit var binding: ActivityRegistroCandidatoBinding
    val baseDatos = localBaseDatos(this, "ITTEPIC", null, 1)
    var IDs = ArrayList<Int>()



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroCandidatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spEscuela: Spinner = findViewById(R.id.txtEscuela)
        val spCarrera1: Spinner = findViewById(R.id.txtCarrera1)
        val spCarrera2: Spinner = findViewById(R.id.txtCarrera2)

        val adEscuela = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.preparatorias))
        val adCarrera = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.carreras))

        spEscuela.adapter = adEscuela
        spCarrera1.adapter = adCarrera
        spCarrera2.adapter = adCarrera

        binding.btnRegresar.setOnClickListener{
            val intent = Intent(binding.root.context, PantallaInicio::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish()
        }

        binding.btnRegistrar.setOnClickListener{
            if (hayCamposVacios()){
                Toast.makeText(this, "Error: No deje campos en blanco.", Toast.LENGTH_LONG).show()
            }else{
                val candidato = baseDatos.writableDatabase
                val datos = ContentValues()

                datos.put("NOMBRE", binding.txtNombre.text.toString())
                datos.put("PREPARATORIA", binding.txtEscuela.getSelectedItem().toString())
                datos.put("CELULAR", binding.txtTelefono.text.toString())
                datos.put("CARRERA1", binding.txtCarrera1.getSelectedItem().toString())
                datos.put("CARRERA2", binding.txtCarrera2.getSelectedItem().toString())
                datos.put("CORREO", binding.txtCorreo.text.toString())

                val current = LocalDateTime.now()

                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                var fechaHoy = "" + binding.txtFecha.year + "/" +
                        binding.txtFecha.month + "/" +
                        binding.txtFecha.dayOfMonth + " " + formatted

                datos.put("FECHA", fechaHoy.toString())

                val resultado = candidato.insert("CANDIDATOS", "ID", datos)
                if (resultado == -1L) {
                    AlertDialog.Builder(this).setTitle("Error").setMessage("No se pudo registrar al candidato").show()
                } else {
                    Toast.makeText(this, "Alerta: Se registro el candidato con exito", Toast.LENGTH_LONG).show()

                    val intent = Intent(binding.root.context, RegistroCandidato::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish()
                }
            }
        }
    }

    private fun hayCamposVacios(): Boolean{
        if (
            binding.txtNombre.text.toString() == "" ||
            binding.txtTelefono.text.toString() == "" ||
            binding.txtCorreo.text.toString() == ""
        ){
            return true
        }

        return false
    }
}