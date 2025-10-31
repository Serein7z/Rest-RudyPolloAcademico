/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.utp.rudypollo.view;

/**
 *
 * @author Vinz
 */
import pe.edu.utp.rudypollo.dao.UsuarioDAO;
import pe.edu.utp.rudypollo.dao.impl.UsuarioDAOImpl;
import pe.edu.utp.rudypollo.model.Usuario;
import pe.edu.utp.rudypollo.util.AuthUtil;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblForgotPassword;
    private JLabel lblStatus;

    public LoginFrame() {
        super("RudyPollo - Login");
        initUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        // Paleta de colores
        Color bgColor = new Color(11, 29, 22);   // fondo oscuro
        Color fieldColor = new Color(20, 40, 30); // campos
        Color btnColor = new Color(0, 230, 118);  // verde brillante
        Color textColor = Color.WHITE;
        Color hintColor = new Color(200, 200, 200);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        root.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));
        add(root);

        // Logo / título
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel lblTitle = new JLabel("RudyPollo");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(textColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Gestión de Pedidos");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblSubtitle.setForeground(hintColor);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(5));
        header.add(lblSubtitle);
        root.add(header, BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        tfUsername = new JTextField();
        styleField(tfUsername, fieldColor, textColor, "Usuario");

        pfPassword = new JPasswordField();
        styleField(pfPassword, fieldColor, textColor, "Contraseña");

        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(btnColor);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

       // 🔽 Nuevo botón
        JButton btnRegister = new JButton("Registrar Usuario");
        btnRegister.setBackground(new Color(40, 180, 99)); // Verde distinto
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(Box.createVerticalStrut(40));
        form.add(tfUsername);
        form.add(Box.createVerticalStrut(15));
        form.add(pfPassword);
        form.add(Box.createVerticalStrut(25));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(10));
        form.add(btnRegister);   // 🔽 Añadido aquí
        form.add(Box.createVerticalStrut(20));
        form.add(lblStatus);

        root.add(form, BorderLayout.CENTER);

        // Eventos
        btnLogin.addActionListener(e -> attemptLogin());
        pfPassword.addActionListener(e -> attemptLogin()); // Enter en password

        // Abrir ventana de registro
        btnRegister.addActionListener(e -> {
           dispose(); // cierra login
           SwingUtilities.invokeLater(() -> new RegisterUserFrame().setVisible(true));
        });
}
    private void styleField(JTextField field, Color bg, Color fg, String placeholder) {
        field.setBackground(bg);
        field.setForeground(fg);
        field.setCaretColor(fg);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.putClientProperty("JComponent.roundRect", true); // FlatLaf soporta esquinas redondeadas
        field.putClientProperty("JTextField.placeholderText", placeholder);
    }

    private void attemptLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Ingrese usuario y contraseña.");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAOImpl();
            Usuario usuario = dao.findByUsername(username);
            if (usuario == null) {
                lblStatus.setText("Usuario no encontrado.");
                return;
            }
            if (!AuthUtil.verifyPassword(password, usuario.getPasswordHash())) {
                lblStatus.setText("Contraseña incorrecta.");
                return;
            }
            String role = usuario.getRoleName();
            if (!(role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("CAJERO"))) {
                lblStatus.setText("Acceso denegado para el rol: " + role);
                return;
            }

           
            SwingUtilities.invokeLater(() -> {
                MainFrame main = new MainFrame(usuario);
                main.setVisible(true);
                dispose();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
