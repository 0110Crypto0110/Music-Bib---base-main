package e2e;

import app.MainView; 
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture; 
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.data.TableCell; 
import org.junit.jupiter.api.AfterEach; 
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test; 

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import java.util.UUID;

import static org.assertj.swing.finder.WindowFinder.findDialog;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste End-to-End (E2E) para a aplicação MainView, usando AssertJ-Swing.
 * Simula um usuário logando e realizando operações CRUD em Músicas.
 * * * NOTA: Este teste depende de credenciais válidas existentes no UsuarioRepository.
 * No ambiente de teste, assumimos que 'test@test.com' / 'pass123' funciona.
 */
public class MusicaE2ETest {
	
	private FrameFixture window;
	private MainView mainFrame;

	// --- Credenciais ---
	private static final String TEST_USER_EMAIL = "test@test.com"; 
	private static final String TEST_PASS = "pass123";
	// O nome esperado para o usuário de teste
	private static final String TEST_USER_NAME = "Teste E2E"; 

	// --- Dados de Música Genéricos para Criação ---
	private static final String NOVA_MUSICA_ARTISTA = "Queen";
	private static final String NOVA_MUSICA_ALBUM = "A Night at the Opera";
	private static final String NOVA_MUSICA_GENERO = "Rock";
	private static final String NOVA_MUSICA_DURACAO = "354";
	
	// --- Dados Específicos para os Testes ---
	private static final String MUSICA_PARA_REMOVER_TITULO = "Musica Remover " + UUID.randomUUID().toString().substring(0, 4);
	private static final String MUSICA_PARA_EDITAR_TITULO = "Musica Editar " + UUID.randomUUID().toString().substring(0, 4);
	
	// --- Dados para Edição ---
	private static final String NOVO_TITULO_EDITADO = "Título Editado " + UUID.randomUUID().toString().substring(0, 4);
	private static final String NOVO_ARTISTA_EDITADO = "Artista Editado";


	@BeforeEach
	public void setUp() {
		// Inicializa o AssertJ-Swing para esperar o EDT
		mainFrame = GuiActionRunner.execute(() -> new MainView());
		window = new FrameFixture(mainFrame);
		window.show(); 
	}

