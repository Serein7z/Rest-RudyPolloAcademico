package pe.edu.utp.rudypollo.view;

import pe.edu.utp.rudypollo.dao.impl.ClienteDAOImpl;
import pe.edu.utp.rudypollo.dao.impl.PlatoDAOImpl;
import pe.edu.utp.rudypollo.model.Cliente;
import pe.edu.utp.rudypollo.model.Pedido;
import pe.edu.utp.rudypollo.model.Plato;
import pe.edu.utp.rudypollo.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Vector;

public class PedidoCreationDialog extends JDialog {

    private JTextField tfClienteNombre, tfClienteDireccion, tfClienteTelefono;

    private JComboBox<String> cbPlatos;
    private JLabel lblPrecio, lblStock, lblTotal;
    private JSpinner spCantidad;
    private JTable itemsTable;
    private DefaultTableModel itemsModel;
    private JButton btnAgregar, btnQuitar, btnGuardar, btnCancelar;

    private Vector<Plato> platosList = new Vector<>();
    private final Runnable onSaved;

    private Pedido pedidoActual;

    public PedidoCreationDialog(Frame owner, Runnable onSaved) {
        super(owner, "Crear / Editar Pedido", true);
        this.onSaved = onSaved;
        initUI();
        setSize(850, 600);
        setLocationRelativeTo(owner);

        loadPlatos();
    }

    private void initUI() {
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(245, 247, 250));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel clientePanel = createCardPanel("🧑 Datos del Cliente");
        clientePanel.setLayout(new GridLayout(3, 2, 6, 6));
        clientePanel.add(new JLabel("Nombre:"));
        tfClienteNombre = new JTextField();
        clientePanel.add(tfClienteNombre);
        clientePanel.add(new JLabel("Dirección:"));
        tfClienteDireccion = new JTextField();
        clientePanel.add(tfClienteDireccion);
        clientePanel.add(new JLabel("Teléfono:"));
        tfClienteTelefono = new JTextField();
        clientePanel.add(tfClienteTelefono);
        leftPanel.add(clientePanel);
        leftPanel.add(Box.createVerticalStrut(15));

