package pe.edu.utp.rudypollo.view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class InicioPanel extends JPanel {

    public InicioPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior - Bienvenida
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panel central - Cards informativos
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        centerPanel.setOpaque(false);

        centerPanel.add(createInfoCard("📦 Pedidos", "Gestiona todos los pedidos", 
            "Crea, edita y asigna pedidos a repartidores", new Color(52, 152, 219)));
        centerPanel.add(createInfoCard("🍗 Inventario", "Control de productos", 
            "Administra el stock de platos disponibles", new Color(46, 204, 113)));
        centerPanel.add(createInfoCard("🚴 Repartidores", "Personal de entrega", 
            "Gestiona el equipo de repartidores", new Color(230, 126, 34)));
        centerPanel.add(createInfoCard("📊 Reportes", "Análisis y estadísticas", 
            "Visualiza el rendimiento del negocio", new Color(155, 89, 182)));

        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior - Información adicional
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("¡Bienvenido a RudyPollo!");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(255, 165, 0));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Pedidos");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblSubtitulo);

        return panel;
    }

    private JPanel createInfoCard(String titulo, String subtitulo, String descripcion, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Título con icono
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(color);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblSubtitulo.setForeground(new Color(50, 50, 50));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descripción
        JTextArea txtDescripcion = new JTextArea(descripcion);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescripcion.setForeground(new Color(100, 100, 100));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setOpaque(false);
        txtDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblSubtitulo);
        card.add(Box.createVerticalStrut(8));
        card.add(txtDescripcion);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblInfo = new JLabel("💡 Usa el menú lateral para navegar entre las diferentes secciones del sistema");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblInfo.setForeground(new Color(100, 100, 100));

        panel.add(lblInfo, BorderLayout.CENTER);

        return panel;
    }
}