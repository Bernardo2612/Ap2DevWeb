package com.example.ap2_desenvolvimentoweb

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.ap2_desenvolvimentoweb.databinding.ActivityGenerosBinding
import com.example.ap2_desenvolvimentoweb.model.Recomendacao
import com.example.ap2_desenvolvimentoweb.model.RecomendacaoRequest
import com.example.ap2_desenvolvimentoweb.network.RetrofitClient
import com.example.ap2_desenvolvimentoweb.util.Extras
import com.example.ap2_desenvolvimentoweb.util.avisar
import com.example.ap2_desenvolvimentoweb.util.avisarFalhaDeConexao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerosBinding

    // Opções do Spinner e a duração máxima (minutos) que cada uma representa
    private val opcoesDuracao = listOf("Até 90 min", "Até 120 min", "Até 150 min", "Até 180 min", "Sem limite")
    private val valoresDuracao = listOf(90, 120, 150, 180, 240)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoesDuracao)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDuracao.adapter = adapter
        binding.spDuracao.setSelection(2)

        binding.btnDescobrir.setOnClickListener { enviar() }
    }

    private fun generosMarcados(): List<String> {
        val caixas = mapOf(
            binding.cbAcao to "Ação",
            binding.cbComedia to "Comédia",
            binding.cbDrama to "Drama",
            binding.cbFiccao to "Ficção Científica",
            binding.cbTerror to "Terror",
            binding.cbRomance to "Romance",
            binding.cbAnimacao to "Animação",
            binding.cbSuspense to "Suspense"
        )
        return caixas.filter { it.key.isChecked }.values.toList()
    }

    private fun enviar() {
        val generos = generosMarcados()
        if (generos.isEmpty()) {
            avisar("Marque pelo menos um gênero")
            return
        }

        val pedido = RecomendacaoRequest(
            nome = intent.getStringExtra(Extras.NOME).orEmpty(),
            humor = intent.getStringExtra(Extras.HUMOR).orEmpty(),
            generos = generos,
            aceitaClassicos = intent.getBooleanExtra(Extras.CLASSICOS, true),
            duracaoMax = valoresDuracao[binding.spDuracao.selectedItemPosition]
        )

        carregando(true)
        RetrofitClient.api.criar(pedido).enqueue(object : Callback<Recomendacao> {
            override fun onResponse(call: Call<Recomendacao>, response: Response<Recomendacao>) {
                carregando(false)
                val corpo = response.body()
                if (response.isSuccessful && corpo != null) {
                    startActivity(
                        Intent(this@GenerosActivity, ResultadoActivity::class.java)
                            .putExtra(Extras.ID, corpo.id)
                    )
                } else {
                    avisar("A API recusou o pedido (código ${response.code()})")
                }
            }

            override fun onFailure(call: Call<Recomendacao>, t: Throwable) {
                carregando(false)
                avisarFalhaDeConexao()
            }
        })
    }

    private fun carregando(ativo: Boolean) {
        binding.pbCarregando.visibility = if (ativo) View.VISIBLE else View.GONE
        binding.btnDescobrir.isEnabled = !ativo
    }
}