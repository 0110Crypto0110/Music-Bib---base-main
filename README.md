## 🎵 Mini Biblioteca de Músicas (Java)

Projeto acadêmico desenvolvido em Java para a disciplina Programação 2 (UFRPE).
O sistema permite gerenciar músicas de forma simples, aplicando conceitos de POO (Programação Orientada a Objetos),
persistência em arquivo e controle de acesso por usuário.

## 🧠 Visão Geral

A Mini Biblioteca de Músicas é um aplicativo de linha de comando (CLI) que possibilita:

👤 Cadastro e autenticação de usuários (nome, e-mail, senha)

🎶 Gerenciamento de músicas (adicionar, editar, remover, listar e buscar)

💾 Persistência local automática em arquivo CSV (as músicas permanecem após fechar o programa)

🔐 Política de acesso — apenas usuários logados podem adicionar, editar ou remover músicas

🧱 Estrutura modular com pacotes (model, repository, persistence, app)

## 🏗️ Estrutura do Projeto

```text
src/
├── app/
│   └── Main.java             # CLI e controle de autenticação
├── model/
│   ├── Musica.java           # Entidade música (UUID + atributos privados)
│   └── Usuario.java          # Entidade usuário
├── repository/
│   ├── BibliotecaMusical.java  # CRUD + integração com persistência
│   └── UsuarioRepository.java  # Cadastro e autenticação de usuários
└── persistence/
    └── FileStorage.java        # Persistência de músicas em arquivo CSV
```

## ⚙️ Tecnologias Utilizadas

| Tecnologia                      | Função                                        |
| ------------------------------- | --------------------------------------------- |
| ☕ **Java 17+**                  | Linguagem principal                           |
| 🧩 **POO**                      | Encapsulamento, construtores, getters/setters |
| 🧠 **ArrayList**                | Armazenamento dinâmico em memória             |
| 💾 **File I/O (java.nio.file)** | Persistência automática das músicas           |
| 🔑 **UUID**                     | Identificador único para cada música          |

## 🔐 Fluxo de Uso

### Ao iniciar o programa, escolha entre:

1 Registrar novo usuário

2 Fazer login

### Após logado, acesse o menu principal:

==== MINI BIBLIOTECA DE MÚSICAS ====
1 - Adicionar música
2 - Editar música
3 - Remover música
4 - Listar todas
5 - Buscar (título / artista / gênero)
0 - Sair

### Todas as músicas são salvas automaticamente em:

~/.mini-bib-musicas/musicas.csv

## 💾 Persistência

Cada música é armazenada como linha no arquivo musicas.csv.

O formato é delimitado por ; e escapa \n e ; automaticamente.

O sistema carrega os dados na inicialização e salva após cada modificação.
