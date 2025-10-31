package pe.edu.utp.rudypollo.view;

import pe.edu.utp.rudypollo.controller.RepartidorControlador;

import javax.swing.*;
import java.awt.*;

public class ButtonEditorRepartidor extends DefaultCellEditor {
    protected JButton button;
    private String action;
    private boolean clicked;
    private RepartidorControlador controlador;
    private JTable table;

    public ButtonEditorRepartidor(JCheckBox checkBox, String action, RepartidorControlador controlador, JTable table) {
        super(checkBox);
        this.action = action;
        this.controlador = controlador;
        this.table = table;
        button = new JButton(action);
        button.setOpaque(true);

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
        int row = table.getSelectedRow();
        if (row >= 0) {
            SwingUtilities.invokeLater(() -> {
                if ("Editar".equals(action)) {
                    controlador.editarRepartidor(row);
                } else if ("Eliminar".equals(action)) {
                    controlador.eliminarRepartidor(row);
                }
            });
        }
        return null;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
