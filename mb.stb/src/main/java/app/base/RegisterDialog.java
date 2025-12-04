package app.base;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import exception.RegraNegocioException;
import model.Usuario;
import repository.UsuarioRepository;

public class RegisterDialog extends JDialog {

    private final UsuarioRepository users;

    private final JTextField nomeField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmPasswordField = new JPasswordField(20);

    private final JButton btnRegister = new JButton("Registrar");
    private final JButton btnCancel = new JButton("Cancelar");

    public RegisterDialog(Frame owner, UsuarioRepository users) {
        super(owner, "Registrar Novo Usuário", true);
        this.users = users;

        buildGUI();
        setupActions();
        
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildGUI() {
        // --- Adiciona nomes aos componentes para E2E testing ---
        // Isso permite que o AssertJ-Swing os encontre de forma robusta.
        nomeField.setName("nomeField");
        emailField.setName("emailField");
        passwordField.setName("passwordField");
        confirmPasswordField.setName("confirmPasswordField");
        btnRegister.setName("btnRegister");
        btnCancel.setName("btnCancel");
        // -----------------------------------------------------
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Nome:"));
        formPanel.add(nomeField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Senha (>=4 caracteres):"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Confirmar Senha:"));
        formPanel.add(confirmPasswordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void setupActions() {
        btnRegister.addActionListener(e -> attemptRegistration());
        btnCancel.addActionListener(e -> dispose());
    }

    private void attemptRegistration() {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String senha = new String(passwordField.getPassword());
        String confirmSenha = new String(confirmPasswordField.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!senha.equals(confirmSenha)) {
            JOptionPane.showMessageDialog(this, "A senha e a confirmação de senha não coincidem.", "Erro de Senha", JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            return;
        }

        try {
            if (users.existePorEmail(email)) {
                JOptionPane.showMessageDialog(this, "Já existe usuário com esse email.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario u = new Usuario(nome, email, senha);
            boolean ok = users.cadastrar(u);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Usuário registrado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível registrar o usuário.", "Erro Desconhecido", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, "Erro de Negócio: " + ex.getMessage(), "Erro de Negócio", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