        JPanel selector = createCardPanel("🍴 Agregar Plato");
        selector.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        selector.add(new JLabel("Plato:"), gbc);
        gbc.gridx = 1;
        cbPlatos = new JComboBox<>();
        cbPlatos.addActionListener(e -> onPlatoSelected());
        cbPlatos.setPreferredSize(new Dimension(180, 28));
        selector.add(cbPlatos, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        selector.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        lblPrecio = new JLabel("S/ 0.00");
        selector.add(lblPrecio, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        selector.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        lblStock = new JLabel("0");
        selector.add(lblStock, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        selector.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        selector.add(spCantidad, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnAgregar = new JButton("➕ Agregar");
        styleAccentButton(btnAgregar);
        btnAgregar.addActionListener(e -> agregarItem());
        selector.add(btnAgregar, gbc);

        leftPanel.add(selector);

        JPanel rightPanel = createCardPanel("📦 Items del Pedido");
        rightPanel.setLayout(new BorderLayout());

        itemsModel = new DefaultTableModel(
                new Object[]{"PlatoID", "Nombre", "Cantidad", "Precio Unit.", "Subtotal"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        itemsTable = new JTable(itemsModel);
        itemsTable.setRowHeight(28);
        itemsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        itemsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        itemsTable.setShowGrid(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        itemsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        itemsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(itemsTable);
        rightPanel.add(scroll, BorderLayout.CENTER);

        btnQuitar = new JButton("🗑 Quitar seleccionado");
        btnQuitar.setForeground(Color.RED.darker());
        btnQuitar.addActionListener(e -> quitarItem());
        JPanel southRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southRight.add(btnQuitar);
        rightPanel.add(southRight, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(300);
        split.setResizeWeight(0);
        add(split, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottom.setBackground(new Color(250, 250, 250));

        lblTotal = new JLabel("Total: S/ 0.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        bottom.add(lblTotal, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        styleNeutralButton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new JButton("💾 Guardar Pedido");
        stylePrimaryButton(btnGuardar);
        btnGuardar.addActionListener(e -> guardarPedido());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);

        bottom.add(btnPanel, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(new Color(255, 153, 0));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 35));
    }

    private void styleNeutralButton(JButton btn) {
        btn.setBackground(new Color(230, 230, 230));
        btn.setForeground(Color.DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setPreferredSize(new Dimension(120, 35));
    }

    private void styleAccentButton(JButton btn) {
        btn.setBackground(new Color(255, 200, 50));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(180, 35));
    }

    private void loadPlatos() {
        try {
            PlatoDAOImpl pdao = new PlatoDAOImpl();
            List<Plato> lista = pdao.findAll();
            platosList.clear();
            cbPlatos.removeAllItems();

            for (Plato p : lista) {
                if (p.getStock() > 0) {
                    platosList.add(p);
                    cbPlatos.addItem(p.getNombre() + " (S/ " + p.getPrecio() + ")");
                }
            }

            if (!platosList.isEmpty()) {
                cbPlatos.setSelectedIndex(0);
                onPlatoSelected();
            } else {

                lblPrecio.setText("S/ 0.00");
                lblStock.setText("0");
                btnAgregar.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPlatoSelected() {
        int idx = cbPlatos.getSelectedIndex();
        if (idx >= 0 && idx < platosList.size()) {
            Plato p = platosList.get(idx);
            lblPrecio.setText(String.format("%.2f", p.getPrecio()));
            lblStock.setText(String.valueOf(p.getStock()));
            spCantidad.setModel(new SpinnerNumberModel(1, 1, Math.max(1, p.getStock()), 1));
        }
    }

    private void agregarItem() {
        int idx = cbPlatos.getSelectedIndex();
        if (idx < 0) {
            return;
        }
        Plato p = platosList.get(idx);
        int qty = (Integer) spCantidad.getValue();
        if (qty > p.getStock()) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente.");
            return;
        }
        itemsModel.addRow(new Object[]{
            p.getId(), p.getNombre(), qty, p.getPrecio(), qty * p.getPrecio()
        });
        actualizarTotal();
    }

    private void quitarItem() {
        int r = itemsTable.getSelectedRow();
        if (r >= 0) {
            itemsModel.removeRow(r);
            actualizarTotal();
        }
    }

    private void actualizarTotal() {
        double total = 0;
        for (int r = 0; r < itemsModel.getRowCount(); r++) {
            total += Double.parseDouble(itemsModel.getValueAt(r, 4).toString());
        }
        lblTotal.setText(String.format("Total: S/ %.2f", total));
    }

    private void guardarPedido() {
        String nombre = tfClienteNombre.getText().trim();
        String direccion = tfClienteDireccion.getText().trim();
        String telefono = tfClienteTelefono.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los datos del cliente.");
            return;
        }
        if (itemsModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un plato.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            boolean prevAuto = con.getAutoCommit();
            con.setAutoCommit(false);

            ClienteDAOImpl cdao = new ClienteDAOImpl();
            int clienteId;
            Cliente clienteExist = cdao.findByTelefono(telefono);
            if (clienteExist == null) {
                clienteExist = cdao.findByNombre(nombre);
            }

            if (clienteExist == null) {
                clienteId = cdao.save(new Cliente(0, nombre, direccion, telefono), con);
            } else {
                clienteExist.setNombre(nombre);
                clienteExist.setDireccion(direccion);
                clienteExist.setTelefono(telefono);
                cdao.update(clienteExist, con);
                clienteId = clienteExist.getId();
            }

            double total = 0;
            for (int r = 0; r < itemsModel.getRowCount(); r++) {
                total += Double.parseDouble(itemsModel.getValueAt(r, 4).toString());
            }

            int pedidoId;
            if (pedidoActual == null) {

                String sqlPedido = "INSERT INTO pedidos (cliente_id,total,estado) VALUES (?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, clienteId);
                    ps.setDouble(2, total);
                    ps.setString(3, "CREADO");
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se generó ID de pedido.");
                        }
                        pedidoId = rs.getInt(1);
                    }
                }
            } else {

                pedidoId = pedidoActual.getId();

                try (PreparedStatement ps = con.prepareStatement("SELECT plato_id,cantidad FROM pedido_items WHERE pedido_id=?")) {
                    ps.setInt(1, pedidoId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            try (PreparedStatement psUpd = con.prepareStatement("UPDATE platos SET stock=stock+? WHERE id=?")) {
                                psUpd.setInt(1, rs.getInt("cantidad"));
                                psUpd.setInt(2, rs.getInt("plato_id"));
                                psUpd.executeUpdate();
                            }
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM pedido_items WHERE pedido_id=?")) {
                    ps.setInt(1, pedidoId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("UPDATE pedidos SET cliente_id=?, total=? WHERE id=?")) {
                    ps.setInt(1, clienteId);
                    ps.setDouble(2, total);
                    ps.setInt(3, pedidoId);
                    ps.executeUpdate();
                }
            }

            String sqlSel = "SELECT stock FROM platos WHERE id=? FOR UPDATE";
            String sqlUpd = "UPDATE platos SET stock=? WHERE id=?";
            String sqlIns = "INSERT INTO pedido_items (pedido_id,plato_id,cantidad,precio_unit) VALUES (?,?,?,?)";

            for (int r = 0; r < itemsModel.getRowCount(); r++) {
                int platoId = (int) itemsModel.getValueAt(r, 0);
                int cant = (int) itemsModel.getValueAt(r, 2);
                double precio = Double.parseDouble(itemsModel.getValueAt(r, 3).toString());

                int stock;
                try (PreparedStatement psSel = con.prepareStatement(sqlSel)) {
                    psSel.setInt(1, platoId);
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Plato no encontrado id=" + platoId);
                        }
                        stock = rs.getInt("stock");
                    }
                }
                if (stock < cant) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this, "Stock insuficiente para plato " + platoId);
                    return;
                }

                try (PreparedStatement psIns = con.prepareStatement(sqlIns)) {
                    psIns.setInt(1, pedidoId);
                    psIns.setInt(2, platoId);
                    psIns.setInt(3, cant);
                    psIns.setDouble(4, precio);
                    psIns.executeUpdate();
                }

                try (PreparedStatement psUpd = con.prepareStatement(sqlUpd)) {
                    psUpd.setInt(1, stock - cant);
                    psUpd.setInt(2, platoId);
                    psUpd.executeUpdate();
                    // 🔽 Nuevo código: descontar ingredientes usados
                    PlatoDAOImpl pdao = new PlatoDAOImpl();
                    pdao.descontarIngredientesPorVenta(platoId, cant, con);

                }
            }

            con.commit();
            con.setAutoCommit(prevAuto);
            JOptionPane.showMessageDialog(this, "Pedido guardado con éxito.");
            if (onSaved != null) {
                onSaved.run();
            }
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar pedido: " + ex.getMessage());
        }
    }

    public void setPedido(Pedido p) {
        this.pedidoActual = p;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM clientes WHERE id=?")) {
            ps.setInt(1, p.getClienteId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tfClienteNombre.setText(rs.getString("nombre"));
                    tfClienteDireccion.setText(rs.getString("direccion"));
                    tfClienteTelefono.setText(rs.getString("telefono"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT pi.*,pl.nombre FROM pedido_items pi JOIN platos pl ON pi.plato_id=pl.id WHERE pi.pedido_id=?")) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                itemsModel.setRowCount(0);
                while (rs.next()) {
                    itemsModel.addRow(new Object[]{
                        rs.getInt("plato_id"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unit"),
                        rs.getInt("cantidad") * rs.getDouble("precio_unit")
                    });
                }
                actualizarTotal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
