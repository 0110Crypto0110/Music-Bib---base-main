package app.base;

import javax.swing.*;
import java.awt.*;

/**
 * Classe base abstrata para todos os diálogos modais da aplicação.
 * <p>
 * Fornece uma estrutura visual e comportamental padronizada,
 * simplificando a criação de subclasses (como AddMusicaDialog, EditMusicaDialog, etc.).
 * </p>
 *
 * <h3>Responsabilidades</h3>
 * <ul>
 *   <li>Configuração inicial do {@link JDialog} (modalidade, título, layout).</li>
 *   <li>Criação de painéis padrão para formulário e botões.</li>
 *   <li>Implementação de botões padrão: Confirmar e Cancelar.</li>
 *   <li>Método utilitário para exibição de mensagens via {@link JOptionPane}.</li>
 * </ul>
 *
 * <h3>Uso nas subclasses:</h3>
 * <ol>
 *   <li>Implementar {@link #buildForm()} para montar o conteúdo central.</li>
 *   <li>Adicionar listeners a {@link #btnConfirmar} se necessário.</li>
 *   <li>Invocar {@link #finalizeDialog()} ao final da construção.</li>
 * </ol>
 */
public abstract class BaseDialog extends JDialog {

    /** Referência ao frame principal (pai). */
    protected final Frame owner;

    /** Painel principal do formulário. */
    protected final JPanel formPanel = new JPanel();

    /** Painel inferior contendo os botões de ação. */
    protected final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    /** Botão padrão de confirmação. */
    protected final JButton btnConfirmar = new JButton("Confirmar");

    /** Botão padrão de cancelamento. */
    protected final JButton btnCancelar = new JButton("Cancelar");

    /**
     * Construtor padrão para os diálogos base.
     *
     * @param owner janela pai (geralmente o JFrame principal)
     * @param title título da janela
     */
    public BaseDialog(Frame owner, String title) {
        super(owner, title, true); // modal = true
        this.owner = owner;

        buildBaseLayout();
        setupDefaultActions();
    }

    /**
     * Monta o layout principal do diálogo.
     * <p>
     * Cria o painel base com margens, insere o painel de formulário
     * e o painel de botões na parte inferior.
     * </p>
     */
    private void buildBaseLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adiciona os botões padrão
        buttonPanel.add(btnConfirmar);
        buttonPanel.add(btnCancelar);

        setContentPane(mainPanel);
    }

    /**
     * Configura ações padrão dos botões e teclas de atalho.
     */
    private void setupDefaultActions() {
        btnCancelar.addActionListener(e -> dispose());

        // Define o botão padrão (Enter confirma)
        getRootPane().setDefaultButton(btnConfirmar);

        // Pressionar ESC fecha o diálogo
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * Subclasses devem implementar este método para construir
     * seus campos e componentes de interface.
     */
    protected abstract void buildForm();

    /**
     * Finaliza e centraliza o diálogo após a construção da interface.
     * Deve ser chamado ao fim do construtor da subclasse.
     */
    protected void finalizeDialog() {
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Exibe uma mensagem padrão usando JOptionPane.
     *
     * @param message mensagem a ser exibida
     * @param title   título da janela
     * @param type    tipo da mensagem (por exemplo, JOptionPane.INFORMATION_MESSAGE)
     */
    protected void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }
}
