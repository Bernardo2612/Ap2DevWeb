
import sqlite3

def conectar():
    conexao = sqlite3.connect("filmes.db")
    conexao.row_factory = sqlite3.Row
    conexao.execute("PRAGMA foreign_keys = ON")
    return conexao


def criar_tabelas():
    conexao = conectar()

    conexao.executescript("""
        CREATE TABLE IF NOT EXISTS usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL UNIQUE
        );

        CREATE TABLE IF NOT EXISTS atores (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            idade INTEGER,
            biografia TEXT
        );

        CREATE TABLE IF NOT EXISTS diretores (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            biografia TEXT
        );

        CREATE TABLE IF NOT EXISTS filmes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            titulo TEXT NOT NULL,
            ano INTEGER,
            genero TEXT,
            duracao INTEGER,
            sinopse TEXT,
            diretor_id INTEGER,
            FOREIGN KEY (diretor_id) REFERENCES diretores(id)
        );

        CREATE TABLE IF NOT EXISTS filme_atores (
            filme_id INTEGER,
            ator_id INTEGER,
            PRIMARY KEY (filme_id, ator_id),
            FOREIGN KEY (filme_id) REFERENCES filmes(id),
            FOREIGN KEY (ator_id) REFERENCES atores(id)
        );

        CREATE TABLE IF NOT EXISTS recomendacoes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario_id INTEGER NOT NULL,
            humor TEXT,
            generos TEXT,
            aceita_classicos INTEGER,
            duracao_max INTEGER,
            filme_id INTEGER,
            compatibilidade REAL,
            nota REAL,
            criado_em TEXT DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
            FOREIGN KEY (filme_id) REFERENCES filmes(id)
        );
    """)

    conexao.commit()
    conexao.close()
