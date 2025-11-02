package app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Musica;
import repository.BibliotecaMusical;

public class EditMusicaDialog extends JDialog {

    private final BibliotecaMusical repo;
    private final Musica musicaOriginal;

    private final JTextField tituloField = new JTextField(20);
    private final JTextField artistaField = new JTextField(20);
    private final JTextField albumField = new JTextField(20);
    private final JTextField generoField = new JTextField(20);
    private final JTextField duracaoField = new JTextField(20);

    private final JButton btnSalvar = new JButton("Salvar Alterações");
    private final JButton btnCancelar = new JButton("Cancelar");

    public EditMusicaDialog(Frame owner, BibliotecaMusical repo, Musica musica) {
        super(owner, "Editar Música: " + musica.getTitulo(), true);
        this.repo = repo;
        this.musicaOriginal = musica;
        
        // --- Nomes para E2E Testing (Maior robustez) ---
        tituloField.setName("editTituloField");
        artistaField.setName("editArtistaField");
        albumField.setName("editAlbumField");
        generoField.setName("editGeneroField");
        duracaoField.setName("editDuracaoField");
        btnSalvar.setName("btnSalvarEdicao");
        btnCancelar.setName("btnCancelarEdicao");
        // --- Fim dos Nomes ---

        buildGUI();
        preloadData();
        setupActions();
        
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildGUI() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }
    
    private void preloadData() {
        tituloField.setText(musicaOriginal.getTitulo());
        artistaField.setText(musicaOriginal.getArtista());
        albumField.setText(musicaOriginal.getAlbum() != null ? musicaOriginal.getAlbum() : "");
        generoField.setText(musicaOriginal.getGenero() != null ? musicaOriginal.getGenero() : "");
        duracaoField.setText(String.valueOf(musicaOriginal.getDuracaoSegundos()));
    }

    private void setupActions() {
        btnSalvar.addActionListener(e -> attemptEditMusica());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void attemptEditMusica() {
        String novoTitulo = getStrValue(tituloField.getText());
        String novoArtista = getStrValue(artistaField.getText());
        String novoAlbum = getStrValue(albumField.getText());
        String novoGenero = getStrValue(generoField.getText());
        Integer novaDuracao = getIntValue(duracaoField.getText());

        if (novoTitulo == null || novoArtista == null) {
              JOptionPane.showMessageDialog(this, "Título e Artista não podem ser vazios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (novaDuracao == null || novaDuracao <= 0) {
              JOptionPane.showMessageDialog(this, "Duração inválida. Deve ser um número inteiro positivo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Música editada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao editar música. ID não encontrado ou erro de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
    
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
