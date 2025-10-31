package pe.edu.utp.rudypollo.view;

import pe.edu.utp.rudypollo.dao.impl.PedidoDAOImpl;
import pe.edu.utp.rudypollo.dao.impl.RepartidorDAOImpl;
import pe.edu.utp.rudypollo.model.Pedido;
import pe.edu.utp.rudypollo.model.Repartidor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AsignarRepartidorDialog extends JDialog {

    private JComboBox<Repartidor> cbRepartidores;
    private JTextField tfEta;
    private JButton btnGuardar, btnCancelar;

    private Pedido pedido;
    private Runnable onSaved;

    public AsignarRepartidorDialog(Frame owner, Pedido pedido, Runnable onSaved) {
        super(owner, "Asignar Repartidor", true);
        this.pedido = pedido;
        this.onSaved = onSaved;
        initUI();
        setSize(400, 250);
        setLocationRelativeTo(owner);
        cargarRepartidores();
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel center = new JPanel(new GridLayout(3, 2, 6, 6));
        center.setBorder(BorderFactory.createTitledBorder("Asignación"));

        center.add(new JLabel("Repartidor:"));
        cbRepartidores = new JComboBox<>();
        center.add(cbRepartidores);

        center.add(new JLabel("Tiempo estimado (ETA en minutos):"));
        tfEta = new JTextField();
        center.add(tfEta);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarAsignacion());
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        bottom.add(btnCancelar);
        bottom.add(btnGuardar);

        add(bottom, BorderLayout.SOUTH);
    }

    private void cargarRepartidores() {
        try {
            RepartidorDAOImpl rdao = new RepartidorDAOImpl();
            List<Repartidor> lista = rdao.findAll();
            cbRepartidores.removeAllItems();
            for (Repartidor r : lista) {
                if ("DISPONIBLE".equalsIgnoreCase(r.getEstado())) {
                    cbRepartidores.addItem(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando repartidores");
        }
    }

   private void guardarAsignacion() {
    Repartidor repartidor = (Repartidor) cbRepartidores.getSelectedItem();
    if (repartidor == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un repartidor");
        return;
    }
    try {
        PedidoDAOImpl pdao = new PedidoDAOImpl();
        RepartidorDAOImpl rdao = new RepartidorDAOImpl();

        // Actualizar pedido
        pedido.setAssignedRepartidorId(repartidor.getId()); // 
        try {
            pedido.setEta(Integer.parseInt(tfEta.getText().trim()));
        } catch (NumberFormatException e) {
    
            System.out.println("Por favor, ingresa un número válido para el ETA.");
        }

        pedido.setEstado("ASIGNADO");
        pdao.update(pedido);

        // Actualizar repartidor
        repartidor.setEstado("EN_RUTA");
        rdao.update(repartidor);

        JOptionPane.showMessageDialog(this, "Repartidor asignado con éxito.");

        //Refrescar inmediatamente la tabla de pedidos
        if (onSaved != null) {
            onSaved.run();
        }

        dispose();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error guardando asignación: " + e.getMessage());
    }
}
}
