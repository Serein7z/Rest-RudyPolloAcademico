package pe.edu.utp.rudypollo.view;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import pe.edu.utp.rudypollo.controller.InventarioControlador;

public class InventarioPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfNombre, tfPrecio, tfStock, tfBuscar, tfPrecioMin, tfPrecioMax, tfStockMin;
    private JTextArea taDescripcion;
    private JButton btnGuardar, btnNuevo, btnBuscar;

    public InventarioPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 128, 85)); // verde oscuro


        JLabel lblTitle = new JLabel("📦 Inventario", SwingConstants.LEFT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        header.add(lblTitle, BorderLayout.WEST);


        ImageIcon originalIcon = new ImageIcon("src/main/resources/imagenes/LOGO.png");
        Image img = originalIcon.getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH); // ajusta tamaño
        ImageIcon scaledIcon = new ImageIcon(img);

        JLabel lblLogo = new JLabel(scaledIcon);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
        header.add(lblLogo, BorderLayout.EAST);


        add(header, BorderLayout.NORTH);

   
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Agregar/Editar Plato"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        formPanel.add(new JLabel("Nombre del Plato:"), gbc);
        gbc.gridx = 1;
        tfNombre = new JTextField();
        formPanel.add(tfNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        tfPrecio = new JTextField();
        formPanel.add(tfPrecio, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        tfStock = new JTextField();
        formPanel.add(tfStock, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Detalle del Plato:"), gbc);
        gbc.gridx = 1;
        taDescripcion = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(taDescripcion), gbc);

  
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        btnGuardar = new JButton("💾 Guardar Plato");
        btnNuevo = new JButton("➕ Nuevo");

        btnGuardar.setBackground(new Color(0, 180, 100));
        btnGuardar.setForeground(Color.WHITE);
        btnNuevo.setBackground(new Color(200, 255, 200));

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnNuevo);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.WEST);


        tfBuscar = new JTextField(12);
        tfPrecioMin = new JTextField(5);
        tfPrecioMax = new JTextField(5);
        tfStockMin = new JTextField(5);
        btnBuscar = new JButton("🔍 Buscar");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 255, 245));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Búsqueda Avanzada"));

        searchPanel.add(new JLabel("Nombre:"));
        searchPanel.add(tfBuscar);
        searchPanel.add(new JLabel("Precio Min:"));
        searchPanel.add(tfPrecioMin);
        searchPanel.add(new JLabel("Precio Max:"));
        searchPanel.add(tfPrecioMax);
        searchPanel.add(new JLabel("Stock Min:"));
        searchPanel.add(tfStockMin);
        searchPanel.add(btnBuscar);

        add(searchPanel, BorderLayout.SOUTH);


        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Precio", "Stock", "Descripción", "Editar", "Eliminar"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5 || col == 6; 
            }
        };
        table = new JTable(tableModel);

       
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

              
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 255, 235));
                }

              
                try {
                    int stock = Integer.parseInt(table.getValueAt(row, 3).toString());
                    if (stock < 3) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } catch (Exception ignored) {}

                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de Platos"));
        add(scroll, BorderLayout.CENTER);
    }

    
    public void configurarAccionesTabla(InventarioControlador controlador) {
        table.getColumn("Editar").setCellRenderer(new ButtonRenderer("✏️"));
        table.getColumn("Editar").setCellEditor(new ButtonEditor(new JCheckBox(), "Editar", controlador, table));
        table.getColumn("Eliminar").setCellRenderer(new ButtonRenderer("🗑️"));
        table.getColumn("Eliminar").setCellEditor(new ButtonEditor(new JCheckBox(), "Eliminar", controlador, table));
    }

    // 🔹 Getters
    public JTextField getTfNombre() { return tfNombre; }
    public JTextField getTfPrecio() { return tfPrecio; }
    public JTextField getTfStock() { return tfStock; }
    public JTextArea getTaDescripcion() { return taDescripcion; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnNuevo() { return btnNuevo; }
    public JTable getTablaPlatos() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTfBuscar() { return tfBuscar; }
    public JTextField getTfPrecioMin() { return tfPrecioMin; }
    public JTextField getTfPrecioMax() { return tfPrecioMax; }
    public JTextField getTfStockMin() { return tfStockMin; }
    public JButton getBtnBuscar() { return btnBuscar; }
}