	/**
	 * Helper para realizar o login na aplicação usando nomes E2E.
	 */
	private void login() {
		window.button("btnLogin").click(); 
		
		DialogFixture loginDialog = findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
			@Override
			protected boolean isMatching(JDialog dialog) {
				return "Fazer Login".equals(dialog.getTitle()) && dialog.isVisible();
			}
		}).using(window.robot());

		// Preencher dados de Login usando Nomes E2E
		loginDialog.textBox("emailField").enterText(TEST_USER_EMAIL); 
		loginDialog.textBox("passwordField").enterText(TEST_PASS);
		
		loginDialog.button("btnLoginDialog").click();
		
		loginDialog.requireNotVisible();
		// CORREÇÃO: Espera o nome do usuário ("Teste E2E"), que é o que o sistema de fato exibe
		window.label("statusLabel").requireText("Status: Logado como " + TEST_USER_NAME); 
	}
	
	/**
	 * Helper para interagir com o JOptionPane de Sucesso.
	 */
	private void confirmSuccessDialog() {
		 findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
			 @Override
			 protected boolean isMatching(JDialog dialog) {
				 return "Sucesso".equals(dialog.getTitle()) && dialog.isVisible();
			 }
		 }).using(window.robot()).button().click();
	}

	/**
	 * Helper para adicionar uma música usando nomes E2E do AddMusicaDialog.
	 * @param titulo O título da música a ser adicionada.
	 */
	private void addMusica(String titulo) {
		window.button("btnAdicionarMusica").click();

		DialogFixture addMusicaDialog = findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
			@Override
			protected boolean isMatching(JDialog dialog) {
				return "Adicionar Nova Música".equals(dialog.getTitle()) && dialog.isVisible();
			}
		}).using(window.robot());

		// Campos do AddMusicaDialog
		addMusicaDialog.textBox("addTituloField").enterText(titulo);
		addMusicaDialog.textBox("addArtistaField").enterText(NOVA_MUSICA_ARTISTA);
		addMusicaDialog.textBox("addAlbumField").enterText(NOVA_MUSICA_ALBUM);
		addMusicaDialog.textBox("addGeneroField").enterText(NOVA_MUSICA_GENERO);
		addMusicaDialog.textBox("addDuracaoField").enterText(NOVA_MUSICA_DURACAO);

		addMusicaDialog.button("btnSalvarMusica").click();
		
		confirmSuccessDialog();
		
		addMusicaDialog.requireNotVisible();
	}
	
	/**
	 * Helper para encontrar a linha de uma música na tabela pelo título.
	 * @param titulo O título para buscar.
	 * @return O índice da linha na view, ou -1 se não for encontrado.
	 */
	private int findMusicaRow(String titulo) {
		JTable table = window.table("musicaTable").target();
		for (int i = 0; i < table.getRowCount(); i++) {
			// Coluna 1 é o Título
			if (table.getValueAt(i, 1).equals(titulo)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Helper para verificar se uma música com um título existe na tabela.
	 */
	private boolean isMusicaPresent(String titulo) {
		return findMusicaRow(titulo) != -1;
	}

	@Test
	public void testScenario_AddAndRemoveMusicaSuccessfully() {
		login();
		
		// 1. Adicionar Música para Remoção
		addMusica(MUSICA_PARA_REMOVER_TITULO);
		
		// 2. Verificar adição
		assertThat(isMusicaPresent(MUSICA_PARA_REMOVER_TITULO))
			 .as("A música '%s' deve ser encontrada após a adição.", MUSICA_PARA_REMOVER_TITULO)
			 .isTrue();

		// --- REMOÇÃO ---
		
		// 3. Encontra a linha da música
		int rowToRemove = findMusicaRow(MUSICA_PARA_REMOVER_TITULO);
		
		// 4. Clica com o botão direito (Popup Trigger) na célula [rowToRemove, 1] (Título)
		// CORREÇÃO: Usando TableCell pois cell(int, int) não é suportado nesta versão
		window.table("musicaTable").cell(TableCell.row(rowToRemove).column(1)).rightClick();
		
		// 5. Clica no item "Remover Música" (usando o nome E2E)
		// Usamos window.menuItem() que procura o item do JPopupMenu.
		window.menuItem("popupItemRemover").click();
		
		// 6. Interage com o Diálogo de Confirmação (Sim)
		DialogFixture confirmDialog = findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
			@Override
			protected boolean isMatching(JDialog dialog) {
				return "Confirmar Remoção".equals(dialog.getTitle()) && dialog.isVisible();
			}
		}).using(window.robot());

		confirmDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return "Sim".equals(button.getText()); 
			}
		}).click();

		// 7. Confirma o sucesso
		confirmSuccessDialog();

		// 8. Verificação Final: A música deve ter desaparecido
		assertThat(isMusicaPresent(MUSICA_PARA_REMOVER_TITULO))
			 .as("A música '%s' deve ter sido removida da tabela.", MUSICA_PARA_REMOVER_TITULO)
			 .isFalse();

		// 9. Logout
		window.button("btnLogin").click(); 
	}
	
	@Test
	public void testScenario_EditMusicaSuccessfully() {
		login();
		
		// 1. Adicionar Música para Edição
		addMusica(MUSICA_PARA_EDITAR_TITULO);
		
		// 2. Verificar adição
		assertThat(isMusicaPresent(MUSICA_PARA_EDITAR_TITULO)).isTrue();
		
		// --- EDIÇÃO ---
		
		// 3. Encontra a linha
		int rowToEdit = findMusicaRow(MUSICA_PARA_EDITAR_TITULO);
		
		// 4. Clica com o botão direito na célula [rowToEdit, 1] (Título)
		// CORREÇÃO: Usando TableCell pois cell(int, int) não é suportado nesta versão
		window.table("musicaTable").cell(TableCell.row(rowToEdit).column(1)).rightClick();
		
		// 5. Clica no item "Editar Música" (usando o nome E2E)
		window.menuItem("popupItemEditar").click();
		
		// 6. Encontra o diálogo de edição
		DialogFixture editDialog = findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
			@Override
			protected boolean isMatching(JDialog dialog) {
				return dialog.getTitle().startsWith("Editar Música:") && dialog.isVisible();
			}
		}).using(window.robot());

		// 7. Edita os campos usando nomes E2E do EditMusicaDialog
		editDialog.textBox("editTituloField").setText(NOVO_TITULO_EDITADO);
		editDialog.textBox("editArtistaField").setText(NOVO_ARTISTA_EDITADO);
		// Deixa os outros campos (Álbum, Gênero, Duração) como estão para teste de persistência
		
		// 8. Salva as alterações
		editDialog.button("btnSalvarEdicao").click();
		
		// 9. Confirma o sucesso
		confirmSuccessDialog();
		
		editDialog.requireNotVisible();

		// 10. Verificação Final: O título original deve ter desaparecido, e o novo deve existir.
		assertThat(isMusicaPresent(MUSICA_PARA_EDITAR_TITULO))
			 .as("O título original deve ter desaparecido da tabela após a edição.")
			 .isFalse();
			 
		assertThat(isMusicaPresent(NOVO_TITULO_EDITADO))
			 .as("O novo título editado deve estar presente na tabela.")
			 .isTrue();

		// 11. (Opcional) Limpeza: Remover a música editada
		int rowToRemove = findMusicaRow(NOVO_TITULO_EDITADO);
		// Clica com o botão direito na célula [rowToRemove, 1] (Título)
		// CORREÇÃO: Usando TableCell pois cell(int, int) não é suportado nesta versão
		window.table("musicaTable").cell(TableCell.row(rowToRemove).column(1)).rightClick();
		window.menuItem("popupItemRemover").click();

		DialogFixture confirmDialog = findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
			@Override
			protected boolean isMatching(JDialog dialog) {
				return "Confirmar Remoção".equals(dialog.getTitle()) && dialog.isVisible();
			}
		}).using(window.robot());
		confirmDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return "Sim".equals(button.getText()); 
			}
		}).click();
		confirmSuccessDialog();

		// 12. Logout
		window.button("btnLogin").click(); 
	}


	@AfterEach 
	public void tearDown() {
		window.cleanUp(); 
	}
}
