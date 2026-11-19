package com.example.ap2_desenvolvimentoweb

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ap2_desenvolvimentoweb.adapter.RecomendacaoAdapter
import com.example.ap2_desenvolvimentoweb.databinding.ActivityHistoricoBinding
import com.example.ap2_desenvolvimentoweb.model.Recomendacao
import com.example.ap2_desenvolvimentoweb.network.RetrofitClient
import com.example.ap2_desenvolvimentoweb.util.Extras
import com.example.ap2_desenvolvimentoweb.util.avisar
import com.example.ap2_desenvolvimentoweb.util.avisarFalhaDeConexao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding
    private lateinit var adapter: RecomendacaoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecomendacaoAdapter { item ->
            startActivity(Intent(this, DetalheActivity::class.java).putExtra(Extras.ID, item.id))
        }
        binding.rvHistorico.layoutManager = LinearLayoutManager(this)
        binding.rvHistorico.adapter = adapter

        binding.btnNova.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    // Recarrega ao voltar da tela de detalhe (a nota pode ter mudado ou o item ter sido excluído)
    override fun onResume() {
        super.onResume()
        carregar()
    }

    private fun carregar() {
        binding.pbCarregando.visibility = View.VISIBLE
        binding.tvVazio.visibility = View.GONE

        RetrofitClient.api.listar().enqueue(object : Callback<List<Recomendacao>> {
            override fun onResponse(call: Call<List<Recomendacao>>, response: Response<List<Recomendacao>>) {
                binding.pbCarregando.visibility = View.GONE
                val lista = response.body()
                if (response.isSuccessful && lista != null) {
                    adapter.atualizar(lista)
                    binding.tvVazio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    avisar("Erro ao carregar o histórico (código ${response.code()})")
                }
            }

            override fun onFailure(call: Call<List<Recomendacao>>, t: Throwable) {
                binding.pbCarregando.visibility = View.GONE
                avisarFalhaDeConexao()
            }
        })
    }
}