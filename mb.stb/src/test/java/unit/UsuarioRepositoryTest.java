package unit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Usuario;
import repository.UsuarioRepository;

class UsuarioRepositoryTest {

	private UsuarioRepository repo;
	private Usuario usuarioA, usuarioB;

	@BeforeEach
	void setup() {
		repo = new UsuarioRepository();
		// Garante que o repositório esteja vazio antes de cada teste unitário,
		// limpando o usuário padrão (E2E) adicionado no construtor.
		repo.limpar();

		usuarioA = new Usuario("Alice", "alice@example.com", "1234");
		usuarioB = new Usuario("Bob", "bob@example.com", "abcd");
	}

	@Test
	void deveCadastrarUsuarioComSucesso() {
		boolean resultado = repo.cadastrar(usuarioA);
		assertTrue(resultado);
		assertEquals(1, repo.listarTodos().size());
	}

	@Test
	void naoDeveCadastrarUsuarioNulo() {
		boolean resultado = repo.cadastrar(null);
		assertFalse(resultado);
		assertTrue(repo.listarTodos().isEmpty());
	}

	@Test
	void naoDeveCadastrarUsuarioComEmailDuplicado() {
		repo.cadastrar(usuarioA);
		Usuario duplicado = new Usuario("Alice Clone", "alice@example.com", "9999");
		boolean resultado = repo.cadastrar(duplicado);
		assertFalse(resultado);
		assertEquals(1, repo.listarTodos().size());
	}

	@Test
	void deveAutenticarUsuarioComCredenciaisCorretas() {
		repo.cadastrar(usuarioA);
		Optional<Usuario> encontrado = repo.autenticar("alice@example.com", "1234");
		assertTrue(encontrado.isPresent());
		assertEquals("Alice", encontrado.get().getNome());
	}

	@Test
	void naoDeveAutenticarComSenhaIncorreta() {
		repo.cadastrar(usuarioA);
		Optional<Usuario> resultado = repo.autenticar("alice@example.com", "errada");
		assertTrue(resultado.isEmpty());
	}

	@Test
	void naoDeveAutenticarUsuarioInexistente() {
		repo.cadastrar(usuarioA);
		Optional<Usuario> resultado = repo.autenticar("inexistente@example.com", "1234");
		assertTrue(resultado.isEmpty());
	}

	@Test
	void deveDetectarEmailExistente() {
		repo.cadastrar(usuarioA);
		assertTrue(repo.existePorEmail("alice@example.com"));
	}

	@Test
	void deveDetectarEmailIgnorandoMaiusculas() {
		repo.cadastrar(usuarioA);
		assertTrue(repo.existePorEmail("ALICE@EXAMPLE.COM"));
	}

	@Test
	void deveRetornarListaImutavelDeUsuarios() {
		repo.cadastrar(usuarioA);
		var lista = repo.listarTodos();
		assertThrows(UnsupportedOperationException.class, () -> lista.add(usuarioB));
	}

	@Test
	void listarTodosDeveRetornarCopiaIndependente() {
		repo.cadastrar(usuarioA);
		var lista1 = repo.listarTodos();
		var lista2 = repo.listarTodos();
		assertNotSame(lista1, lista2, "Cada chamada deve retornar uma nova cópia imutável");
	}
}
