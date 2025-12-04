package app.base;

import model.Usuario;
import repository.UsuarioRepository;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Diálogo de autenticação do usuário.
 * <p>
 * Realiza login básico validando email e senha através do {@link UsuarioRepository}.
 * </p>
 */
public class LoginDialog extends BaseDialog {

    private final UsuarioRepository users;
    private Usuario authenticatedUser = null;

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginDialog(Frame owner, UsuarioRepository users) {
        super(owner, "Fazer Login");
        this.users = users;

        // --- Nomes para E2E Testing (Maior robustez) ---
        emailField.setName("emailField");
        passwordField.setName("passwordField");
        btnConfirmar.setName("btnLoginDialog");
        btnCancelar.setName("btnCancelDialog");
        // --- Fim dos Nomes ---

        buildForm();
        setupActions();
        finalizeDialog();
    }

    @Override
    protected void buildForm() {
        formPanel.setLayout(new GridLayout(2, 2, 8, 8));
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Senha:"));
        formPanel.add(passwordField);
    }

    private void setupActions() {
        btnConfirmar.setText("Login");
        btnConfirmar.addActionListener(e -> attemptLogin());
        btnCancelar.addActionListener(e -> {
            authenticatedUser = null;
            dispose();
        });
    }

    private void attemptLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Por favor, preencha o e-mail e a senha.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Usuario> optUser = users.autenticar(email, password);

        if (optUser.isPresent()) {
            authenticatedUser = optUser.get();
            dispose();
        } else {
            showMessage("Credenciais inválidas.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    public Optional<Usuario> getAuthenticatedUser() {
        return Optional.ofNullable(authenticatedUser);
    }
}
