
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
import json

from database import criar_tabelas, conectar

app = FastAPI(title="Backend Filmes")

criar_tabelas()


# MODELOS

class Usuario(BaseModel):
    nome: str


class Ator(BaseModel):
    nome: str
    idade: Optional[int] = None
    biografia: Optional[str] = None


class Diretor(BaseModel):
    nome: str
    biografia: Optional[str] = None


class Filme(BaseModel):
    titulo: str
    ano: int
    genero: str
    duracao: int
    sinopse: str
    diretor_id: Optional[int] = None
    atores_ids: list[int] = []


class RecomendacaoRequest(BaseModel):
    nome: str
    humor: str
    generos: list[str]
    aceita_classicos: bool
    duracao_max: int


class AtualizacaoRequest(BaseModel):
    nota: Optional[int] = None


# INÍCIO

@app.get("/")
def inicio():
    return {"mensagem": "Backend de filmes funcionando!"}


# USUÁRIOS

@app.post("/usuarios")
def cadastrar_usuario(usuario: Usuario):
    conexao = conectar()
    try:
        cursor = conexao.execute(
            "INSERT INTO usuarios (nome) VALUES (?)",
            (usuario.nome,)
        )
        conexao.commit()
        return {"id": cursor.lastrowid, "nome": usuario.nome}
    except Exception:
        raise HTTPException(400, "Usuário já cadastrado ou dados inválidos.")
    finally:
        conexao.close()


@app.get("/usuarios")
def listar_usuarios():
    conexao = conectar()
    dados = conexao.execute("SELECT * FROM usuarios").fetchall()
    conexao.close()
    return [dict(x) for x in dados]


# ATORES

@app.post("/atores")
def cadastrar_ator(ator: Ator):
    conexao = conectar()
    cursor = conexao.execute(
        "INSERT INTO atores (nome, idade, biografia) VALUES (?, ?, ?)",
        (ator.nome, ator.idade, ator.biografia)
    )
    conexao.commit()
    resultado = {"id": cursor.lastrowid, **ator.model_dump()}
    conexao.close()
    return resultado


@app.get("/atores")
def listar_atores():
    conexao = conectar()
    dados = conexao.execute("SELECT * FROM atores").fetchall()
    conexao.close()
    return [dict(x) for x in dados]


# DIRETORES

@app.post("/diretores")
def cadastrar_diretor(diretor: Diretor):
    conexao = conectar()
    cursor = conexao.execute(
        "INSERT INTO diretores (nome, biografia) VALUES (?, ?)",
        (diretor.nome, diretor.biografia)
    )
    conexao.commit()
    resultado = {"id": cursor.lastrowid, **diretor.model_dump()}
    conexao.close()
    return resultado


@app.get("/diretores")
def listar_diretores():
    conexao = conectar()
    dados = conexao.execute("SELECT * FROM diretores").fetchall()
    conexao.close()
    return [dict(x) for x in dados]


# FILMES

@app.post("/filmes")
def cadastrar_filme(filme: Filme):
    conexao = conectar()
    try:
        cursor = conexao.execute("""
            INSERT INTO filmes
            (titulo, ano, genero, duracao, sinopse, diretor_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """, (
            filme.titulo, filme.ano, filme.genero,
            filme.duracao, filme.sinopse, filme.diretor_id
        ))

        filme_id = cursor.lastrowid

        for ator_id in filme.atores_ids:
            conexao.execute(
                "INSERT INTO filme_atores (filme_id, ator_id) VALUES (?, ?)",
                (filme_id, ator_id)
            )

        conexao.commit()
        return {"id": filme_id, **filme.model_dump()}

    except Exception:
        raise HTTPException(400, "Não foi possível cadastrar o filme.")
    finally:
        conexao.close()


@app.get("/filmes")
def listar_filmes():
    conexao = conectar()
    dados = conexao.execute("""
        SELECT f.*, d.nome AS diretor
        FROM filmes f
        LEFT JOIN diretores d ON f.diretor_id = d.id
    """).fetchall()
    conexao.close()
    return [dict(x) for x in dados]


@app.get("/filmes/{filme_id}")
def consultar_filme(filme_id: int):
    conexao = conectar()
    filme = conexao.execute(
        "SELECT * FROM filmes WHERE id = ?", (filme_id,)
    ).fetchone()
    conexao.close()

    if not filme:
        raise HTTPException(404, "Filme não encontrado.")

    return dict(filme)


# RECOMENDAÇÕES

