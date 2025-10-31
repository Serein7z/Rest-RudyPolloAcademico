package pe.edu.utp.rudypollo.controller;

import pe.edu.utp.rudypollo.dao.impl.PedidoDAOImpl;
import pe.edu.utp.rudypollo.dao.impl.RepartidorDAOImpl;
import pe.edu.utp.rudypollo.model.Pedido;
import pe.edu.utp.rudypollo.model.Repartidor;
import pe.edu.utp.rudypollo.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PedidoControlador {

    private PedidoPanel vista;
    private PedidoDAOImpl pedidoDAO;
    private RepartidorDAOImpl repartidorDAO;

    public PedidoControlador(PedidoPanel vista) {
        this.vista = vista;
        this.pedidoDAO = new PedidoDAOImpl();
        this.repartidorDAO = new RepartidorDAOImpl();
        initController();
    }

    private void initController() {
        cargarPedidos();
        vista.getBtnBuscar().addActionListener(e -> buscarPedidos());
        vista.getBtnNuevoPedido().addActionListener(e -> abrirNuevoPedidoDialog());
        vista.getBtnActualizar().addActionListener(e -> cargarPedidos()); 
    }

    public void cargarPedidos() {
        try {
            List<Pedido> lista = pedidoDAO.findAll();
            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0);

            for (Pedido p : lista) {
                String accion = p.getEstado().equalsIgnoreCase("ASIGNADO") ? "Entregar"
                        : p.getEstado().equalsIgnoreCase("ENTREGADO") ? "Finalizado"
                        : "Asignar";

                model.addRow(new Object[]{
                    p.getId(),
                    p.getClienteId(),
                    p.getTotal(),
                    p.getEstado(),
                    p.getAssignedRepartidorId(),
                    p.getEta(),
                    p.getCreadoAt(),
                    "Editar",
                    "Eliminar",
                    accion
                });
            }

            vista.getTablaPedidos().getColumn("Editar")
                    .setCellRenderer(new ButtonRenderer("Editar"));
            vista.getTablaPedidos().getColumn("Editar")
                    .setCellEditor(new ButtonEditorPedido(new JCheckBox(), this));

            vista.getTablaPedidos().getColumn("Eliminar")
                    .setCellRenderer(new ButtonRenderer("Eliminar"));
            vista.getTablaPedidos().getColumn("Eliminar")
                    .setCellEditor(new ButtonEditorPedido(new JCheckBox(), this));

            vista.getTablaPedidos().getColumn("Acción")
                    .setCellRenderer(new ButtonRenderer("Acción"));
            vista.getTablaPedidos().getColumn("Acción")
                    .setCellEditor(new ButtonEditorPedido(new JCheckBox(), this));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error cargando pedidos: " + e.getMessage());
        } 
    }

    private void buscarPedidos() {
        String filtro = vista.getTfBuscar().getText().trim();
        try {
            List<Pedido> lista = filtro.isEmpty()
                    ? pedidoDAO.findAll()
                    : pedidoDAO.findByClienteOrEstado(filtro);

            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0);

            for (Pedido p : lista) {
                String accion = p.getEstado().equalsIgnoreCase("ASIGNADO") ? "Entregar"
                        : p.getEstado().equalsIgnoreCase("ENTREGADO") ? "Finalizado"
                        : "Asignar";

                model.addRow(new Object[]{
                    p.getId(),
                    p.getClienteId(),
                    p.getTotal(),
                    p.getEstado(),
                    p.getAssignedRepartidorId(),
                    p.getEta(),
                    p.getCreadoAt(),
                    "Editar",
                    "Eliminar",
                    accion
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirNuevoPedidoDialog() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(vista);
        new PedidoCreationDialog(owner, this::cargarPedidos).setVisible(true);
    }

    // Editar pedido - CON VALIDACIÓN DE ESTADO
    public void editarPedido(int row) {
        try {
            int modelRow = vista.getTablaPedidos().convertRowIndexToModel(row);
            int id = (int) vista.getTablaPedidos().getModel().getValueAt(modelRow, 0);
            String estado = (String) vista.getTablaPedidos().getModel().getValueAt(modelRow, 3);

            // Validar que el pedido no esté ASIGNADO o ENTREGADO
            if (estado.equalsIgnoreCase("ASIGNADO")) {
                JOptionPane.showMessageDialog(vista, 
                    "No se puede editar un pedido que ya está ASIGNADO a un repartidor.",
                    "Edición no permitida",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (estado.equalsIgnoreCase("ENTREGADO")) {
                JOptionPane.showMessageDialog(vista, 
                    "No se puede editar un pedido que ya fue ENTREGADO.",
                    "Edición no permitida",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Si el estado es CREADO, permitir edición
            Pedido pedido = pedidoDAO.findById(id);
            if (pedido != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(vista);
                PedidoCreationDialog dialog = new PedidoCreationDialog(owner, this::cargarPedidos);
                dialog.setPedido(pedido);
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar pedido para edición");
        }
    }

    // Eliminar pedido - CON VALIDACIÓN DE ESTADO
    public void eliminarPedido(int row) {
        try {
            int modelRow = vista.getTablaPedidos().convertRowIndexToModel(row);
            int id = (int) vista.getTablaPedidos().getModel().getValueAt(modelRow, 0);
            String estado = (String) vista.getTablaPedidos().getModel().getValueAt(modelRow, 3);

            // Validar que el pedido no esté ASIGNADO o ENTREGADO
            if (estado.equalsIgnoreCase("ASIGNADO")) {
                JOptionPane.showMessageDialog(vista, 
                    "No se puede eliminar un pedido que está ASIGNADO a un repartidor.",
                    "Eliminación no permitida",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (estado.equalsIgnoreCase("ENTREGADO")) {
                JOptionPane.showMessageDialog(vista, 
                    "No se puede eliminar un pedido que ya fue ENTREGADO.",
                    "Eliminación no permitida",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(vista,
                    "¿Eliminar el pedido con ID " + id + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                pedidoDAO.delete(id);
                JOptionPane.showMessageDialog(vista, "Pedido eliminado con éxito.");
                cargarPedidos();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al eliminar pedido");
        }
    }

    // Asignar repartidor
    public void asignarPedido(int row) {
        try {
            int modelRow = vista.getTablaPedidos().convertRowIndexToModel(row);
            int id = (int) vista.getTablaPedidos().getModel().getValueAt(modelRow, 0);
            Pedido pedido = pedidoDAO.findById(id);

            if (pedido != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(vista);
                AsignarRepartidorDialog dialog
                        = new AsignarRepartidorDialog(owner, pedido, () -> {
                            try {
                                Pedido actualizado = pedidoDAO.findById(id);
                                if (actualizado != null) {
                                    DefaultTableModel model = vista.getTableModel();
                                    model.setValueAt(actualizado.getEstado(), modelRow, 3);
                                    model.setValueAt("Entregar", modelRow, 9);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al abrir asignación de repartidor");
        }
    }

    // Entregar pedido
    public void entregarPedido(int row) {
        try {
            int modelRow = vista.getTablaPedidos().convertRowIndexToModel(row);
            int pedidoId = (int) vista.getTablaPedidos().getModel().getValueAt(modelRow, 0);

            Pedido pedido = pedidoDAO.findById(pedidoId);
            if (pedido == null) {
                return;
            }

            if (!"ASIGNADO".equalsIgnoreCase(pedido.getEstado())) {
                JOptionPane.showMessageDialog(vista, "El pedido no está en estado ASIGNADO.");
                return;
            }

            pedido.setEstado("ENTREGADO");
            pedidoDAO.update(pedido);

            if (pedido.getAssignedRepartidorId() != null) {
                Repartidor r = repartidorDAO.findById(pedido.getAssignedRepartidorId());
                if (r != null) {
                    r.setEstado("DISPONIBLE");
                    repartidorDAO.update(r);
                }
            }

            JOptionPane.showMessageDialog(vista, "Pedido marcado como ENTREGADO.");

            DefaultTableModel model = vista.getTableModel();
            model.setValueAt("ENTREGADO", modelRow, 3);
            model.setValueAt("Finalizado", modelRow, 9);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al entregar pedido");
        }
    }
}