package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout; 
import java.util.List;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.Musica;
import model.Usuario;
import persistence.FileStorage;
import repository.BibliotecaMusical;
import repository.UsuarioRepository;

public class MainView extends JFrame {

    private final BibliotecaMusical repo = new BibliotecaMusical(new FileStorage());
    private final UsuarioRepository users = new UsuarioRepository();
    private Usuario currentUser = null;

    // Componentes da Interface
    private final JLabel statusLabel = new JLabel("Status: Desconectado");
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnRegister = new JButton("Registrar");
    private final JButton btnAdicionarMusica = new JButton("Adicionar Música");
    private final JButton btnListarMusicas = new JButton("Listar Todas");
    
    // Componentes para JTable e Busca
    private final MusicaTableModel tableModel = new MusicaTableModel();
    private final JTable musicaTable = new JTable(tableModel);
    private final JScrollPane scrollPane = new JScrollPane(musicaTable);
    private final JPanel contentPanel = new JPanel(new BorderLayout()); 
    private final JTextField searchField = new JTextField(15);
    private final JButton btnBuscar = new JButton("Buscar");

    public MainView() {
        super("Mini Biblioteca de Músicas - GUI");
        inicializarComponentes();
        configurarLayout();
        configurarAcoes();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void inicializarComponentes() {
        // --- Nomes para E2E Testing ---
        statusLabel.setName("statusLabel");
        btnLogin.setName("btnLogin");
        btnRegister.setName("btnRegister");
        btnAdicionarMusica.setName("btnAdicionarMusica");
        musicaTable.setName("musicaTable");
        searchField.setName("searchField");
        btnBuscar.setName("btnBuscar");
        // --- Fim dos Nomes ---
        
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        updateStatus(false);
        
        musicaTable.setFillsViewportHeight(true);
        musicaTable.setAutoCreateRowSorter(true);
    }

    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10)); 

        // 1. Painel Superior para Autenticação
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(statusLabel);
        topPanel.add(btnLogin);
        topPanel.add(btnRegister);
        add(topPanel, BorderLayout.NORTH);

        // 2. Painel Lateral/Menu para o CRUD de Músicas
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        menuPanel.setPreferredSize(new Dimension(200, 400));
        
        JPanel buttonColumn = new JPanel(new GridLayout(4, 1, 0, 10)); 
        buttonColumn.add(btnAdicionarMusica);
        buttonColumn.add(btnListarMusicas);
        
        menuPanel.add(buttonColumn);
        add(menuPanel, BorderLayout.WEST);

        // 3. Painel de Conteúdo Principal (Central - Tabela e Busca)
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar (Termo):"));
        searchPanel.add(searchField);
        searchPanel.add(btnBuscar);
        
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER); 
        add(contentPanel, BorderLayout.CENTER);
        
        // Adiciona o Listener do Menu de Contexto
        musicaTable.addMouseListener(new MusicaPopupListener(musicaTable, this));
    }
    
    private void configurarAcoes() {
        // Ações de Autenticação
        btnLogin.addActionListener(e -> {
            if (currentUser == null) {
                showLoginDialog();
            } else {
                performLogout();
            }
        });
        
        btnRegister.addActionListener(e -> showRegisterDialog());

        // Ações CRUD
        btnAdicionarMusica.addActionListener(e -> {
            if (currentUser != null) {
                showAddMusicaDialog();
            }
        });
        
        btnListarMusicas.addActionListener(e -> listarMusicas());
        btnBuscar.addActionListener(e -> performSearch());
        
        listarMusicas(); 
    }
    
    // ================== MÉTODOS DE AÇÃO / UTILITÁRIOS ==================

    // NOVO MÉTODO: Essencial para o MusicaPopupListener
    public Usuario getCurrentUser() {
        return currentUser;
    }

    // ---------------- Autenticação ----------------
    private void showLoginDialog() {
        LoginDialog dialog = new LoginDialog(this, users);
        dialog.setVisible(true);
        
        dialog.getAuthenticatedUser().ifPresent(user -> {
            currentUser = user;
            updateStatus(true);
            listarMusicas(); 
        });
    }

    private void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this, users);
        dialog.setVisible(true);
    }

    private void performLogout() {
        currentUser = null;
        updateStatus(false);
        JOptionPane.showMessageDialog(this, "Logout realizado com sucesso.", "Sessão Encerrada", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatus(boolean loggedIn) {
        if (loggedIn && currentUser != null) {
            statusLabel.setText("Status: Logado como " + currentUser.getNome());
            statusLabel.setForeground(Color.GREEN.darker());
            btnLogin.setText("Logout");
            btnRegister.setEnabled(false);
            btnAdicionarMusica.setEnabled(true);
        } else {
            statusLabel.setText("Status: Desconectado");
            statusLabel.setForeground(Color.BLUE);
            btnLogin.setText("Login");
            btnRegister.setEnabled(true);
            btnAdicionarMusica.setEnabled(false);
        }
    }
    
    // ---------------- CRUD ----------------
    private void showAddMusicaDialog() {
        AddMusicaDialog dialog = new AddMusicaDialog(this, repo);
        dialog.setVisible(true);
        listarMusicas(); 
    }

    public void showEditMusicaDialog(UUID id) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Ação restrita. Faça login primeiro.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        var optMusica = repo.buscarPorId(id);
        
        if (optMusica.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Música não encontrada. Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
            listarMusicas();
            return;
        }
        
        Musica musicaParaEditar = optMusica.get();
        
        EditMusicaDialog dialog = new EditMusicaDialog(this, repo, musicaParaEditar);
        dialog.setVisible(true);
        
        listarMusicas(); 
    }
    
    public void removeMusica(UUID id) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Ação restrita. Faça login primeiro.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja remover a música com ID: " + id + "?", 
            "Confirmar Remoção", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = repo.removerPorId(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Música removida com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma música encontrada com esse ID.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            listarMusicas();
        }
    }

    // ---------------- Listagem/Busca ----------------
    public void loadMusicas(List<Musica> musicas) {
        if (musicas == null || musicas.isEmpty()) {
            tableModel.setMusicas(List.of());
            scrollPane.setViewportView(new JLabel("Não há músicas cadastradas."));
        } else {
            tableModel.setMusicas(musicas);
            scrollPane.setViewportView(musicaTable);
        }
    }
    
    private void listarMusicas() {
        List<Musica> todasMusicas = repo.listarTodas();
        loadMusicas(todasMusicas);
    }
    
    private void performSearch() {
        String termo = searchField.getText().trim();
        List<Musica> resultado = List.of();
        
        if (termo.isEmpty()) {
            listarMusicas();
            return;
        }
        
        resultado = repo.buscarPorTitulo(termo);
        if (resultado.isEmpty()) {
            resultado = repo.buscarPorArtista(termo);
        }
        if (resultado.isEmpty()) {
            resultado = repo.buscarPorGenero(termo);
        }
        
        loadMusicas(resultado);
        
        if (resultado.isEmpty()) {
             JOptionPane.showMessageDialog(this, 
                 "Nenhum resultado encontrado para: " + termo, 
                 "Busca", 
                 JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainView::new);
    }
}
