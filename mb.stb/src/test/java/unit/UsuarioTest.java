package unit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Usuario;

class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setup() {
        // O construtor de 3 argumentos será adicionado em Usuario.java
        usuario = new Usuario("João", "joao@example.com", "1234");
    }

    // =========================================================
    // ✅ Testes de Construtor
    // =========================================================

    @Test
    void deveCriarUsuarioComDadosValidos() {
        assertEquals("João", usuario.getNome());
        assertEquals("joao@example.com", usuario.getEmail());
        assertEquals("1234", usuario.getSenha());
    }

    @Test
    void deveConverterEmailParaMinusculasERemoverEspacos() {
        Usuario u = new Usuario("Maria", "  MARIA@GMAIL.COM  ", "senha");
        assertEquals("maria@gmail.com", u.getEmail());
    }

    @Test
    void deveLancarExcecaoSeEmailForNuloOuVazio() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> new Usuario("A", "", "1234")),
            () -> assertThrows(IllegalArgumentException.class, () -> new Usuario("A", "   ", "1234")),
            () -> assertThrows(IllegalArgumentException.class, () -> new Usuario("A", null, "1234"))
        );
    }

    // =========================================================
    // ✅ Testes de Nome
    // =========================================================

    @Test
    void devePermitirAlterarNomeValido() {
        usuario.setNome("João Ricardo");
        assertEquals("João Ricardo", usuario.getNome());
    }

    @Test
    void deveRemoverEspacosEmBrancoDoNome() {
        usuario.setNome("  João Justo  ");
        assertEquals("João Justo", usuario.getNome());
    }

    @Test
    void deveLancarExcecaoSeNomeForNuloOuVazio() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> usuario.setNome("")),
            () -> assertThrows(IllegalArgumentException.class, () -> usuario.setNome("   ")),
            () -> assertThrows(IllegalArgumentException.class, () -> usuario.setNome(null))
        );
    }

    // =========================================================
    // ✅ Testes de Senha
    // =========================================================

    @Test
    void devePermitirAlterarSenhaValida() {
        usuario.setSenha("novaSenha");
        assertEquals("novaSenha", usuario.getSenha());
    }

    @Test
    void deveLancarExcecaoSeSenhaForCurtaOuNula() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> usuario.setSenha("")),
            () -> assertThrows(IllegalArgumentException.class, () -> usuario.setSenha("abc")),
            () -> assertThrows(IllegalArgumentException.class, () -> usuario.setSenha(null))
        );
    }

    // =========================================================
    // ✅ Testes de Autenticação
    // =========================================================

    @Test
    void autenticarDeveRetornarTrueParaCredenciaisCorretas() {
        assertTrue(usuario.autenticar("joao@example.com", "1234"));
    }

    @Test
    void autenticarDeveIgnorarCaseDoEmail() {
        assertTrue(usuario.autenticar("JOAO@EXAMPLE.COM", "1234"));
    }

    @Test
    void autenticarDeveRetornarFalseParaEmailOuSenhaErrados() {
        assertAll(
            () -> assertFalse(usuario.autenticar("errado@example.com", "1234")),
            () -> assertFalse(usuario.autenticar("joao@example.com", "senhaErrada")),
            () -> assertFalse(usuario.autenticar("errado@example.com", "senhaErrada"))
        );
    }

    // =========================================================
    // ✅ Teste de Representação Textual
    // =========================================================

    @Test
    void toStringDeveExibirNomeEEmailFormatados() {
        String esperado = "Usuário: João <joao@example.com>";
        assertEquals(esperado, usuario.toString());
    }

}
