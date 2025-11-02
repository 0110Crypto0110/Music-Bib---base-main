package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Usuario;

public class UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();

    /**
     * Construtor que garante a inicialização com um usuário padrão de teste.
     * Necessário para testes automatizados (E2E) que dependem de credenciais válidas.
     */
    public UsuarioRepository() {
        // Adiciona um usuário padrão para o ambiente de teste/desenvolvimento
        if (!existePorEmail("test@test.com")) {
            try {
                // Credenciais usadas no MusicaE2ETest
                Usuario usuarioTeste = new Usuario("Teste E2E", "test@test.com", "pass123");
                this.usuarios.add(usuarioTeste);
                System.out.println("[UsuarioRepository] Usuário de teste 'test@test.com' adicionado para E2E.");
            } catch (IllegalArgumentException e) {
                // Devemos garantir que o Usuario seja criado com dados válidos (Nome, Email, Senha >= 4)
                System.err.println("Falha ao criar usuário de teste: " + e.getMessage());
            }
        }
    }

    public boolean cadastrar(Usuario u) {
        if (u == null) return false;
        if (existePorEmail(u.getEmail())) return false;
        return usuarios.add(u);
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        return usuarios.stream()
                .filter(u -> u.autenticar(email, senha))
                .findFirst();
    }

    public boolean existePorEmail(String email) {
        return usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public List<Usuario> listarTodos() {
        return List.copyOf(usuarios);
    }

    /**
     * Limpa a lista de usuários. Deve ser usado com cautela, principalmente para testes.
     */
    public void limpar() {
        this.usuarios.clear();
    }
}
