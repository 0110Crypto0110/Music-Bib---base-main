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

public class AddMusicaDialog extends JDialog {

    private final BibliotecaMusical repo;

    private final JTextField tituloField = new JTextField(20);
    private final JTextField artistaField = new JTextField(20);
    private final JTextField albumField = new JTextField(20);
    private final JTextField generoField = new JTextField(20);
    private final JTextField duracaoField = new JTextField(20);

    private final JButton btnSalvar = new JButton("Salvar Música");
    private final JButton btnCancelar = new JButton("Cancelar");

    public AddMusicaDialog(Frame owner, BibliotecaMusical repo) {
        super(owner, "Adicionar Nova Música", true);
        this.repo = repo;
        
        // --- Nomes para E2E Testing (Maior robustez) ---
        tituloField.setName("addTituloField");
        artistaField.setName("addArtistaField");
        albumField.setName("addAlbumField");
        generoField.setName("addGeneroField");
        duracaoField.setName("addDuracaoField");
        btnSalvar.setName("btnSalvarMusica");
        btnCancelar.setName("btnCancelarAdicao");
        // --- Fim dos Nomes ---

        buildGUI();
        setupActions();
        
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildGUI() {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

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

    private void setupActions() {
        btnSalvar.addActionListener(e -> attemptAddMusica());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void attemptAddMusica() {
        String titulo = tituloField.getText().trim();
        String artista = artistaField.getText().trim();
        String album = albumField.getText().trim();
        String genero = generoField.getText().trim();
        String duracaoStr = duracaoField.getText().trim();
        int duracao;

        // Validação da Duração
        try {
            duracao = Integer.parseInt(duracaoStr);
            if (duracao <= 0) {
                 JOptionPane.showMessageDialog(this, "A duração deve ser um número inteiro positivo.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "A duração deve ser um número inteiro válido.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validação Básica dos Campos
        if (titulo.isEmpty() || artista.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Título e Artista são campos obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Musica novaMusica = new Musica(titulo, artista, album, genero, duracao);
            boolean ok = repo.adicionarMusica(novaMusica);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Música adicionada com sucesso!\nID: " + novaMusica.getId(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao adicionar: Já existe uma música idêntica cadastrada.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}
