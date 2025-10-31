package pe.edu.utp.rudypollo.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import pe.edu.utp.rudypollo.controller.RepartidorControlador;

public class RepartidorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfNombre, tfTelefono, tfBuscar;
    private JComboBox<String> cbEstado;
    private JButton btnGuardar, btnNuevo, btnBuscar, btnRefrescar;

    public RepartidorPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 247, 250)); // fondo suave del dashboard

        // -------- HEADER (logo + título) --------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 128, 85)); // verde oscuro

        JLabel lblTitle = new JLabel("🚴 Gestión de Repartidores", SwingConstants.LEFT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        header.add(lblTitle, BorderLayout.WEST);

        ImageIcon originalIcon = new ImageIcon("src/main/resources/imagenes/LOGO.png");
        Image img = originalIcon.getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);

        JLabel lblLogo = new JLabel(scaledIcon);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
        header.add(lblLogo, BorderLayout.EAST);

        // -------- FORMULARIO --------
        JPanel formPanel = new RoundedPanel(14, new Color(255, 255, 255));
        formPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lNombre = new JLabel("Nombre:");
        lNombre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(lNombre);

        tfNombre = new JTextField(12);
        styleInput(tfNombre);
        formPanel.add(tfNombre);

        JLabel lTelefono = new JLabel("Teléfono:");
        lTelefono.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(lTelefono);

        tfTelefono = new JTextField(12);
        styleInput(tfTelefono);
        formPanel.add(tfTelefono);

        JLabel lEstado = new JLabel("Estado:");
        lEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(lEstado);

        cbEstado = new JComboBox<>(new String[]{"DISPONIBLE", "EN_RUTA", "NO_DISPONIBLE"});
        cbEstado.setPreferredSize(new Dimension(150, 30));
        cbEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cbEstado);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        btnGuardar = new JButton("💾 Guardar");
        btnNuevo = new JButton("🆕 Nuevo");
        btnRefrescar = new JButton("🔄 Refrescar");

        stylePrimaryButton(btnGuardar);
        styleAccentButton(btnNuevo);
        styleNeutralButton(btnRefrescar);

        btnPanel.add(btnRefrescar);
        btnPanel.add(btnGuardar);
        btnPanel.add(btnNuevo);

        formPanel.add(btnPanel);

        // -------- PANEL DE BÚSQUEDA --------
        JPanel searchPanel = new RoundedPanel(12, new Color(255,255,255));
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        tfBuscar = new JTextField(15);
        styleInput(tfBuscar);
        btnBuscar = new JButton("🔍 Buscar");
        styleAccentButton(btnBuscar);
        btnBuscar.setToolTipText("Buscar por nombre");

        searchPanel.add(new JLabel("Nombre:"));
        searchPanel.add(tfBuscar);
        searchPanel.add(btnBuscar);

        // -------- AGRUPAR HEADER + FORMULARIO + BÚSQUEDA --------
        JPanel northWrapper = new JPanel();
        northWrapper.setLayout(new BoxLayout(northWrapper, BoxLayout.Y_AXIS));
        northWrapper.setBackground(new Color(245, 247, 250));

        northWrapper.add(header);
        northWrapper.add(Box.createVerticalStrut(10));
        northWrapper.add(formPanel);
        northWrapper.add(Box.createVerticalStrut(8));
        northWrapper.add(searchPanel);

        add(northWrapper, BorderLayout.NORTH);

        // -------- TABLA --------
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Teléfono", "Estado", "Editar", "Eliminar"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4 || col == 5;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader headerTable = table.getTableHeader();
        headerTable.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerTable.setBackground(new Color(250, 250, 250));
        headerTable.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,230,230)));
        headerTable.setReorderingAllowed(false);
        headerTable.setPreferredSize(new Dimension(100, 36));

        table.setDefaultRenderer(Object.class, new AlternateRowRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new EstadoRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(4).setMaxWidth(70);
        table.getColumnModel().getColumn(5).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
    }

    public void configurarAccionesTabla(RepartidorControlador controlador) {
        table.getColumn("Editar").setCellRenderer(new ButtonRenderer("✏️"));
        table.getColumn("Editar").setCellEditor(new ButtonEditorRepartidor(new JCheckBox(), "Editar", controlador, table));

        table.getColumn("Eliminar").setCellRenderer(new ButtonRenderer("🗑️"));
        table.getColumn("Eliminar").setCellEditor(new ButtonEditorRepartidor(new JCheckBox(), "Eliminar", controlador, table));
    }

    // ----- Getters -----
    public JTextField getTfNombre() { return tfNombre; }
    public JTextField getTfTelefono() { return tfTelefono; }
    public JComboBox<String> getCbEstado() { return cbEstado; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnNuevo() { return btnNuevo; }
    public JTable getTablaRepartidores() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTfBuscar() { return tfBuscar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnRefrescar() { return btnRefrescar; }

    // ----- Helpers de estilo -----
    private void styleInput(JTextField tf) {
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        tf.setPreferredSize(new Dimension(120, 30));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setBackground(new Color(250, 250, 250));
    }

    private void stylePrimaryButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(new Color(74, 82, 89));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
    }

    private void styleAccentButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(new Color(243, 136, 30));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
    }

    private void styleNeutralButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBackground(new Color(245,245,245));
        b.setForeground(new Color(60,60,60));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
    }

    // ----- Renderizadores -----
    private class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String val = value == null ? "" : value.toString();
            JLabel lbl = new JLabel(displayText(val), SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            lbl.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
            lbl.setForeground(new Color(45,45,45));

            switch (val) {
                case "DISPONIBLE":
                    lbl.setBackground(new Color(214, 249, 217));
                    break;
                case "EN_RUTA":
                    lbl.setBackground(new Color(217, 229, 255));
                    break;
                case "NO_DISPONIBLE":
                    lbl.setBackground(new Color(255, 221, 221));
                    break;
                default:
                    lbl.setBackground(new Color(240,240,240));
                    break;
            }
            return lbl;
        }

        private String displayText(String raw) {
            if ("EN_RUTA".equals(raw)) return "EN RUTA";
            if ("NO_DISPONIBLE".equals(raw)) return "NO DISPONIBLE";
            return raw;
        }
    }

    private class AlternateRowRenderer extends DefaultTableCellRenderer {
        private final Color even = Color.WHITE;
        private final Color odd = new Color(250, 251, 253);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                c.setBackground(new Color(220, 235, 255));
            } else {
                c.setBackground((row % 2 == 0) ? even : odd);
            }
            c.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
            return c;
        }
    }

    // Panel redondeado
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            cornerRadius = radius;
            backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.setColor(backgroundColor);
            g2.fill(round);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
