package pe.edu.utp.rudypollo.view;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import pe.edu.utp.rudypollo.dao.impl.ClienteDAOImpl;
import pe.edu.utp.rudypollo.dao.impl.PedidoDAOImpl;
import pe.edu.utp.rudypollo.dao.impl.RepartidorDAOImpl;
import pe.edu.utp.rudypollo.model.Cliente;
import pe.edu.utp.rudypollo.model.Pedido;
import pe.edu.utp.rudypollo.model.Repartidor;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    private PedidoDAOImpl pedidoDAO;
    private RepartidorDAOImpl repartidorDAO;
    private ClienteDAOImpl clienteDAO;

    private JPanel panelGraficos;
    private JLabel lblTotalPedidos, lblPedidosEntregados, lblTotalClientes, lblTopRepartidor;

    private JTextField tfBuscarCliente;
    private JTextField tfBuscarRepartidor;
    private DatePicker dpFechaInicio;
    private DatePicker dpFechaFin;


    private Color azul = new Color(66, 133, 244);
    private Color verde = new Color(52, 168, 83);
    private Color naranja = new Color(251, 188, 5);
    private Color rojo = new Color(234, 67, 53);

    public DashboardPanel() {
        this.pedidoDAO = new PedidoDAOImpl();
        this.repartidorDAO = new RepartidorDAOImpl();
        this.clienteDAO = new ClienteDAOImpl();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("📊 Dashboard - Reportes Generales", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(azul);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);


        JPanel panelMetricas = new JPanel(new GridLayout(1, 4, 15, 15));
        panelMetricas.setBackground(Color.WHITE);
        panelMetricas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalPedidos = crearCard("Total Pedidos", "0", azul);
        lblPedidosEntregados = crearCard("Pedidos Entregados", "0", verde);
        lblTotalClientes = crearCard("Clientes Registrados", "0", naranja);
        lblTopRepartidor = crearCard("Top Repartidor", "N/A", rojo);

        panelMetricas.add(lblTotalPedidos);
        panelMetricas.add(lblPedidosEntregados);
        panelMetricas.add(lblTotalClientes);
        panelMetricas.add(lblTopRepartidor);

        add(panelMetricas, BorderLayout.SOUTH);


        panelGraficos = new JPanel(new GridLayout(2, 2, 15, 15));
        panelGraficos.setBackground(Color.WHITE);
        panelGraficos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(panelGraficos, BorderLayout.CENTER);

        recargarDatos();
    }

    private JLabel crearCard(String titulo, String valor, Color colorFondo) {
        JLabel lbl = new JLabel("<html><center>" + titulo + "<br><b>" + valor + "</b></center></html>");
        lbl.setOpaque(true);
        lbl.setBackground(colorFondo);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        return lbl;
    }

    public void recargarDatos() {
        panelGraficos.removeAll();

        panelGraficos.add(crearGraficoPedidosPorEstado());
        panelGraficos.add(crearGraficoPedidosPorFecha());
        panelGraficos.add(crearPanelTopClientes());
        panelGraficos.add(crearPanelTopRepartidores());

        actualizarMetricas();

        panelGraficos.revalidate();
        panelGraficos.repaint();
    }

    private void actualizarMetricas() {
        try {
            List<Pedido> pedidos = pedidoDAO.findAll();
            List<Repartidor> repartidores = repartidorDAO.findAll();
            int totalClientes = clienteDAO.countClientes();

            lblTotalPedidos.setText("<html><center>Total Pedidos<br><b>" + pedidos.size() + "</b></center></html>");
            long entregados = pedidos.stream().filter(p -> "ENTREGADO".equalsIgnoreCase(p.getEstado())).count();
            lblPedidosEntregados.setText("<html><center>Pedidos Entregados<br><b>" + entregados + "</b></center></html>");
            lblTotalClientes.setText("<html><center>Clientes Registrados<br><b>" + totalClientes + "</b></center></html>");

            Map<Integer, Long> entregasPorRep = pedidos.stream()
                    .filter(p -> p.getAssignedRepartidorId() != null && "ENTREGADO".equalsIgnoreCase(p.getEstado()))
                    .collect(Collectors.groupingBy(Pedido::getAssignedRepartidorId, Collectors.counting()));

            String topRep = "N/A";
            if (!entregasPorRep.isEmpty()) {
                int topId = entregasPorRep.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get().getKey();
                Repartidor r = repartidores.stream().filter(x -> x.getId() == topId).findFirst().orElse(null);
                topRep = (r != null ? r.getNombre() : "N/A");
            }
            lblTopRepartidor.setText("<html><center>Top Repartidor<br><b>" + topRep + "</b></center></html>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ChartPanel crearGraficoPedidosPorEstado() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            List<Pedido> pedidos = pedidoDAO.findAll();
            Map<String, Long> conteo = pedidos.stream()
                    .collect(Collectors.groupingBy(Pedido::getEstado, Collectors.counting()));
            conteo.forEach((estado, cantidad) -> dataset.addValue(cantidad, "Pedidos", estado));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFreeChart chart = ChartFactory.createBarChart("Pedidos por Estado", "Estado", "Cantidad", dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, azul);
        renderer.setDrawBarOutline(false);

        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        return new ChartPanel(chart);
    }

   
    private JPanel crearGraficoPedidosPorFecha() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filtro = new JPanel();
        filtro.setBackground(Color.WHITE);
        filtro.add(new JLabel("Desde:"));
        dpFechaInicio = new DatePicker();
        filtro.add(dpFechaInicio);

        filtro.add(new JLabel("Hasta:"));
        dpFechaFin = new DatePicker();
        filtro.add(dpFechaFin);

        JButton btnFiltrar = new JButton("Filtrar");
        filtro.add(btnFiltrar);

        panel.add(filtro, BorderLayout.NORTH);

        ChartPanel chartPanel = generarGraficoEvolucion(null, null);
        panel.add(chartPanel, BorderLayout.CENTER);

        btnFiltrar.addActionListener(e -> {
            try {
                LocalDate inicio = dpFechaInicio.getDate();
                LocalDate fin = dpFechaFin.getDate();
                chartPanel.setChart(generarGraficoEvolucion(inicio, fin).getChart());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error procesando fechas.");
            }
        });

        return panel;
    }

    private ChartPanel generarGraficoEvolucion(LocalDate inicio, LocalDate fin) {
        TimeSeries series = new TimeSeries("Pedidos");
        try {
            List<Pedido> pedidos = pedidoDAO.findAll();
            Map<LocalDate, Long> conteo = pedidos.stream()
                    .map(p -> p.getCreadoAt().toLocalDateTime().toLocalDate())
                    .filter(d -> (inicio == null || !d.isBefore(inicio)) && (fin == null || !d.isAfter(fin)))
                    .collect(Collectors.groupingBy(d -> d, Collectors.counting()));

            for (Map.Entry<LocalDate, Long> entry : conteo.entrySet()) {
                series.add(new Day(entry.getKey().getDayOfMonth(),
                        entry.getKey().getMonthValue(),
                        entry.getKey().getYear()), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Evolución de Pedidos", "Fecha", "Cantidad", dataset);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, naranja);
        renderer.setSeriesShapesVisible(0, true); 
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }


    private JPanel crearPanelTopClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filtro = new JPanel();
        filtro.setBackground(Color.WHITE);
        filtro.add(new JLabel("Buscar Cliente:"));
        tfBuscarCliente = new JTextField(10);
        filtro.add(tfBuscarCliente);
        JButton btnBuscar = new JButton("Buscar");
        filtro.add(btnBuscar);
        panel.add(filtro, BorderLayout.NORTH);

        ChartPanel chartPanel = generarGraficoTopClientes(null);
        panel.add(chartPanel, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> {
            String nombre = tfBuscarCliente.getText().trim();
            chartPanel.setChart(generarGraficoTopClientes(nombre.isEmpty() ? null : nombre).getChart());
        });

        return panel;
    }

    private ChartPanel generarGraficoTopClientes(String filtro) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            List<Pedido> pedidos = pedidoDAO.findAll();
            Map<Integer, Long> conteo = pedidos.stream()
                    .collect(Collectors.groupingBy(Pedido::getClienteId, Collectors.counting()));

            List<Map.Entry<Integer, Long>> top = conteo.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            for (Map.Entry<Integer, Long> entry : top) {
                Cliente c = clienteDAO.findById(entry.getKey());
                if (c != null) {
                    if (filtro == null || c.getNombre().toLowerCase().contains(filtro.toLowerCase())) {
                        dataset.addValue(entry.getValue(), "Pedidos", c.getNombre());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFreeChart chart = ChartFactory.createBarChart("Top Clientes", "Cliente", "Pedidos", dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, verde);
        renderer.setDrawBarOutline(false);

        return new ChartPanel(chart);
    }


    private JPanel crearPanelTopRepartidores() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filtro = new JPanel();
        filtro.setBackground(Color.WHITE);
        filtro.add(new JLabel("Buscar Repartidor:"));
        tfBuscarRepartidor = new JTextField(10);
        filtro.add(tfBuscarRepartidor);
        JButton btnBuscar = new JButton("Buscar");
        filtro.add(btnBuscar);
        panel.add(filtro, BorderLayout.NORTH);

        ChartPanel chartPanel = generarGraficoTopRepartidores(null);
        panel.add(chartPanel, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> {
            String nombre = tfBuscarRepartidor.getText().trim();
            chartPanel.setChart(generarGraficoTopRepartidores(nombre.isEmpty() ? null : nombre).getChart());
        });

        return panel;
    }

    private ChartPanel generarGraficoTopRepartidores(String filtro) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            List<Pedido> pedidos = pedidoDAO.findAll();
            Map<Integer, Long> conteo = pedidos.stream()
                    .filter(p -> p.getAssignedRepartidorId() != null && "ENTREGADO".equalsIgnoreCase(p.getEstado()))
                    .collect(Collectors.groupingBy(Pedido::getAssignedRepartidorId, Collectors.counting()));

            List<Map.Entry<Integer, Long>> top = conteo.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            for (Map.Entry<Integer, Long> entry : top) {
                Repartidor r = repartidorDAO.findById(entry.getKey());
                if (r != null) {
                    if (filtro == null || r.getNombre().toLowerCase().contains(filtro.toLowerCase())) {
                        dataset.addValue(entry.getValue(), "Pedidos", r.getNombre());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFreeChart chart = ChartFactory.createBarChart("Top Repartidores", "Repartidor", "Pedidos", dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, rojo);
        renderer.setDrawBarOutline(false);

        return new ChartPanel(chart);
    }
}
