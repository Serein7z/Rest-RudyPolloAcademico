package pe.edu.utp.rudypollo.view;

import pe.edu.utp.rudypollo.controller.PedidoControlador;

import javax.swing.*;
import java.awt.*;

public class ButtonEditorPedido extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean clicked;
    private int row;
    private PedidoControlador controlador;

    public ButtonEditorPedido(JCheckBox checkBox, PedidoControlador controlador) {
        super(checkBox);
        this.controlador = controlador;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int col) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        clicked = true;
        this.row = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            switch (label) {
                case "Editar":
                    controlador.editarPedido(row);
                    controlador.cargarPedidos();
                    break;
                case "Eliminar":
                    controlador.eliminarPedido(row);
                    controlador.cargarPedidos();
                    break;
                case "Asignar":
                    controlador.asignarPedido(row);
                    controlador.cargarPedidos();
                    break;
                case "Entregar":
                    controlador.entregarPedido(row);
                    controlador.cargarPedidos();
                    break;
                case "Finalizado":
                    JOptionPane.showMessageDialog(null, "El pedido ya fue entregado.");
                    break;
            }
        }
        clicked = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
