package app.base;

import model.Musica;
import repository.BibliotecaMusical;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo responsável por adicionar uma nova música à biblioteca.
 * Refatorado para herdar de {@link BaseDialog}, eliminando redundância estrutural.
 */
public class AddMusicaDialog extends BaseDialog {

    private final BibliotecaMusical repo;

    private final JTextField tituloField = new JTextField(20);
    private final JTextField artistaField = new JTextField(20);
    private final JTextField albumField = new JTextField(20);
    private final JTextField generoField = new JTextField(20);
    private final JTextField duracaoField = new JTextField(20);

    /**
     * Construtor padrão para o diálogo de adição de música.
     *
     * @param owner janela principal
     * @param repo  repositório de músicas
     */
    public AddMusicaDialog(Frame owner, BibliotecaMusical repo) {
        super(owner, "Adicionar Nova Música");
        this.repo = repo;

        // --- Identificadores para E2E Testing ---
        tituloField.setName("addTituloField");
        artistaField.setName("addArtistaField");
        albumField.setName("addAlbumField");
        generoField.setName("addGeneroField");
        duracaoField.setName("addDuracaoField");
        btnConfirmar.setName("btnSalvarMusica");
        btnCancelar.setName("btnCancelarAdicao");
        // ---------------------------------------

        buildForm();
        setupActions();
        finalizeDialog();
    }

    /**
     * Monta os campos do formulário principal.
     */
    @Override
    protected void buildForm() {
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));

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

    /**
     * Define as ações dos botões Confirmar e Cancelar.
     */
    private void setupActions() {
        btnConfirmar.setText("Salvar Música");
        btnConfirmar.addActionListener(e -> attemptAddMusica());
        // btnCancelar já definido em BaseDialog
    }

    /**
     * Valida os campos e tenta adicionar a nova música ao repositório.
     */
    private void attemptAddMusica() {
        String titulo = tituloField.getText().trim();
        String artista = artistaField.getText().trim();
        String album = albumField.getText().trim();
        String genero = generoField.getText().trim();
        String duracaoStr = duracaoField.getText().trim();
        int duracao;

        // --- Validação da Duração ---
        try {
            duracao = Integer.parseInt(duracaoStr);
            if (duracao <= 0) {
                showMessage("A duração deve ser um número inteiro positivo.",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("A duração deve ser um número inteiro válido.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Validação Básica ---
        if (titulo.isEmpty() || artista.isEmpty()) {
            showMessage("Título e Artista são campos obrigatórios.",
                    "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Musica novaMusica = new Musica(titulo, artista, album, genero, duracao);
            boolean ok = repo.adicionarMusica(novaMusica);

            if (ok) {
                showMessage("Música adicionada com sucesso!\nID: " + novaMusica.getId(),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showMessage("Falha ao adicionar: Já existe uma música idêntica cadastrada.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            showMessage("Erro: " + ex.getMessage(),
                    "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}
