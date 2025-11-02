package model;

/**
 * Representa um usuário do sistema.
 * email é considerado identificador e não possui setter.
 */
public class Usuario {
    private String nome;
    private final String email;
    private String senha;

    public Usuario(String nome, String email, String senha) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio.");
        }
        this.email = email.trim().toLowerCase();
        setNome(nome);
        setSenha(senha);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getEmail() { return email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) {
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres.");
        }
        this.senha = senha;
    }

    public boolean autenticar(String email, String senha) {
        return this.email.equalsIgnoreCase(email) && this.senha.equals(senha);
    }

    @Override
    public String toString() {
        return "Usuário: " + nome + " <" + email + ">";
    }
}
