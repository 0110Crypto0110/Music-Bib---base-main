package app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Usuario;
import repository.UsuarioRepository;

public class LoginDialog extends JDialog {

    private final UsuarioRepository users;
    private Usuario authenticatedUser = null;

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnCancel = new JButton("Cancelar");

    public LoginDialog(Frame owner, UsuarioRepository users) {
        super(owner, "Fazer Login", true);
        this.users = users;

        // --- Nomes para E2E Testing (Maior robustez) ---
        emailField.setName("emailField");
        passwordField.setName("passwordField");
        btnLogin.setName("btnLoginDialog");
        btnCancel.setName("btnCancelDialog");
        // --- Fim dos Nomes ---
        
        buildGUI();
        setupActions();

        pack();
        setLocationRelativeTo(owner);
    }

    private void buildGUI() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Senha:"));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        setContentPane(mainPanel);
    }

    private void setupActions() {
        btnLogin.addActionListener(e -> attemptLogin());
        btnCancel.addActionListener(e -> {
            authenticatedUser = null;
            dispose();
        });
    }

    private void attemptLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()); 

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o e-mail e a senha.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Usuario> optUser = users.autenticar(email, password);

        if (optUser.isPresent()) {
            authenticatedUser = optUser.get();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciais inválidas.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    public Optional<Usuario> getAuthenticatedUser() {
        return Optional.ofNullable(authenticatedUser);
    }
}
