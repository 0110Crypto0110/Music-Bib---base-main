package model;

import java.util.UUID;

/**
 * Representa uma música dentro da biblioteca.
 * Todos os atributos são privados e acessados via getters/setters.
 * O ID é imutável e usado para identificação única.
 */
public class Musica {

    private final UUID id;
    private String titulo;
    private String artista;
    private String album;
    private String genero;
    private int duracaoSegundos;

    /** Construtor padrão: gera UUID automaticamente */
    public Musica(String titulo, String artista, String album, String genero, int duracaoSegundos) {
        this(UUID.randomUUID(), titulo, artista, album, genero, duracaoSegundos);
    }

    /** Construtor usado pela persistência (UUID fornecido) */
    public Musica(UUID id, String titulo, String artista, String album, String genero, int duracaoSegundos) {
        this.id = (id == null ? UUID.randomUUID() : id);
        setTitulo(titulo);
        setArtista(artista);
        setAlbum(album);
        setGenero(genero);
        setDuracaoSegundos(duracaoSegundos);
    }

    public UUID getId() { return id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank())
            throw new IllegalArgumentException("Título não pode ser vazio.");
        this.titulo = titulo.trim();
    }

    public String getArtista() { return artista; }
    public void setArtista(String artista) {
        if (artista == null || artista.isBlank())
            throw new IllegalArgumentException("Artista não pode ser vazio.");
        this.artista = artista.trim();
    }

    public String getAlbum() { return album; }
    public void setAlbum(String album) {
        this.album = (album == null ? "" : album.trim());
    }

    public String getGenero() { return genero; }
    public void setGenero(String genero) {
        this.genero = (genero == null ? "" : genero.trim());
    }

    public int getDuracaoSegundos() { return duracaoSegundos; }
    public void setDuracaoSegundos(int duracaoSegundos) {
        if (duracaoSegundos <= 0)
            throw new IllegalArgumentException("Duração deve ser maior que zero.");
        this.duracaoSegundos = duracaoSegundos;
    }

    public String toLinha() {
        return String.format("%s - %s (%s) [%ds]", titulo, artista, genero, duracaoSegundos);
    }

    @Override
    public String toString() { return toLinha(); }

    /** Duplicidade agora é verificada por título + artista + álbum (case-insensitive) */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Musica)) return false;
        Musica outra = (Musica) obj;
        return titulo.equalsIgnoreCase(outra.titulo)
            && artista.equalsIgnoreCase(outra.artista)
            && album.equalsIgnoreCase(outra.album);
    }

    @Override
    public int hashCode() {
        return (titulo.toLowerCase() + "|" + artista.toLowerCase() + "|" + album.toLowerCase()).hashCode();
    }
}
