package app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Musica;

public class MusicaTableModel extends AbstractTableModel {

    private final String[] colunas = {"ID", "Título", "Artista", "Álbum", "Gênero", "Duração (s)"};
    private List<Musica> dados;

    public MusicaTableModel() {
        this.dados = new ArrayList<>();
    }

    public void setMusicas(List<Musica> musicas) {
        this.dados = musicas;
        fireTableDataChanged(); 
    }
    
    public Musica getMusica(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < dados.size()) {
            return dados.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return dados.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Musica musica = dados.get(rowIndex);

        switch (columnIndex) {
            case 0: return musica.getId().toString();
            case 1: return musica.getTitulo();
            case 2: return musica.getArtista();
            case 3: return musica.getAlbum();
            case 4: return musica.getGenero();
            case 5: return musica.getDuracaoSegundos();
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; 
    }
}