package pe.edu.utp.rudypollo.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PedidoPanel extends JPanel {

    private JTable tablaPedidos;
    private DefaultTableModel tableModel;
    private JTextField tfBuscar;
    private JButton btnBuscar, btnNuevoPedido, btnActualizar;

    public PedidoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); 


        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 128, 85));

        JLabel lblTitle = new JLabel("📝 Pedido", SwingConstants.LEFT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        header.add(lblTitle, BorderLayout.WEST);

        
        ImageIcon originalIcon = null;
        try {
            originalIcon = new ImageIcon(getClass().getResource("/imagenes/LOGO.png"));
        } catch (Exception e) {
            System.err.println("⚠️ No se encontró el logo en /imagenes/LOGO.png");
        }

        if (originalIcon != null) {
            Image img = originalIcon.getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(img);
            JLabel lblLogo = new JLabel(scaledIcon);
            lblLogo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
            header.add(lblLogo, BorderLayout.EAST);
        }

    
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        topPanel.setBackground(new Color(245, 247, 250));

     
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        tfBuscar = new JTextField(" Buscar...", 20);
        tfBuscar.setForeground(Color.GRAY);
        tfBuscar.setPreferredSize(new Dimension(200, 30));
        tfBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        btnBuscar = new JButton("🔍");
        styleNeutralButton(btnBuscar);

        searchPanel.add(tfBuscar);
        searchPanel.add(btnBuscar);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        btnNuevoPedido = new JButton("➕ Nuevo Pedido");
        styleAccentButton(btnNuevoPedido);

        btnActualizar = new JButton("⟳ Actualizar");
        styleNeutralButton(btnActualizar);

        actionPanel.add(btnNuevoPedido);
        actionPanel.add(btnActualizar);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        
        JPanel containerTop = new JPanel(new BorderLayout());
        containerTop.add(header, BorderLayout.NORTH);
        containerTop.add(topPanel, BorderLayout.SOUTH);

        add(containerTop, BorderLayout.NORTH);

 
        String[] columnas = {
                "ID", "Cliente", "Total", "Estado", "Repartidor", "ETA", "Creado en",
                "Editar", "Eliminar", "Acción"
        };

        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 7; // Solo acciones editables
            }
        };

        tablaPedidos = new JTable(tableModel);
        tablaPedidos.setRowHeight(32);
        tablaPedidos.setShowGrid(false);
        tablaPedidos.setFillsViewportHeight(true);
        tablaPedidos.setIntercellSpacing(new Dimension(0, 0));

    
        tablaPedidos.getTableHeader().setBackground(new Color(240, 240, 240));
        tablaPedidos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaPedidos.getTableHeader().setPreferredSize(new Dimension(0, 35));

        
        DefaultTableCellRenderer zebraRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                
                if (column == 3 && value != null) {
                    String estado = value.toString();
                    JLabel lbl = new JLabel(estado, SwingConstants.CENTER);
                    lbl.setOpaque(true);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                    lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                    switch (estado) {
                        case "CREADO" -> lbl.setBackground(new Color(173, 216, 255));
                        case "ASIGNADO" -> lbl.setBackground(new Color(255, 230, 150));
                        case "ENTREGADO" -> lbl.setBackground(new Color(180, 235, 180));
                        default -> lbl.setBackground(Color.LIGHT_GRAY);
                    }
                    return lbl;
                }

              
                if (column >= 7 && value != null) {
                    JLabel link = new JLabel(value.toString(), SwingConstants.CENTER);
                    link.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    link.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    if (value.toString().equalsIgnoreCase("Editar")) {
                        link.setForeground(new Color(0, 102, 204));
                    } else if (value.toString().equalsIgnoreCase("Eliminar")) {
                        link.setForeground(Color.RED);
                    } else {
                        link.setForeground(new Color(0, 153, 51));
                    }
                    return link;
                }

                return c;
            }
        };

        for (int i = 0; i < tablaPedidos.getColumnCount(); i++) {
            tablaPedidos.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }

        JScrollPane scroll = new JScrollPane(tablaPedidos);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        add(scroll, BorderLayout.CENTER);
    }

  
    private void styleAccentButton(JButton btn) {
        btn.setBackground(new Color(255, 145, 0));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleNeutralButton(JButton btn) {
        btn.setBackground(new Color(240, 240, 240));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    
    public JTable getTablaPedidos() { return tablaPedidos; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTfBuscar() { return tfBuscar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnNuevoPedido() { return btnNuevoPedido; }
    public JButton getBtnActualizar() { return btnActualizar; }
}
