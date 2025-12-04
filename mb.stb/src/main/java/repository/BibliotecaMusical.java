package repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import model.Musica;
import persistence.FileStorage;

/**
 * Repositório em memória com persistência em arquivo.
 * Totalmente testável: aceita FileStorage mockado.
 */
public class BibliotecaMusical {

    private final List<Musica> listaMusicas;
    private final FileStorage storage;

    /** Construtor padrão — cria FileStorage com caminho padrão. */
    public BibliotecaMusical(Path arquivo) {
        this(new FileStorage(arquivo), true);
    }

    /** Construtor principal — aceita FileStorage injetado (para testes). */
    public BibliotecaMusical(FileStorage storage) {
        this(storage, true);
    }

    /**
     * Construtor interno que permite desativar la criação de músicas de exemplo.
     * Útil para testes unitários.
     */
    public BibliotecaMusical(FileStorage storage, boolean carregarExemplosSeVazio) {
        this.storage = storage;
        List<Musica> carregadas = storage.carregar();

        if (carregarExemplosSeVazio && carregadas.isEmpty()) {
            carregadas = criarMusicasExemplo();
            storage.salvar(carregadas);
        }

        this.listaMusicas = new ArrayList<>(carregadas);
    }

    private List<Musica> criarMusicasExemplo() {
        List<Musica> exemplo = new ArrayList<>();
        exemplo.add(new Musica(UUID.randomUUID(), "Imagine", "John Lennon", "Imagine", "Pop", 183));
        exemplo.add(new Musica(UUID.randomUUID(), "Billie Jean", "Michael Jackson", "Thriller", "Pop", 294));
        exemplo.add(new Musica(UUID.randomUUID(), "Smells Like Teen Spirit", "Nirvana", "Nevermind", "Rock", 301));
        return exemplo;
    }

    // ======================== Operações ========================

    public boolean adicionarMusica(Musica m) {
        if (m == null || existeDuplicada(m)) return false;
        listaMusicas.add(m);
        storage.salvar(listaMusicas);
        return true;
    }

    public boolean editarMusica(UUID id, String novoTitulo, String novoArtista,
                                String novoAlbum, String novoGenero, Integer novaDuracao) {
        Optional<Musica> opt = buscarPorId(id);
        if (opt.isEmpty()) return false;
        Musica alvo = opt.get();

        if (novoTitulo != null) alvo.setTitulo(novoTitulo);
        if (novoArtista != null) alvo.setArtista(novoArtista);
        if (novoAlbum != null) alvo.setAlbum(novoAlbum);
        if (novoGenero != null) alvo.setGenero(novoGenero);
        if (novaDuracao != null) alvo.setDuracaoSegundos(novaDuracao);

        storage.salvar(listaMusicas);
        return true;
    }

    public boolean removerPorId(UUID id) {
        boolean ok = listaMusicas.removeIf(m -> m.getId().equals(id));
        if (ok) storage.salvar(listaMusicas);
        return ok;
    }

    public Optional<Musica> buscarPorId(UUID id) {
        if (id == null) return Optional.empty();
        return listaMusicas.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public List<Musica> buscarPorTitulo(String termo) {
        String t = termo == null ? "" : termo.toLowerCase();
        List<Musica> resultado = new ArrayList<>();
        for (Musica m : listaMusicas) {
            if (m.getTitulo() != null && m.getTitulo().toLowerCase().contains(t)) resultado.add(m);
        }
        return resultado;
    }

    public List<Musica> buscarPorArtista(String termo) {
        String t = termo == null ? "" : termo.toLowerCase();
        List<Musica> resultado = new ArrayList<>();
        for (Musica m : listaMusicas) {
            if (m.getArtista() != null && m.getArtista().toLowerCase().contains(t)) resultado.add(m);
        }
        return resultado;
    }

    public List<Musica> buscarPorGenero(String termo) {
        String t = termo == null ? "" : termo.toLowerCase();
        List<Musica> resultado = new ArrayList<>();
        for (Musica m : listaMusicas) {
            if (m.getGenero() != null && m.getGenero().toLowerCase().contains(t)) resultado.add(m);
        }
        return resultado;
    }

    public List<Musica> listarTodas() {
        return Collections.unmodifiableList(listaMusicas);
    }

    /** Verifica duplicadas por conteúdo, não apenas referência */
    public boolean existeDuplicada(Musica nova) {
        if (nova == null) return false;
        for (Musica m : listaMusicas) {
            if (m.equals(nova)) return true;
        }
        return false;
    }

    public int tamanho() {
        return listaMusicas.size();
    }
}

