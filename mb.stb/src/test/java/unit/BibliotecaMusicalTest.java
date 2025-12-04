package unit;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse; // Importação limpa
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import model.Musica;
import persistence.FileStorage;
import repository.BibliotecaMusical;

class BibliotecaMusicalTest {

    @Mock
    private FileStorage storageMock;

    private BibliotecaMusical biblioteca;

    private Musica musicaA, musicaB;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Simulação de carregamento inicial vazio
        when(storageMock.carregar()).thenReturn(new ArrayList<>());

        // CORREÇÃO: O método de setup anterior era complexo e falhava na injeção.
        // Esta é a forma correta:
        // 1. Passa o mock diretamente no construtor.
        // 2. Passa 'false' para 'carregarExemplosSeVazio' para garantir que a lista comece vazia.
        biblioteca = new BibliotecaMusical(storageMock, false);

        // Garante que as músicas de teste tenham IDs únicos e imutáveis
        musicaA = new Musica(UUID.randomUUID(), "Imagine", "John Lennon", "Imagine", "Rock", 180);
        musicaB = new Musica(UUID.randomUUID(), "Hey Jude", "The Beatles", "Hey Jude", "Rock", 210);
    }

    @Test
    void deveAdicionarMusicaComSucesso() {
        boolean resultado = biblioteca.adicionarMusica(musicaA);
        assertTrue(resultado, "Deveria retornar true ao adicionar."); // Agora deve passar
        assertEquals(1, biblioteca.tamanho(), "A biblioteca deve ter 1 música.");
        
        // Verifica que salvar foi chamado 1 vez (para persistir a nova lista)
        verify(storageMock, times(1)).salvar(anyList());
    }

    @Test
    void naoDeveAdicionarMusicaNula() {
        boolean resultado = biblioteca.adicionarMusica(null);
        assertFalse(resultado, "Deveria retornar false para música nula.");
        verify(storageMock, never()).salvar(any());
    }

    @Test
    void naoDeveAdicionarMusicaDuplicada() {
        // 1ª adição: deve salvar 1 vez
        biblioteca.adicionarMusica(musicaA); 
        
        // 2ª adição (duplicada): não deve salvar
        boolean duplicada = biblioteca.adicionarMusica(musicaA);
        
        assertFalse(duplicada, "Deveria retornar false para música duplicada.");
        // O salvar deve ter sido chamado apenas na primeira adição.
        verify(storageMock, times(1)).salvar(anyList()); // Agora deve passar
    }

    @Test
    void deveEditarMusicaExistente() {
        // 1ª Adição: Salva 1 vez
        biblioteca.adicionarMusica(musicaA); 
        
        // Edição: Deve salvar novamente (2ª vez)
        boolean ok = biblioteca.editarMusica(musicaA.getId(), "Imagine (Remastered)", null, null, null, null);
        
        assertTrue(ok, "Deveria retornar true ao editar."); // Agora deve passar
        assertEquals("Imagine (Remastered)", biblioteca.buscarPorId(musicaA.getId()).get().getTitulo());
        
        // Deve ter chamado salvar duas vezes (uma para a adição + uma para a edição)
        verify(storageMock, times(2)).salvar(anyList()); 
    }

    @Test
    void naoDeveEditarMusicaInexistente() {
        boolean ok = biblioteca.editarMusica(UUID.randomUUID(), "Nova", null, null, null, null);
        assertFalse(ok, "Deveria retornar false para edição inexistente.");
        verify(storageMock, never()).salvar(any());
    }

    @Test
    void deveRemoverMusicaPorId() {
        // 1ª Adição: Salva 1 vez
        biblioteca.adicionarMusica(musicaA); 
        
        // Remoção: Deve salvar novamente (2ª vez)
        boolean removida = biblioteca.removerPorId(musicaA.getId());
        
        assertTrue(removida, "Deveria retornar true na remoção."); // Agora deve passar
        assertEquals(0, biblioteca.tamanho());
        
        // Deve ter chamado salvar duas vezes (uma para a adição + uma para a remoção)
        verify(storageMock, times(2)).salvar(anyList());
    }

    // ... (o restante dos testes estava correto) ...

    @Test
    void naoDeveRemoverMusicaInexistente() {
        boolean ok = biblioteca.removerPorId(UUID.randomUUID());
        assertFalse(ok, "Deveria retornar false para remoção inexistente.");
        verify(storageMock, never()).salvar(any());
    }

    @Test
    void deveBuscarPorTitulo() {
        biblioteca.adicionarMusica(musicaA);
        biblioteca.adicionarMusica(musicaB);
        var resultados = biblioteca.buscarPorTitulo("Imagine");
        assertEquals(1, resultados.size());
        assertEquals(musicaA, resultados.get(0));
    }

    @Test
    void deveBuscarPorArtista() {
        biblioteca.adicionarMusica(musicaB);
        // O teste deve ser case-insensitive e buscar por substrings
        var resultados = biblioteca.buscarPorArtista("beatles"); 
        assertEquals(1, resultados.size());
        assertEquals("The Beatles", resultados.get(0).getArtista());
    }

    @Test
    void deveBuscarPorGenero() {
        biblioteca.adicionarMusica(musicaA);
        biblioteca.adicionarMusica(musicaB);
        // O teste deve ser case-insensitive
        var resultados = biblioteca.buscarPorGenero("rock"); 
        assertEquals(2, resultados.size());
    }

    @Test
    void deveRetornarListaImutavel() {
        biblioteca.adicionarMusica(musicaA);
        var lista = biblioteca.listarTodas();
        assertThrows(UnsupportedOperationException.class, () -> lista.add(musicaB));
    }

    @Test
    void deveVerificarDuplicidadeCorretamente() {
        biblioteca.adicionarMusica(musicaA);
        assertTrue(biblioteca.existeDuplicada(musicaA));
        assertFalse(biblioteca.existeDuplicada(musicaB));
    }
}
