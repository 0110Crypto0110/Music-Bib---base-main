package app.base;

import model.Musica;
import repository.BibliotecaMusical;

import javax.swing.*;

import exception.RegraNegocioException;

import java.awt.*;

/**
 * Diálogo para edição de uma música existente.
 * <p>
 * Permite alterar os campos básicos da música, com validação simples e persistência via {@link BibliotecaMusical}.
 * </p>
 */
public class EditMusicaDialog extends BaseDialog {

    private final BibliotecaMusical repo;
    private final Musica musicaOriginal;

    // Campos de entrada
    private final JTextField tituloField = new JTextField(20);
    private final JTextField artistaField = new JTextField(20);
    private final JTextField albumField = new JTextField(20);
    private final JTextField generoField = new JTextField(20);
    private final JTextField duracaoField = new JTextField(20);

    public EditMusicaDialog(Frame owner, BibliotecaMusical repo, Musica musica) {
        super(owner, "Editar Música: " + musica.getTitulo());
        this.repo = repo;
        this.musicaOriginal = musica;

        // --- Nomes para E2E Testing ---
        tituloField.setName("editTituloField");
        artistaField.setName("editArtistaField");
        albumField.setName("editAlbumField");
        generoField.setName("editGeneroField");
        duracaoField.setName("editDuracaoField");
        btnConfirmar.setName("btnSalvarEdicao");
        btnCancelar.setName("btnCancelarEdicao");
        // ------------------------------

        buildForm();
        preloadData();
        setupActions();
        finalizeDialog();
    }

    @Override
    protected void buildForm() {
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("ID (Não Editável):"));
        formPanel.add(new JLabel(musicaOriginal.getId().toString()));

        formPanel.add(new JLabel("Título:"));
        formPanel.add(tituloField);

        formPanel.add(new JLabel("Artista:"));
        formPanel.add(artistaField);

        formPanel.add(new JLabel("Álbum:"));
        formPanel.add(albumField);

        formPanel.add(new JLabel("Gênero:"));
        formPanel.add(generoField);

        formPanel.add(new JLabel("Duração (segundos):"));
        formPanel.add(duracaoField);
    }

    private void preloadData() {
        tituloField.setText(musicaOriginal.getTitulo());
        artistaField.setText(musicaOriginal.getArtista());
        albumField.setText(musicaOriginal.getAlbum() != null ? musicaOriginal.getAlbum() : "");
        generoField.setText(musicaOriginal.getGenero() != null ? musicaOriginal.getGenero() : "");
        duracaoField.setText(String.valueOf(musicaOriginal.getDuracaoSegundos()));
    }

    private void setupActions() {
        btnConfirmar.addActionListener(e -> attemptEditMusica());
    }

    private void attemptEditMusica() {
        String novoTitulo = getStrValue(tituloField.getText());
        String novoArtista = getStrValue(artistaField.getText());
        String novoAlbum = getStrValue(albumField.getText());
        String novoGenero = getStrValue(generoField.getText());
        Integer novaDuracao = getIntValue(duracaoField.getText());

        if (novoTitulo == null || novoArtista == null) {
            showMessage("Título e Artista não podem ser vazios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (novaDuracao == null || novaDuracao <= 0) {
            showMessage("Duração inválida. Deve ser um número inteiro positivo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean ok = repo.editarMusica(
                musicaOriginal.getId(),
                novoTitulo,
                novoArtista,
                novoAlbum,
                novoGenero,
                novaDuracao
            );

            if (ok) {
                showMessage("Música editada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showMessage("Falha ao editar música. ID não encontrado ou erro de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RegraNegocioException ex) { 
            showMessage("Erro de Validação: " + ex.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
             showMessage("Erro inesperado: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Utilitários internos
    private String getStrValue(String s) {
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer getIntValue(String s) {
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return null;
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
