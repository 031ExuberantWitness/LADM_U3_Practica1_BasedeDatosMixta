package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityPantallaInicioBinding

class PantallaInicio : AppCompatActivity() {
    lateinit var binding: ActivityPantallaInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegistrar.setOnClickListener {
            val intent = Intent(binding.root.context, RegistroCandidato::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        binding.btnBuscar.setOnClickListener {
            val intent = Intent(binding.root.context, BuscarCandidato::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        binding.btnFirebase.setOnClickListener {
            val intent = Intent(binding.root.context, Firebase::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}