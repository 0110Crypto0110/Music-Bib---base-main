package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.Musica;

/**
 * Persistência simples em arquivo CSV (delimitador ';').
 * Suporta escapes: ';' -> '\;', '\' -> '\\', '\n' -> '\n'.
 */
public class FileStorage {

    private static final String CAMINHO_PADRAO =
            "C:\\Users\\Strange brick\\OneDrive\\Desktop\\Music-Bib---base-main\\mb.stb\\musicas.csv";

    private final Path arquivo;

    public FileStorage(Path arquivo) {
        this.arquivo = (arquivo != null) ? arquivo : Paths.get(CAMINHO_PADRAO);
        // REMOVIDO: criarArquivoSeNaoExistir();
        // A criação do arquivo só deve ocorrer no momento de SALVAR,
        // não na inicialização (o que quebrava o teste 'deveRetornarListaVaziaSeArquivoNaoExistir').
    }

    public FileStorage() {
        this(null);
    }

    private void criarArquivoSeNaoExistir() {
        try {
            if (arquivo.getParent() != null && !Files.exists(arquivo.getParent())) {
                Files.createDirectories(arquivo.getParent());
            }
            if (!Files.exists(arquivo)) {
                Files.createFile(arquivo);
                System.out.printf("[FileStorage] Criado novo arquivo CSV em: %s%n", arquivo.toAbsolutePath());
            } else {
                System.out.printf("[FileStorage] Arquivo CSV existente detectado em: %s%n", arquivo.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Falha ao criar arquivo ou diretório: " + e.getMessage());
        }
    }

    public List<Musica> carregar() {
        List<Musica> lista = new ArrayList<>();
        // Esta verificação agora funciona corretamente, pois o construtor não cria mais o arquivo.
        if (!Files.exists(arquivo) || arquivo.toFile().length() == 0) return lista;

        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (String ln : linhas) {
                if (ln == null || ln.isBlank()) continue;

                String[] parts = splitCsvLine(ln);
                if (parts.length < 6) {
                    System.err.println("Linha incompleta ignorada: " + ln);
                    continue;
                }

                try {
                    UUID id = UUID.fromString(parts[0]);
                    String titulo = unescape(parts[1]);
                    String artista = unescape(parts[2]);
                    String album = unescape(parts[3]);
                    String genero = unescape(parts[4]);
                    int duracao = Integer.parseInt(parts[5]);

                    lista.add(new Musica(id, titulo, artista, album, genero, duracao));
                } catch (Exception e) {
                    System.err.println("Linha inválida ignorada: " + ln + " -> " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Falha ao ler arquivo: " + e.getMessage());
        }

        return lista;
    }

    public void salvar(List<Musica> musicas) {
        if (musicas == null) return;

        // ADICIONADO: A verificação/criação do arquivo foi movida para cá.
        criarArquivoSeNaoExistir();

        try {
            List<String> linhas = new ArrayList<>();
            for (Musica m : musicas) {
                linhas.add(String.join(";",
                        m.getId().toString(),
                        escape(m.getTitulo()),
                        escape(m.getArtista()),
                        escape(m.getAlbum()),
                        escape(m.getGenero()),
                        String.valueOf(m.getDuracaoSegundos())
                ));
            }
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
            System.out.println("[FileStorage] Arquivo salvo com sucesso em: " + arquivo.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Falha ao salvar arquivo: " + e.getMessage());
        }
    }

    // ======== ESCAPE/UNESCAPE ========

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("\n", "\\n");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (esc) {
                switch (c) {
                    case 'n' -> out.append('\n'); // \n vira nova linha
                    case '\\' -> out.append('\\'); // \\ vira \
                    case ';' -> out.append(';');   // \; vira ;
                    default -> out.append(c);       // mantém qualquer outro
                }
                esc = false;
            } else if (c == '\\') {
                esc = true;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }


    // ======== SPLIT CSV ========

    private static String[] splitCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (esc) {
                cur.append(c);
                esc = false;
            } else if (c == '\\') {
                esc = true;
                // CORREÇÃO: A barra invertida (o caractere de escape)
                // deve ser mantida para ser processada pelo unescape().
                cur.append(c);
            } else if (c == ';') {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
