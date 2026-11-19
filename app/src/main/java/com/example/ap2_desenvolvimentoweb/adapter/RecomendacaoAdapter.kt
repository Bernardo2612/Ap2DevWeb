package com.example.ap2_desenvolvimentoweb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ap2_desenvolvimentoweb.databinding.ItemRecomendacaoBinding
import com.example.ap2_desenvolvimentoweb.model.Recomendacao

class RecomendacaoAdapter(
    private val onClick: (Recomendacao) -> Unit
) : RecyclerView.Adapter<RecomendacaoAdapter.ViewHolder>() {

    private val itens = mutableListOf<Recomendacao>()

    fun atualizar(novos: List<Recomendacao>) {
        itens.clear()
        itens.addAll(novos)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemRecomendacaoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecomendacaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = itens.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itens[position]
        val data = item.criadoEm?.take(10) ?: ""
        with(holder.binding) {
            tvFilme.text = "${item.filmeTitulo} (${item.filmeAno})"
            tvDetalhes.text = "Para ${item.nome} • ${item.humor} • $data"
            tvNota.text = item.nota?.let { "Sua nota: $it/5" } ?: "Ainda sem nota"
            root.setOnClickListener { onClick(item) }
        }
    }
}