package com.example.ap2_desenvolvimentoweb

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ap2_desenvolvimentoweb.databinding.ActivityResultadoBinding
import com.example.ap2_desenvolvimentoweb.model.Recomendacao
import com.example.ap2_desenvolvimentoweb.network.RetrofitClient
import com.example.ap2_desenvolvimentoweb.util.Extras
import com.example.ap2_desenvolvimentoweb.util.abrirTrailer
import com.example.ap2_desenvolvimentoweb.util.avisar
import com.example.ap2_desenvolvimentoweb.util.avisarFalhaDeConexao
import com.example.ap2_desenvolvimentoweb.util.compartilharFilme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadoBinding
    private var recomendacao: Recomendacao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTrailer.setOnClickListener {
            recomendacao?.let { abrirTrailer(it.filmeTitulo) }
        }
        binding.btnCompartilhar.setOnClickListener {
            recomendacao?.let { compartilharFilme(it.filmeTitulo, it.filmeAno) }
        }
        binding.btnHistorico.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
        binding.btnInicio.setOnClickListener {
            val inicio = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(inicio)
            finish()
        }

        carregar(intent.getIntExtra(Extras.ID, -1))
    }

    private fun carregar(id: Int) {
        RetrofitClient.api.consultar(id).enqueue(object : Callback<Recomendacao> {
            override fun onResponse(call: Call<Recomendacao>, response: Response<Recomendacao>) {
                val corpo = response.body()
                if (response.isSuccessful && corpo != null) {
                    recomendacao = corpo
                    exibir(corpo)
                } else {
                    binding.pbCarregando.visibility = View.GONE
                    avisar("Recomendação não encontrada")
                }
            }

            override fun onFailure(call: Call<Recomendacao>, t: Throwable) {
                binding.pbCarregando.visibility = View.GONE
                avisarFalhaDeConexao()
            }
        })
    }

    private fun exibir(r: Recomendacao) {
        binding.tvSaudacao.text = "Pronto, ${r.nome}! Sua recomendação:"
        binding.tvTitulo.text = r.filmeTitulo
        binding.tvInfo.text = "${r.filmeAno} • ${r.filmeGenero} • ${r.filmeDuracao} min"
        binding.tvSinopse.text = r.filmeSinopse
        binding.tvCompat.text = "Compatibilidade: ${r.compatibilidade}%"
        binding.pbCompat.progress = r.compatibilidade

        binding.pbCarregando.visibility = View.GONE
        binding.conteudo.visibility = View.VISIBLE
    }
}