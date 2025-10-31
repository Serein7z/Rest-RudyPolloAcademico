package pe.edu.utp.rudypollo.controller;

import pe.edu.utp.rudypollo.dao.PlatoDAO;
import pe.edu.utp.rudypollo.dao.impl.PlatoDAOImpl;
import pe.edu.utp.rudypollo.model.Plato;
import pe.edu.utp.rudypollo.view.InventarioPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.List;

public class InventarioControlador {
    private InventarioPanel vista;
    private PlatoDAO platoDAO;

    public InventarioControlador(InventarioPanel vista) {
        this.vista = vista;
        this.platoDAO = new PlatoDAOImpl();
        initListeners();
        cargarPlatos();
    }

    private void initListeners() {
        vista.getBtnGuardar().addActionListener(e -> guardarPlato());
        vista.getBtnNuevo().addActionListener(e -> limpiarFormulario());
        vista.getBtnBuscar().addActionListener(e -> buscarPlatos());
    }

    private void guardarPlato() {
        try {
            String nombre = vista.getTfNombre().getText().trim();
            String precioStr = vista.getTfPrecio().getText().trim();
            String stockStr = vista.getTfStock().getText().trim();
            String desc = vista.getTaDescripcion().getText().trim();

            // Validaciones
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El nombre del plato es obligatorio.");
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(precioStr);
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(vista, "El precio debe ser mayor que 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(vista, "Ingrese un precio válido.");
                return;
            }

            int stock;
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) {
                    JOptionPane.showMessageDialog(vista, "El stock no puede ser negativo.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(vista, "Ingrese un stock válido (número entero).");
                return;
            }

            if (nombre.length() > 150) {
                JOptionPane.showMessageDialog(vista, "El nombre no puede tener más de 150 caracteres.");
                return;
            }
            if (desc.length() > 500) {
                JOptionPane.showMessageDialog(vista, "La descripción no puede tener más de 500 caracteres.");
                return;
            }

            Plato p = new Plato();
            p.setNombre(nombre);
            p.setPrecio(precio);
            p.setStock(stock);
            p.setDescripcion(desc);

            int selectedRow = vista.getTablaPlatos().getSelectedRow();


            List<Plato> existentes = platoDAO.findAll();
            for (Plato existente : existentes) {
                int idExistente = existente.getId();
                int idSeleccionado = (selectedRow >= 0)
                        ? (int) vista.getTablaPlatos().getValueAt(selectedRow, 0)
                        : 0;

                if (existente.getNombre().equalsIgnoreCase(nombre)
                        && (selectedRow < 0 || idExistente != idSeleccionado)) {
                    JOptionPane.showMessageDialog(vista, "Ya existe un plato con ese nombre.");
                    return;
                }
            }

            if (selectedRow >= 0) {

                int id = (int) vista.getTablaPlatos().getValueAt(selectedRow, 0);
                p.setId(id);
                platoDAO.update(p);
                JOptionPane.showMessageDialog(vista, "Plato actualizado con éxito.");
            } else {

                platoDAO.save(p);
                JOptionPane.showMessageDialog(vista, "Plato guardado con éxito.");
            }

            limpiarFormulario();
            cargarPlatos();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        vista.getTfNombre().setText("");
        vista.getTfPrecio().setText("");
        vista.getTfStock().setText("");
        vista.getTaDescripcion().setText("");
        vista.getTablaPlatos().clearSelection();
    }

    public void cargarPlatos() {
        try {
            List<Plato> lista = platoDAO.findAll();
            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0);

            for (Plato p : lista) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getNombre(),
                        p.getPrecio(),
                        p.getStock(),
                        p.getDescripcion(),
                        "✏️", "🗑️"
                });
            }


            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            vista.getTablaPlatos().setRowSorter(sorter);

            vista.configurarAccionesTabla(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void editarPlato(int row) {
    try {

        int modelRow = vista.getTablaPlatos().convertRowIndexToModel(row);


        TableRowSorter<?> sorter = (TableRowSorter<?>) vista.getTablaPlatos().getRowSorter();
        vista.getTablaPlatos().setRowSorter(null);

        int id = (int) vista.getTablaPlatos().getModel().getValueAt(modelRow, 0);
        String nombre = (String) vista.getTablaPlatos().getModel().getValueAt(modelRow, 1);
        double precio = Double.parseDouble(vista.getTablaPlatos().getModel().getValueAt(modelRow, 2).toString());
        int stock = Integer.parseInt(vista.getTablaPlatos().getModel().getValueAt(modelRow, 3).toString());
        String desc = (String) vista.getTablaPlatos().getModel().getValueAt(modelRow, 4);

        vista.getTfNombre().setText(nombre);
        vista.getTfPrecio().setText(String.valueOf(precio));
        vista.getTfStock().setText(String.valueOf(stock));
        vista.getTaDescripcion().setText(desc);

        vista.getTablaPlatos().setRowSelectionInterval(row, row);

        vista.getTablaPlatos().setRowSorter(sorter);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(vista, "Error al cargar plato para edición");
    }
}


    public void eliminarPlato(int row) {
    try {
        int modelRow = vista.getTablaPlatos().convertRowIndexToModel(row);
        int id = (int) vista.getTablaPlatos().getModel().getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Eliminar el plato con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            platoDAO.delete(id);
            JOptionPane.showMessageDialog(vista, "Plato eliminado con éxito.");

          
            vista.getTablaPlatos().setRowSorter(null);

            cargarPlatos(); 
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(vista, "Error al eliminar plato");
    }
}




    private void buscarPlatos() {
        try {
            String nombre = vista.getTfBuscar().getText().trim().toLowerCase();
            String precioMinStr = vista.getTfPrecioMin().getText().trim();
            String precioMaxStr = vista.getTfPrecioMax().getText().trim();
            String stockMinStr = vista.getTfStockMin().getText().trim();

            Double precioMin = precioMinStr.isEmpty() ? null : Double.parseDouble(precioMinStr);
            Double precioMax = precioMaxStr.isEmpty() ? null : Double.parseDouble(precioMaxStr);
            Integer stockMin = stockMinStr.isEmpty() ? null : Integer.parseInt(stockMinStr);

   
            List<Plato> lista;
            if (!nombre.isEmpty()) {
                lista = platoDAO.findByNameLike(nombre);
            } else {
                lista = platoDAO.findAll();
            }

            DefaultTableModel model = vista.getTableModel();
            model.setRowCount(0); 

            for (Plato p : lista) {
                boolean match = true;

                if (precioMin != null && p.getPrecio() < precioMin) match = false;
                if (precioMax != null && p.getPrecio() > precioMax) match = false;
                if (stockMin != null && p.getStock() > stockMin) match = false;

                if (match) {
                    model.addRow(new Object[]{
                            p.getId(), p.getNombre(), p.getPrecio(),
                            p.getStock(), p.getDescripcion(), "✏️", "🗑️"
                    });
                }
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(vista,
                        "No se encontraron platos con los filtros aplicados.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }

            vista.configurarAccionesTabla(this);

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            vista.getTablaPlatos().setRowSorter(sorter);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + e.getMessage());
        }
    }
}
