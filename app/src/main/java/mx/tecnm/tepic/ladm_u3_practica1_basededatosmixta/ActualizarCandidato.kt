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
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityActualizarCandidatoBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ActualizarCandidato : AppCompatActivity() {
    lateinit var binding: ActivityActualizarCandidatoBinding
    val baseDatos = localBaseDatos(this, "ITTEPIC", null, 1)
    var id = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarCandidatoBinding.inflate(layoutInflater)
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

        id = intent.extras!!.getString("id")!!
        val cursor = baseDatos.readableDatabase.query(
            "CANDIDATOS", arrayOf("*"), "ID=?", arrayOf(id.toString()), null,
            null, null
        )

        if (cursor.moveToFirst()) {
            binding.txtNombre.setText(cursor.getString(1))
            binding.txtTelefono.setText(cursor.getString(3))
            binding.txtCorreo.setText(cursor.getString(6))
        } else {
            binding.txtNombre.setText("Error: No hay datos que recuperar")
            binding.txtTelefono.setText("Error: No hay datos que recuperar")
            binding.txtCorreo.setText("Error: No hay datos que recuperar")

            binding.txtNombre.isEnabled = false
            binding.txtEscuela.isEnabled = false
            binding.txtTelefono.isEnabled = false
            binding.txtCarrera1.isEnabled = false
            binding.txtCarrera2.isEnabled = false
            binding.txtCorreo.isEnabled = false
            binding.txtFecha.isEnabled = false
        }

        binding.btnActualizar.setOnClickListener {
            val res = actualizarPersona()
            if (res == 0) {
                AlertDialog.Builder(this).setMessage("Error: No se pudo actualizar").show()
            } else {
                Toast.makeText(this, "Alerta: Se actualizo con exito.", Toast.LENGTH_LONG).show()
                val intent = Intent(binding.root.context, BuscarCandidato::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish()
            }
        }

        binding.btnRegresar.setOnClickListener {
            val intent = Intent(binding.root.context, BuscarCandidato::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarPersona(): Int {
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
        return baseDatos.writableDatabase.update("CANDIDATOS", datos, "ID=?", arrayOf(id.toString()))
    }
}