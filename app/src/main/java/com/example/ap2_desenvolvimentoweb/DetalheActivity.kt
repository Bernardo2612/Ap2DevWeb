package com.example.ap2_desenvolvimentoweb

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ap2_desenvolvimentoweb.databinding.ActivityDetalheBinding
import com.example.ap2_desenvolvimentoweb.model.AtualizacaoRequest
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

class DetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheBinding
    private var recomendacao: Recomendacao? = null
    private var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getIntExtra(Extras.ID, -1)

        val notas = listOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, notas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spNota.adapter = adapter

        binding.btnSalvarNota.setOnClickListener { salvarNota() }
        binding.btnTrailer.setOnClickListener {
            recomendacao?.let { abrirTrailer(it.filmeTitulo) }
        }
        binding.btnCompartilhar.setOnClickListener {
            recomendacao?.let { compartilharFilme(it.filmeTitulo, it.filmeAno) }
        }
        binding.btnExcluir.setOnClickListener { confirmarExclusao() }

        carregar()
    }

    private fun carregar() {
        RetrofitClient.api.consultar(id).enqueue(object : Callback<Recomendacao> {
            override fun onResponse(call: Call<Recomendacao>, response: Response<Recomendacao>) {
                val corpo = response.body()
                if (response.isSuccessful && corpo != null) {
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
        recomendacao = r
        binding.tvTitulo.text = r.filmeTitulo
        binding.tvInfo.text = "${r.filmeAno} • ${r.filmeGenero} • ${r.filmeDuracao} min"
        binding.tvSinopse.text = r.filmeSinopse
        binding.tvPerfil.text = "Gerado para ${r.nome} (humor: ${r.humor}) com " +
                "${r.compatibilidade}% de compatibilidade."
        r.nota?.let { binding.spNota.setSelection(it - 1) }

        binding.pbCarregando.visibility = View.GONE
        binding.conteudo.visibility = View.VISIBLE
    }

    private fun salvarNota() {
        val nota = binding.spNota.selectedItemPosition + 1
        RetrofitClient.api.atualizar(id, AtualizacaoRequest(nota)).enqueue(object : Callback<Recomendacao> {
            override fun onResponse(call: Call<Recomendacao>, response: Response<Recomendacao>) {
                if (response.isSuccessful) {
                    avisar("Nota salva!")
                } else {
                    avisar("Não foi possível salvar a nota (código ${response.code()})")
                }
            }

            override fun onFailure(call: Call<Recomendacao>, t: Throwable) {
                avisarFalhaDeConexao()
            }
        })
    }

    private fun confirmarExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Excluir")
            .setMessage("Remover esta recomendação do histórico?")
            .setPositiveButton("Excluir") { _, _ -> excluir() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluir() {
        RetrofitClient.api.excluir(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    avisar("Recomendação removida")
                    finish()
                } else {
                    avisar("Não foi possível excluir (código ${response.code()})")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                avisarFalhaDeConexao()
            }
        })
    }
}