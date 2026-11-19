package com.example.ap2_desenvolvimentoweb.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

// ---- Intents implícitas ----

/** Abre a busca de trailer no navegador (ACTION_VIEW). */
fun Context.abrirTrailer(titulo: String) {
    val url = "https://www.youtube.com/results?search_query=" + Uri.encode("$titulo trailer")
    abrir(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

/** Compartilha o filme recomendado por qualquer app de mensagens (ACTION_SEND). */
fun Context.compartilharFilme(titulo: String, ano: Int) {
    val envio = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Vou assistir $titulo ($ano)! Recomendado pelo CineMatch.")
    }
    abrir(Intent.createChooser(envio, "Compartilhar via"))
}

/** Abre o app de e-mail para enviar feedback (ACTION_SENDTO). */
fun Context.abrirEmailFeedback() {
    val email = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("seuemail@exemplo.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Feedback do CineMatch")
    }
    abrir(email)
}

// ---- Feedback ao usuário ----

fun Context.avisar(mensagem: String) {
    Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
}

fun Context.avisarFalhaDeConexao() {
    avisar("Não foi possível falar com a API. Ela está rodando?")
}

private fun Context.abrir(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        avisar("Nenhum aplicativo disponível para esta ação")
    }
}