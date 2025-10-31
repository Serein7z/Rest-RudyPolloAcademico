package pe.edu.utp.rudypollo.view;

import javax.swing.*;
import java.awt.*;
import pe.edu.utp.rudypollo.controller.InventarioControlador;

public class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String action;
    private boolean clicked;
    private InventarioControlador controlador;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox, String action, InventarioControlador controlador, JTable table) {
        super(checkBox);
        this.action = action;
        this.controlador = controlador;
        this.table = table;
        button = new JButton(action);
        button.setOpaque(true);

        //Cuando el botón se presiona, dispara stopCellEditing()
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int col) {
        button.setText(action.equals("Editar") ? "✏️" : "🗑️");
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                //Ejecutar después de que Swing cierre la edición
                SwingUtilities.invokeLater(() -> {
                    if (action.equals("Editar")) {
                        controlador.editarPlato(row);
                    } else if (action.equals("Eliminar")) {
                        controlador.eliminarPlato(row);
                    }
                });
            }
        }
        clicked = false;
        return action; 
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
