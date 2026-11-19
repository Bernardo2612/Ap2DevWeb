package com.example.ap2_desenvolvimentoweb

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ap2_desenvolvimentoweb.databinding.ActivityMainBinding
import com.example.ap2_desenvolvimentoweb.util.abrirEmailFeedback

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnComecar.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        binding.btnHistorico.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
        binding.btnFeedback.setOnClickListener { abrirEmailFeedback() }
    }
}