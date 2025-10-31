package pe.edu.utp.rudypollo.controller;

import pe.edu.utp.rudypollo.dao.IngredienteDAO;
import pe.edu.utp.rudypollo.dao.impl.IngredienteDAOImpl;
import pe.edu.utp.rudypollo.dao.impl.RecetaDAOImpl;
import pe.edu.utp.rudypollo.model.Ingrediente;
import pe.edu.utp.rudypollo.model.Receta;
import pe.edu.utp.rudypollo.view.IngredientePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import pe.edu.utp.rudypollo.util.DBConnection;

public class IngredienteControlador {
    private IngredientePanel vista;
    private IngredienteDAO ingredienteDAO;

    public IngredienteControlador(IngredientePanel vista) {
        this.vista = vista;
        this.ingredienteDAO = new IngredienteDAOImpl();
        initListeners();
        cargarIngredientes();
    }

    private void initListeners() {
        vista.getBtnGuardar().addActionListener(e -> guardarIngrediente());
        vista.getBtnNuevo().addActionListener(e -> limpiarFormulario());
        vista.getBtnBuscar().addActionListener(e -> buscarIngredientes());
    }

    private void guardarIngrediente() {
        try {
            String nombre = vista.getTfNombre().getText().trim();
            String cantidadStr = vista.getTfCantidad().getText().trim();
            String unidad = vista.getTfUnidad().getText().trim();

            if (nombre.isEmpty() || cantidadStr.isEmpty() || unidad.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.");
                return;
            }

            double cantidad = Double.parseDouble(cantidadStr);

            Ingrediente ing = new Ingrediente();
            ing.setNombre(nombre);
            ing.setCantidad(cantidad);
            ing.setUnidad(unidad);

            int selectedRow = vista.getTablaIngredientes().getSelectedRow();

            if (selectedRow >= 0) {
                int id = (int) vista.getTablaIngredientes().getValueAt(selectedRow, 0);
                ing.setId(id);
                ingredienteDAO.update(ing);
                JOptionPane.showMessageDialog(vista, "Ingrediente actualizado con éxito.");
            } else {
                ingredienteDAO.save(ing);
                JOptionPane.showMessageDialog(vista, "Ingrediente guardado con éxito.");
            }

            //Recalcular el stock de los platos que usan este ingrediente
            recalcularStockPlatos(ing.getId());

            limpiarFormulario();
            cargarIngredientes();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser un número válido.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        vista.getTfNombre().setText("");
        vista.getTfCantidad().setText("");
        vista.getTfUnidad().setText("");
        vista.getTablaIngredientes().clearSelection();
    }

    public void cargarIngredientes() {
        try {
            List<Ingrediente> lista = ingredienteDAO.findAll();
            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0);
            for (Ingrediente i : lista) {
                model.addRow(new Object[]{
                        i.getId(), i.getNombre(), i.getCantidad(), i.getUnidad(), "✏️", "🗑️"
                });
            }

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            vista.getTablaIngredientes().setRowSorter(sorter);

            vista.configurarAccionesTabla(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editarIngrediente(int row) {
        try {
            int modelRow = vista.getTablaIngredientes().convertRowIndexToModel(row);
            int id = (int) vista.getTablaIngredientes().getModel().getValueAt(modelRow, 0);
            String nombre = (String) vista.getTablaIngredientes().getModel().getValueAt(modelRow, 1);
            double cantidad = Double.parseDouble(vista.getTablaIngredientes().getModel().getValueAt(modelRow, 2).toString());
            String unidad = (String) vista.getTablaIngredientes().getModel().getValueAt(modelRow, 3);

            vista.getTfNombre().setText(nombre);
            vista.getTfCantidad().setText(String.valueOf(cantidad));
            vista.getTfUnidad().setText(unidad);

            vista.getTablaIngredientes().setRowSelectionInterval(row, row);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar ingrediente para edición");
        }
    }

    public void eliminarIngrediente(int row) {
        try {
            int modelRow = vista.getTablaIngredientes().convertRowIndexToModel(row);
            int id = (int) vista.getTablaIngredientes().getModel().getValueAt(modelRow, 0);

            int confirm = JOptionPane.showConfirmDialog(vista,
                    "¿Eliminar el ingrediente con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ingredienteDAO.delete(id);
                JOptionPane.showMessageDialog(vista, "Ingrediente eliminado con éxito.");
                cargarIngredientes();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al eliminar ingrediente");
        }
    }

    private void buscarIngredientes() {
        try {
            String nombre = vista.getTfBuscar().getText().trim().toLowerCase();
            List<Ingrediente> lista = nombre.isEmpty() ?
                    ingredienteDAO.findAll() :
                    ingredienteDAO.findByNameLike(nombre);

            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0);
            for (Ingrediente i : lista) {
                model.addRow(new Object[]{
                        i.getId(), i.getNombre(), i.getCantidad(), i.getUnidad(), "✏️", "🗑️"
                });
            }

            vista.configurarAccionesTabla(this);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + e.getMessage());
        }
    }

    /**
     * Recalcula el stock de todos los platos que usan el ingrediente dado
     * El stock de un plato depende de cuántas unidades completas se pueden preparar
     * con los ingredientes disponibles (mínimo entre todos los ingredientes de la receta)
     */
    private void recalcularStockPlatos(int idIngrediente) {
        try {
            RecetaDAOImpl recetaDAO = new RecetaDAOImpl();
            List<Receta> recetas = recetaDAO.findByIngredienteId(idIngrediente);

            for (Receta receta : recetas) {
                int idPlato = receta.getIdPlato();
                double stockMinimo = Double.MAX_VALUE;

                // Obtener todos los ingredientes que forman ese plato
                List<Receta> ingredientesPlato = recetaDAO.findByPlatoId(idPlato);
                for (Receta r : ingredientesPlato) {
                    Ingrediente ing = ingredienteDAO.findById(r.getIdIngrediente());
                    if (ing != null && r.getCantidadNecesaria() > 0) {
                        double posible = ing.getCantidad() / r.getCantidadNecesaria();
                        if (posible < stockMinimo) {
                            stockMinimo = posible;
                        }
                    }
                }

                // Actualizar el stock del plato (redondeando hacia abajo)
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement("UPDATE platos SET stock = ? WHERE id = ?")) {
                    ps.setInt(1, (int) Math.floor(stockMinimo));
                    ps.setInt(2, idPlato);
                    ps.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
