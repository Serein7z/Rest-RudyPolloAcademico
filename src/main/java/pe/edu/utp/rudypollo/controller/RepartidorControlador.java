package pe.edu.utp.rudypollo.controller;

import pe.edu.utp.rudypollo.dao.RepartidorDAO;
import pe.edu.utp.rudypollo.dao.impl.RepartidorDAOImpl;
import pe.edu.utp.rudypollo.model.Repartidor;
import pe.edu.utp.rudypollo.view.RepartidorPanel;

import javax.swing.table.TableRowSorter;
import java.util.List;
import pe.edu.utp.rudypollo.view.ButtonRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pe.edu.utp.rudypollo.view.ButtonEditorRepartidor;

public class RepartidorControlador {

    private RepartidorPanel vista;
    private RepartidorDAO repartidorDAO;
    private Integer idEnEdicion = null; 

    public RepartidorControlador(RepartidorPanel vista) {
        this.vista = vista;
        this.repartidorDAO = new RepartidorDAOImpl();
        initListeners();
        cargarRepartidores();
    }

    private void initListeners() {
        vista.getBtnGuardar().addActionListener(e -> guardarRepartidor());
        vista.getBtnNuevo().addActionListener(e -> limpiarFormulario());
        vista.getBtnBuscar().addActionListener(e -> buscarRepartidores());
        vista.getBtnRefrescar().addActionListener(e -> cargarRepartidores());
    }

    private void guardarRepartidor() {
        try {
            String nombre = vista.getTfNombre().getText().trim();
            String telefono = vista.getTfTelefono().getText().trim();
            String estado = (String) vista.getCbEstado().getSelectedItem();

            // Validaciones
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El nombre es obligatorio.");
                return;
            }
            if (telefono.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El teléfono es obligatorio.");
                return;
            }
            if (!telefono.matches("\\d{7,15}")) {
                JOptionPane.showMessageDialog(vista, "El teléfono debe contener entre 7 y 15 dígitos.");
                return;
            }

            // Crear objeto
            Repartidor r = new Repartidor();
            r.setNombre(nombre);
            r.setTelefono(telefono);
            r.setEstado(estado);

            // Usar idEnEdicion 
            if (idEnEdicion != null) {
                // UPDATE
                r.setId(idEnEdicion);
                repartidorDAO.update(r);
                JOptionPane.showMessageDialog(vista, "Repartidor actualizado con éxito.");
            } else {
                // INSERT
                repartidorDAO.save(r);
                JOptionPane.showMessageDialog(vista, "Repartidor guardado con éxito.");
            }

            limpiarFormulario();
            cargarRepartidores();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        vista.getTfNombre().setText("");
        vista.getTfTelefono().setText("");
        vista.getCbEstado().setSelectedIndex(0);
        vista.getTablaRepartidores().clearSelection();
        idEnEdicion = null; // Limpiar el ID en edición
    }

    public void cargarRepartidores() {
        try {
            List<Repartidor> lista = repartidorDAO.findAll();
            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0);

            for (Repartidor r : lista) {
                model.addRow(new Object[]{
                    r.getId(),
                    r.getNombre(),
                    r.getTelefono(),
                    r.getEstado(),
                    "✏️", "🗑️"
                });
            }

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            vista.getTablaRepartidores().setRowSorter(sorter);

            vista.getTablaRepartidores().getColumn("Editar").setCellRenderer(new ButtonRenderer("✏️"));
            vista.getTablaRepartidores().getColumn("Editar").setCellEditor(new ButtonEditorRepartidor(new JCheckBox(), "Editar", this, vista.getTablaRepartidores()));
            vista.getTablaRepartidores().getColumn("Eliminar").setCellRenderer(new ButtonRenderer("🗑️"));
            vista.getTablaRepartidores().getColumn("Eliminar").setCellEditor(new ButtonEditorRepartidor(new JCheckBox(), "Eliminar", this, vista.getTablaRepartidores()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método editarRepartidor
    public void editarRepartidor(int row) {
        try {
            // Convertir índice de la vista al modelo
            int modelRow = vista.getTablaRepartidores().convertRowIndexToModel(row);

            // Obtener datos del modelo (no de la vista)
            int id = (int) vista.getTableModel().getValueAt(modelRow, 0);
            String nombre = (String) vista.getTableModel().getValueAt(modelRow, 1);
            String telefono = (String) vista.getTableModel().getValueAt(modelRow, 2);
            String estado = (String) vista.getTableModel().getValueAt(modelRow, 3);

            //Guardar el ID para usarlo en guardarRepartidor()
            idEnEdicion = id;

            // Cargar datos en el formulario
            vista.getTfNombre().setText(nombre);
            vista.getTfTelefono().setText(telefono);
            vista.getCbEstado().setSelectedItem(estado);

            // Seleccionar la fila en la vista
            vista.getTablaRepartidores().setRowSelectionInterval(row, row);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar repartidor para edición: " + e.getMessage());
        }
    }

    public void eliminarRepartidor(int row) {
        try {
            int modelRow = vista.getTablaRepartidores().convertRowIndexToModel(row);
            int id = (int) vista.getTableModel().getValueAt(modelRow, 0);

            int confirm = JOptionPane.showConfirmDialog(vista,
                    "¿Eliminar al repartidor con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repartidorDAO.delete(id);
                JOptionPane.showMessageDialog(vista, "Repartidor eliminado con éxito.");
                cargarRepartidores();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al eliminar repartidor: " + e.getMessage());
        }
    }

    private void buscarRepartidores() {
        try {
            String nombre = vista.getTfBuscar().getText().trim().toLowerCase();

            List<Repartidor> lista;
            if (!nombre.isEmpty()) {
                lista = repartidorDAO.findByNameLike(nombre);
            } else {
                lista = repartidorDAO.findAll();
            }

            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0);

            for (Repartidor r : lista) {
                model.addRow(new Object[]{
                    r.getId(),
                    r.getNombre(),
                    r.getTelefono(),
                    r.getEstado(),
                    "✏️", "🗑️"
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(vista,
                        "No se encontraron repartidores con ese nombre.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + e.getMessage());
        }
    }
}