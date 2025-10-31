/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.utp.rudypollo.view;

/**
 *
 * @author Vinz
 */
import pe.edu.utp.rudypollo.dao.impl.UsuarioDAOImpl;
import pe.edu.utp.rudypollo.model.Usuario;
import pe.edu.utp.rudypollo.util.AuthUtil;

import javax.swing.*;
import java.awt.*;

public class RegisterUserFrame extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword, pfConfirm;
    private JComboBox<String> cbRole;
    private JLabel lblStatus;

    public RegisterUserFrame() {
        super("Registrar Nuevo Usuario");
        initUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 550);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        // Colores
        Color bgColor = new Color(11, 29, 22);
        Color fieldColor = new Color(20, 40, 30);
        Color btnColor = new Color(0, 230, 118);
        Color textColor = Color.WHITE;
        Color hintColor = new Color(200, 200, 200);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        add(root);

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel lblTitle = new JLabel("Registrar Nuevo Usuario");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(textColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistema de Gestión RudyPollo");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(hintColor);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(5));
        header.add(lblSubtitle);
        root.add(header, BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        tfUsername = new JTextField();
        styleField(tfUsername, fieldColor, textColor, "Usuario");

        pfPassword = new JPasswordField();
        styleField(pfPassword, fieldColor, textColor, "Contraseña");

        pfConfirm = new JPasswordField();
        styleField(pfConfirm, fieldColor, textColor, "Confirmar Contraseña");

        cbRole = new JComboBox<>(new String[]{"ADMIN", "CAJERO"});
        cbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        cbRole.setBackground(fieldColor);
        cbRole.setForeground(textColor);

        JButton btnRegister = new JButton("Registrar");
        btnRegister.setBackground(btnColor);
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(fieldColor);
        btnCancel.setForeground(textColor);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(Box.createVerticalStrut(30));
        form.add(tfUsername);
        form.add(Box.createVerticalStrut(15));
        form.add(pfPassword);
        form.add(Box.createVerticalStrut(15));
        form.add(pfConfirm);
        form.add(Box.createVerticalStrut(15));
        form.add(cbRole);
        form.add(Box.createVerticalStrut(25));
        form.add(btnRegister);
        form.add(Box.createVerticalStrut(10));
        form.add(btnCancel);
        form.add(Box.createVerticalStrut(20));
        form.add(lblStatus);

        root.add(form, BorderLayout.CENTER);

        // Eventos
        btnRegister.addActionListener(e -> saveUser());
        btnCancel.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
    }

    private void styleField(JTextField field, Color bg, Color fg, String placeholder) {
        field.setBackground(bg);
        field.setForeground(fg);
        field.setCaretColor(fg);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.putClientProperty("JComponent.roundRect", true);
        field.putClientProperty("JTextField.placeholderText", placeholder);
    }

    private void saveUser() {
       String username = tfUsername.getText().trim();
    String password = new String(pfPassword.getPassword());
    String confirm = new String(pfConfirm.getPassword());
    String role = (String) cbRole.getSelectedItem();

    if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
        lblStatus.setText("Todos los campos son obligatorios.");
        return;
    }
    if (!password.equals(confirm)) {
        lblStatus.setText("Las contraseñas no coinciden.");
        return;
    }

    try {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(AuthUtil.hashPassword(password));
        usuario.setRoleName(role);

        UsuarioDAOImpl dao = new UsuarioDAOImpl();
        dao.save(usuario);

        JOptionPane.showMessageDialog(this, "Usuario registrado con éxito.");
        dispose();

        // 🔽 Reinicia la aplicación para volver a Login fresco
        SwingUtilities.invokeLater(() -> {
            try {
                pe.edu.utp.rudypollo.desktop.RudypolloDesktop.main(new String[]{});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    } catch (Exception ex) {
        ex.printStackTrace();
        lblStatus.setText("Error: " + ex.getMessage());
    }
}
}
