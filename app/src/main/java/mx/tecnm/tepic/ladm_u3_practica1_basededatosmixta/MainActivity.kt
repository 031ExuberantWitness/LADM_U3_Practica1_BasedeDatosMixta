package mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import mx.tecnm.tepic.ladm_u3_practica1_basededatosmixta.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                println(1)
            }

            override fun onFinish() {
                val intent = Intent(binding.root.context, PantallaInicio::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
        timer.start()
    }
}

