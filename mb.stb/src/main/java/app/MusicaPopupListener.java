package app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
// O import javax.swing.SwingUtilities foi removido
// import javax.swing.SwingUtilities; 

public class MusicaPopupListener extends MouseAdapter {

    private final JTable table;
    private final MainView mainView;

    public MusicaPopupListener(JTable table, MainView mainView) {
        this.table = table;
        this.mainView = mainView;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    private void showPopup(MouseEvent e) {
        // Verifica se é o clique do botão direito
        if (e.isPopupTrigger()) { 
            
            int r = table.rowAtPoint(e.getPoint());
            if (r >= 0 && r < table.getRowCount()) {
                table.setRowSelectionInterval(r, r); 
            } else {
                return; 
            }
            
            // Verifica se o usuário está logado antes de mostrar o menu
            if (mainView.getCurrentUser() == null) { 
                JOptionPane.showMessageDialog(table, "Faça login para editar ou remover músicas.", "Acesso Restrito", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JPopupMenu popup = new JPopupMenu();
            
            JMenuItem itemEditar = new JMenuItem("Editar Música");
            JMenuItem itemRemover = new JMenuItem("Remover Música");
            
            // --- Nomes para E2E Testing ---
            itemEditar.setName("popupItemEditar");
            itemRemover.setName("popupItemRemover");
            // -----------------------------

            itemEditar.addActionListener(ae -> {
                int selectedRow = table.getSelectedRow();
                UUID musicaId = getSelectedMusicaId(selectedRow);
                if (musicaId != null) {
                    mainView.showEditMusicaDialog(musicaId); 
                }
            });

            itemRemover.addActionListener(ae -> {
                int selectedRow = table.getSelectedRow();
                UUID musicaId = getSelectedMusicaId(selectedRow);
                if (musicaId != null) {
                    mainView.removeMusica(musicaId); 
                }
            });
            
            popup.add(itemEditar);
            popup.add(itemRemover);

            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    private UUID getSelectedMusicaId(int viewRow) {
        if (viewRow == -1) return null;
        
        int modelRow = table.convertRowIndexToModel(viewRow);
        
        // A coluna 0 é o ID (String)
        String idString = (String) table.getModel().getValueAt(modelRow, 0); 
        
        try {
            return UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
