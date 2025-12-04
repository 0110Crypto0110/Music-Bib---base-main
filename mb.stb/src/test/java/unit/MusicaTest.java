
package unit;
import java.util.UUID;
import exception.RegraNegocioException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Musica;

class MusicaTest {

    private Musica musica;

    @BeforeEach
    void setup() {
        musica = new Musica("Imagine", "John Lennon", "Imagine", "Rock", 183);
    }

    // =========================================================
    // ✅ Testes de Construtores
    // =========================================================

    @Test
    void deveGerarIdAutomaticamenteNoConstrutorPadrao() {
        assertNotNull(musica.getId(), "O ID deve ser gerado automaticamente.");
    }

    @Test
    void deveUsarIdFornecidoNoConstrutorComUUID() {
        UUID idEsperado = UUID.randomUUID();
        Musica m = new Musica(idEsperado, "Hey Jude", "Beatles", "Single", "Rock", 420);
        assertEquals(idEsperado, m.getId(), "O ID fornecido deve ser mantido.");
    }

    @Test
    void deveGerarNovoIdSePassadoNuloNoConstrutorComUUID() {
        Musica m = new Musica(null, "Yellow Submarine", "Beatles", "Album", "Pop", 200);
        assertNotNull(m.getId());
    }

    // =========================================================
    // ✅ Testes de Validação dos Setters
    // =========================================================

    @Test
    void devePermitirCamposValidosNosSetters() {
        musica.setTitulo("Jealous Guy");
        musica.setArtista("John Lennon");
        musica.setAlbum("Imagine");
        musica.setGenero("Rock");
        musica.setDuracaoSegundos(250);

        assertEquals("Jealous Guy", musica.getTitulo());
        assertEquals("John Lennon", musica.getArtista());
        assertEquals("Imagine", musica.getAlbum());
        assertEquals("Rock", musica.getGenero());
        assertEquals(250, musica.getDuracaoSegundos());
    }

    @Test
    void deveRemoverEspacosEmBrancoNosCampos() {
        musica.setTitulo("  Imagine  ");
        musica.setArtista("  John Lennon ");
        musica.setAlbum("  Imagine ");
        musica.setGenero("  Rock  ");

        assertEquals("Imagine", musica.getTitulo());
        assertEquals("John Lennon", musica.getArtista());
        assertEquals("Imagine", musica.getAlbum());
        assertEquals("Rock", musica.getGenero());
    }

    @Test
    void deveLancarExcecaoSeTituloForVazioOuNulo() {
        assertAll(
            () -> assertThrows(RegraNegocioException.class, () -> musica.setTitulo("")),
            () -> assertThrows(RegraNegocioException.class, () -> musica.setTitulo("  ")),
            () -> assertThrows(RegraNegocioException.class, () -> musica.setTitulo(null))
        );
    }

    @Test
    void deveLancarExcecaoSeArtistaForVazioOuNulo() {
        assertAll(
            () -> assertThrows(RegraNegocioException.class, () -> musica.setArtista("")),
            () -> assertThrows(RegraNegocioException.class, () -> musica.setArtista("   ")),
            () -> assertThrows(RegraNegocioException.class, () -> musica.setArtista(null))
        );
    }

    @Test
    void deveAceitarAlbumOuGeneroNulosESubstituirPorVazio() {
        musica.setAlbum(null);
        musica.setGenero(null);
        assertEquals("", musica.getAlbum());
        assertEquals("", musica.getGenero());
    }

    @Test
    void deveLancarExcecaoSeDuracaoForMenorOuIgualAZero() {
        assertAll(
            () -> assertThrows(RegraNegocioException.class, () -> musica.setDuracaoSegundos(0)),
            () -> assertThrows(RegraNegocioException.class, () -> musica.setDuracaoSegundos(-5))
        );
    }

    // =========================================================
    // ✅ Testes de Métodos Funcionais
    // =========================================================

    @Test
    void toLinhaDeveGerarFormatoEsperado() {
        String linha = musica.toLinha();
        assertTrue(linha.contains("Imagine - John Lennon (Rock) [183s]"),
                "O formato de saída deve conter título, artista, gênero e duração.");
    }

    @Test
    void toStringDeveRetornarMesmaSaidaDeToLinha() {
        assertEquals(musica.toLinha(), musica.toString());
    }

    // =========================================================
    // ✅ Testes de Igualdade e Hash
    // =========================================================

    @Test
    void duasMusicasIguaisDevemSerConsideradasIguais() {
        Musica outra = new Musica("Imagine", "JOHN LENNON", "Imagine", "Rock", 200);
        assertEquals(musica, outra);
        assertEquals(musica.hashCode(), outra.hashCode());
    }

    @Test
    void musicasComTitulosOuAlbunsDiferentesNaoDevemSerIguais() {
        Musica outra = new Musica("Hey Jude", "John Lennon", "Imagine", "Rock", 200);
        assertNotEquals(musica, outra);
    }

    @Test
    void equalsDeveRetornarFalseParaObjetoDeOutroTipoOuNulo() {
        assertNotEquals(musica, null);
        assertNotEquals(musica, "String qualquer");
    }

}
