package com.example.ap2_desenvolvimentoweb

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ap2_desenvolvimentoweb.databinding.ActivityPerfilBinding
import com.example.ap2_desenvolvimentoweb.util.Extras
import com.example.ap2_desenvolvimentoweb.util.avisar

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProximo.setOnClickListener { avancar() }
    }

    private fun avancar() {
        val nome = binding.etNome.text.toString().trim()
        if (nome.length < 2) {
            binding.etNome.error = "Informe seu nome"
            return
        }

        val humor = humorSelecionado()
        if (humor == null) {
            avisar("Escolha como você está se sentindo")
            return
        }

        val intent = Intent(this, GenerosActivity::class.java).apply {
            putExtra(Extras.NOME, nome)
            putExtra(Extras.HUMOR, humor)
            putExtra(Extras.CLASSICOS, binding.swClassicos.isChecked)
        }
        startActivity(intent)
    }

    private fun humorSelecionado(): String? = when (binding.rgHumor.checkedRadioButtonId) {
        R.id.rbFeliz -> "Feliz"
        R.id.rbTriste -> "Triste"
        R.id.rbEstressado -> "Estressado"
        R.id.rbEntediado -> "Entediado"
        else -> null
    }
}