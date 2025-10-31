package pe.edu.utp.rudypollo.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import pe.edu.utp.rudypollo.controller.IngredienteControlador;

public class IngredientePanel extends JPanel {
    private JTextField tfNombre, tfCantidad, tfUnidad, tfBuscar;
    private JButton btnGuardar, btnNuevo, btnBuscar;
    private JTable tablaIngredientes;
    private DefaultTableModel tableModel;

    public IngredientePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // PANEL SUPERIOR: FORMULARIO
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Ingredientes"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblCantidad = new JLabel("Cantidad:");
        JLabel lblUnidad = new JLabel("Unidad:");

        tfNombre = new JTextField(15);
        tfCantidad = new JTextField(10);
        tfUnidad = new JTextField(10);

        btnGuardar = new JButton("Guardar");
        btnNuevo = new JButton("Nuevo");

        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(tfNombre, gbc);

        gbc.gridx = 2;
        formPanel.add(lblCantidad, gbc);
        gbc.gridx = 3;
        formPanel.add(tfCantidad, gbc);

        // Fila 2
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblUnidad, gbc);
        gbc.gridx = 1;
        formPanel.add(tfUnidad, gbc);

        gbc.gridx = 2;
        formPanel.add(btnGuardar, gbc);
        gbc.gridx = 3;
        formPanel.add(btnNuevo, gbc);

        // PANEL DE BÚSQUEDA
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        tfBuscar = new JTextField(15);
        btnBuscar = new JButton("Buscar");
        searchPanel.add(new JLabel("Buscar por nombre:"));
        searchPanel.add(tfBuscar);
        searchPanel.add(btnBuscar);

        // PANEL CENTRAL: TABLA
        String[] columnNames = {"ID", "Nombre", "Cantidad", "Unidad", "Editar", "Eliminar"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Solo editar o eliminar
            }
        };
        tablaIngredientes = new JTable(tableModel);
        tablaIngredientes.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tablaIngredientes);

        // Agregar todo al panel principal
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);
    }

    /**
     * Configura las acciones de edición y eliminación en la tabla
     */
    public void configurarAccionesTabla(IngredienteControlador controlador) {
        tablaIngredientes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tablaIngredientes.rowAtPoint(e.getPoint());
                int col = tablaIngredientes.columnAtPoint(e.getPoint());

                if (col == 4) { // ✏️ Editar
                    controlador.editarIngrediente(row);
                } else if (col == 5) { // 🗑️ Eliminar
                    controlador.eliminarIngrediente(row);
                }
            }
        });
    }

    // ======== GETTERS =========
    public JTextField getTfNombre() { return tfNombre; }
    public JTextField getTfCantidad() { return tfCantidad; }
    public JTextField getTfUnidad() { return tfUnidad; }
    public JTextField getTfBuscar() { return tfBuscar; }

    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnNuevo() { return btnNuevo; }
    public JButton getBtnBuscar() { return btnBuscar; }

    public JTable getTablaIngredientes() { return tablaIngredientes; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
