package pe.edu.utp.rudypollo.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import pe.edu.utp.rudypollo.controller.InventarioControlador;
import pe.edu.utp.rudypollo.controller.PedidoControlador;
import pe.edu.utp.rudypollo.controller.RepartidorControlador;
import pe.edu.utp.rudypollo.controller.IngredienteControlador;
import pe.edu.utp.rudypollo.model.Usuario;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JButton bInicio, bPedidos, bInventario, bRepartidores, bIngredientes, bReportes;
    private Usuario loggedUser;

    public MainFrame() {
        this(null);
    }

    public MainFrame(Usuario user) {
        super("RudyPollo - Sistema de Pedidos");
        this.loggedUser = user;
        initUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        if (user != null) {
            setTitle(getTitle() + " - " + user.getUsername() + " (" + user.getRoleName() + ")");
            applyRolePermissions(user.getRoleName());
        }
    }

    private void initUI() {

        // Panel del título
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("RudyPollo", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255, 165, 0));
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setMaximumSize(new Dimension(200, 60));

        // Panel lateral de botones
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        // Crear botones
        bInicio = createButton("Inicio", "home.png");
        bPedidos = createButton("Pedidos", "Pedidos.png");
        bInventario = createButton("Inventario", "Inventario.png");
        bIngredientes = createButton("Ingredientes", "Inventario.png"); // nuevo botón
        bRepartidores = createButton("Repartidores", "RepartidorIcon.png");
        bReportes = createButton("Reportes", "Reportes.png");

        // Alinear botones
        bInicio.setAlignmentX(Component.CENTER_ALIGNMENT);
        bPedidos.setAlignmentX(Component.CENTER_ALIGNMENT);
        bInventario.setAlignmentX(Component.CENTER_ALIGNMENT);
        bIngredientes.setAlignmentX(Component.CENTER_ALIGNMENT);
        bRepartidores.setAlignmentX(Component.CENTER_ALIGNMENT);
        bReportes.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar botones al panel
        buttonsPanel.add(bInicio);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(bPedidos);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(bInventario);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(bIngredientes);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(bRepartidores);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(bReportes);

        JPanel side = new JPanel(new BorderLayout());
        side.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        side.add(titlePanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(buttonsPanel);
        side.add(centerWrapper, BorderLayout.CENTER);

        // Panel de pestañas principal
        tabbedPane = new JTabbedPane();

        InicioPanel inicioPanel = new InicioPanel();
        tabbedPane.addTab("Inicio", inicioPanel);

        PedidoPanel pedidoPanel = new PedidoPanel();
        PedidoControlador pedidoControlador = new PedidoControlador(pedidoPanel);
        tabbedPane.addTab("Pedidos", pedidoPanel);

        InventarioPanel inventarioPanel = new InventarioPanel();
        InventarioControlador inventarioControlador = new InventarioControlador(inventarioPanel);
        inventarioPanel.configurarAccionesTabla(inventarioControlador);
        tabbedPane.addTab("Inventario", inventarioPanel);

        // Nuevo panel Ingredientes
        IngredientePanel ingredientePanel = new IngredientePanel();
        IngredienteControlador ingredienteControlador = new IngredienteControlador(ingredientePanel);
        tabbedPane.addTab("Ingredientes", ingredientePanel);

        RepartidorPanel repartidorPanel = new RepartidorPanel();
        RepartidorControlador repartidorControlador = new RepartidorControlador(repartidorPanel);
        tabbedPane.addTab("Repartidores", repartidorPanel);

        DashboardPanel dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);

        // Colores al cambiar de pestaña
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (i == index) {
                    tabbedPane.setBackgroundAt(i, new Color(52, 152, 219));
                    tabbedPane.setForegroundAt(i, Color.WHITE);
                } else {
                    tabbedPane.setBackgroundAt(i, Color.LIGHT_GRAY);
                    tabbedPane.setForegroundAt(i, Color.BLACK);
                }
            }
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof InventarioPanel) {
                inventarioControlador.cargarPlatos();
            } else if (selected instanceof IngredientePanel) {
                ingredienteControlador.cargarIngredientes();
            } else if (selected instanceof PedidoPanel) {
                pedidoControlador.cargarPedidos();
            } else if (selected instanceof RepartidorPanel) {
                repartidorControlador.cargarRepartidores();
            }
        });

        // Acciones de botones
        bInicio.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            setSelectedButton(bInicio);
        });
        bPedidos.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            setSelectedButton(bPedidos);
        });
        bInventario.addActionListener(e -> {
            tabbedPane.setSelectedIndex(2);
            setSelectedButton(bInventario);
        });
        bIngredientes.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
            setSelectedButton(bIngredientes);
        });
        bRepartidores.addActionListener(e -> {
            tabbedPane.setSelectedIndex(4);
            setSelectedButton(bRepartidores);
        });
        bReportes.addActionListener(e -> {
            tabbedPane.setSelectedIndex(5);
            setSelectedButton(bReportes);
        });

        // Estructura general
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, side, tabbedPane);
        split.setDividerLocation(180);
        add(split, BorderLayout.CENTER);
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        URL iconURL = getClass().getResource("/iconos/" + iconPath);

        if (iconURL == null) {
            System.err.println("Error: No se pudo cargar el icono: " + iconPath);
            iconURL = getClass().getResource("/iconos/default_icon.png");
        }

        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        }

        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(160, 50));
        button.setBackground(new Color(244, 244, 244));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isButtonSelected(button)) {
                    button.setBackground(new Color(244, 244, 244));
                    button.setForeground(Color.BLACK);
                }
            }
        });
        return button;
    }

    private boolean isButtonSelected(JButton button) {
        return button.getBackground().equals(new Color(52, 152, 219));
    }

    private void setSelectedButton(JButton selectedButton) {
        JButton[] buttons = {bInicio, bPedidos, bInventario, bIngredientes, bRepartidores, bReportes};
        for (JButton b : buttons) {
            b.setBackground(new Color(244, 244, 244));
            b.setForeground(Color.BLACK);
        }
        selectedButton.setBackground(new Color(52, 152, 219));
        selectedButton.setForeground(Color.WHITE);
    }

    private void applyRolePermissions(String role) {
        if (role == null) {
            return;
        }
        if (!role.equalsIgnoreCase("ADMIN")) {
            bInventario.setEnabled(false);
            bIngredientes.setEnabled(false);
        }
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public Usuario getLoggedUser() {
        return loggedUser;
    }
}