@app.post("/recomendacoes")
def criar_recomendacao(pedido: RecomendacaoRequest):
    conexao = conectar()

    usuario = conexao.execute(
        "SELECT id FROM usuarios WHERE nome = ?", (pedido.nome,)
    ).fetchone()

    if usuario:
        usuario_id = usuario["id"]
    else:
        cursor = conexao.execute(
            "INSERT INTO usuarios (nome) VALUES (?)", (pedido.nome,)
        )
        usuario_id = cursor.lastrowid

    filmes = conexao.execute("SELECT * FROM filmes").fetchall()

    melhor_filme = None
    melhor_pontuacao = -1

    for filme in filmes:
        pontuacao = 0

        if filme["genero"].lower() in [g.lower() for g in pedido.generos]:
            pontuacao += 60

        if filme["duracao"] <= pedido.duracao_max:
            pontuacao += 30

        if pedido.aceita_classicos or filme["ano"] >= 2000:
            pontuacao += 10

        if pontuacao > melhor_pontuacao:
            melhor_pontuacao = pontuacao
            melhor_filme = filme

    if not melhor_filme:
        conexao.close()
        raise HTTPException(404, "Nenhum filme cadastrado no catálogo.")

    compatibilidade = melhor_pontuacao

    cursor = conexao.execute("""
        INSERT INTO recomendacoes
        (usuario_id, humor, generos, aceita_classicos,
         duracao_max, filme_id, compatibilidade)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """, (
        usuario_id, pedido.humor, json.dumps(pedido.generos),
        pedido.aceita_classicos, pedido.duracao_max,
        melhor_filme["id"], compatibilidade
    ))

    conexao.commit()
    recomendacao_id = cursor.lastrowid

    resultado = {
        "id": recomendacao_id,
        "nome": pedido.nome,
        "humor": pedido.humor,
        "generos": pedido.generos,
        "aceita_classicos": pedido.aceita_classicos,
        "duracao_max": pedido.duracao_max,
        "filme_titulo": melhor_filme["titulo"],
        "filme_ano": melhor_filme["ano"],
        "filme_genero": melhor_filme["genero"],
        "filme_duracao": melhor_filme["duracao"],
        "filme_sinopse": melhor_filme["sinopse"],
        "compatibilidade": compatibilidade,
        "nota": None,
        "criado_em": None
    }

    conexao.close()
    return resultado


@app.get("/recomendacoes")
def listar_recomendacoes():
    conexao = conectar()
    dados = conexao.execute("""
        SELECT r.*, u.nome, f.titulo AS filme_titulo,
               f.ano AS filme_ano, f.genero AS filme_genero,
               f.duracao AS filme_duracao,
               f.sinopse AS filme_sinopse
        FROM recomendacoes r
        JOIN usuarios u ON r.usuario_id = u.id
        JOIN filmes f ON r.filme_id = f.id
        ORDER BY r.id DESC
    """).fetchall()

    resultado = []
    for x in dados:
        item = dict(x)
        item["generos"] = json.loads(item["generos"])
        item["aceita_classicos"] = bool(item["aceita_classicos"])
        resultado.append(item)

    conexao.close()
    return resultado


@app.get("/recomendacoes/{recomendacao_id}")
def consultar_recomendacao(recomendacao_id: int):
    dados = listar_recomendacoes()
    for item in dados:
        if item["id"] == recomendacao_id:
            return item
    raise HTTPException(404, "Recomendação não encontrada.")


@app.put("/recomendacoes/{recomendacao_id}")
def atualizar_recomendacao(
    recomendacao_id: int,
    atualizacao: AtualizacaoRequest
):
    conexao = conectar()
    cursor = conexao.execute(
        "UPDATE recomendacoes SET nota = ? WHERE id = ?",
        (atualizacao.nota, recomendacao_id)
    )
    conexao.commit()
    conexao.close()

    if cursor.rowcount == 0:
        raise HTTPException(404, "Recomendação não encontrada.")

    return consultar_recomendacao(recomendacao_id)


@app.delete("/recomendacoes/{recomendacao_id}")
def excluir_recomendacao(recomendacao_id: int):
    conexao = conectar()
    cursor = conexao.execute(
        "DELETE FROM recomendacoes WHERE id = ?",
        (recomendacao_id,)
    )
    conexao.commit()
    conexao.close()

    if cursor.rowcount == 0:
        raise HTTPException(404, "Recomendação não encontrada.")

    return {"mensagem": "Recomendação excluída."}
