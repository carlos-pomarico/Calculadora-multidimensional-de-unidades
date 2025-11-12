package borrador.calculadora.unidades;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class CalculadoraMultidimensional extends JFrame {
    
    // Componentes de la interfaz
    private JComboBox<String> comboMagnitud;
    private JComboBox<String> comboUnidadOrigen;
    private JComboBox<String> comboUnidadDestino;
    private JTextField txtValor;
    private JLabel lblResultado;
    private JTextArea txtDescripcion;
    private JButton btnConvertir;
    private JButton btnLimpiar;
    private JButton btnProcedimiento;
    
    // Gestor de conversiones
    private final ConversionManager conversionManager;
    private final DecimalFormat df;
    
    // Variables para el procedimiento
    private String ultimoProcedimiento = "";
    private boolean hayConversionRealizada = false;
    
    public CalculadoraMultidimensional() {
        conversionManager = new ConversionManager();
        //Formato con coma para decimales y punto para miles
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,##0.##########", symbols);
        
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        setTitle("Calculadora Multidimensional de Unidades");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 530);
        setBackground(new Color(17,48,61));
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Inicializar componentes
        String[] magnitudes = {"Longitud", "Área", "Volumen", "Masa", "Tiempo", 
                               "Velocidad", "Temperatura", "Frecuencia", 
                               "Almacenamiento de Datos", "Radiación"};
        comboMagnitud = new JComboBox<>(magnitudes);
        comboMagnitud.setFont(new Font("Tahoma", Font.BOLD, 12));
        comboMagnitud.setPreferredSize(new Dimension(200, 25));
        
        comboUnidadOrigen = new JComboBox<>();
        comboUnidadOrigen.setFont(new Font("Tahoma", Font.BOLD, 11));
        comboUnidadOrigen.setPreferredSize(new Dimension(180, 25));
        
        comboUnidadDestino = new JComboBox<>();
        comboUnidadDestino.setFont(new Font("Tahoma", Font.BOLD, 11));
        comboUnidadDestino.setPreferredSize(new Dimension(180, 25));
        
        txtValor = new JTextField(10);
        txtValor.setFont(new Font("Tahoma", Font.BOLD, 13));
        txtValor.setPreferredSize(new Dimension(140, 40));
        
        lblResultado = new JLabel("0.0");
        lblResultado.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblResultado.setForeground(new Color(255,255,255));
        
        txtDescripcion = new JTextArea(3, 35);
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(new Font("Tahoma", Font.BOLD, 11));
        txtDescripcion.setBackground(new Color(17,48,61));
        txtDescripcion.setForeground(new Color(255,255,255));
        txtDescripcion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        //Botón de convertir
        btnConvertir = new JButton("Convertir");
        btnConvertir.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnConvertir.setPreferredSize(new Dimension(130, 35));
        btnConvertir.setForeground(new Color(17,48,61));
        btnConvertir.setFocusPainted(false);
        btnConvertir.setOpaque(true);
        btnConvertir.setBorderPainted(true);
        btnConvertir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        //Botón de limpiar
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnLimpiar.setForeground(new Color(17,48,61));
        btnLimpiar.setPreferredSize(new Dimension(130, 35));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        //Botón de procedimiento
        btnProcedimiento = new JButton("Ver Procedimiento");
        btnProcedimiento.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnProcedimiento.setPreferredSize(new Dimension(160, 35));
        btnProcedimiento.setForeground(new Color(17,48,61));
        btnProcedimiento.setFocusPainted(false);
        btnProcedimiento.setOpaque(true);
        btnProcedimiento.setBorderPainted(true);
        btnProcedimiento.setEnabled(false);
        btnProcedimiento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        //Cargar unidades iniciales
        actualizarUnidades();
    }
    
    private void setupLayout() {
        //AbsoluteLayout para el control del formato
        setLayout(null);
        getContentPane().setBackground(new Color(17,48,61));
        
        //Panel del título
        JPanel panelTitulo = new JPanel(null);
        panelTitulo.setBackground(new Color(255,255,255));
        panelTitulo.setBounds(0, 0, 520, 50);
        
        JLabel lblTitulo = new JLabel("Calculadora Multidimensional de Unidades");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(17,48,61));
        lblTitulo.setBounds(40, 10, 440, 30);
        panelTitulo.add(lblTitulo);
        
        //Etiqueta seleccione la magnitud
        JLabel lblMagnitud = new JLabel("Seleccione la magnitud:");
        lblMagnitud.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblMagnitud.setForeground(new Color(255,255,255));
        lblMagnitud.setBounds(30, 65, 160, 25);
        
        //Combo box magnitudes
        comboMagnitud.setBounds(190, 65, 200, 25);
        
        //Panel de la descripción
        JPanel panelDescripcion = new JPanel(new BorderLayout());
        panelDescripcion.setBackground(new Color(17,48,61));
        panelDescripcion.setBounds(30, 100, 460, 95);
        panelDescripcion.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2), 
            "Información de la Magnitud",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 11),
            new Color(255, 255, 255)));
        
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(null);
        panelDescripcion.add(scrollDescripcion, BorderLayout.CENTER);
        
        //Etiquetas de valor e ingreso de datos
        JLabel lblValor = new JLabel("Valor:");
        lblValor.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblValor.setForeground(new Color(255,255,255));
        lblValor.setBounds(40, 215, 50, 25);
        
        txtValor.setBounds(90, 215, 100, 25);
        
        JLabel lblDe = new JLabel("De:");
        lblDe.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblDe.setForeground(new Color(255,255,255));
        lblDe.setBounds(210, 215, 30, 25);
        
        comboUnidadOrigen.setBounds(240, 215, 180, 25);
        
        JLabel lblA = new JLabel("A:");
        lblA.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblA.setForeground(new Color(255,255,255));
        lblA.setBounds(210, 250, 30, 25);
        
        comboUnidadDestino.setBounds(240, 250, 180, 25);
        
        //Panel del resultado
        JPanel panelResultado = new JPanel(null);
        panelResultado.setBackground(new Color(17,48,61));
        panelResultado.setBounds(30, 290, 460, 75);
        panelResultado.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200), 2),
            "Resultado",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 13),
            new Color(255,255,255)));
        
        lblResultado.setBounds(50, 25, 360, 30);
        lblResultado.setHorizontalAlignment(SwingConstants.CENTER);
        panelResultado.add(lblResultado);
        
        //Botones
        btnConvertir.setBounds(30, 385, 130, 35);
        btnLimpiar.setBounds(175, 385, 130, 35);
        btnProcedimiento.setBounds(320, 385, 160, 35);
        
        //Panel de créditos
        JPanel panelCreditos = new JPanel(null);
        panelCreditos.setBackground(new Color(17,48,61));
        panelCreditos.setBounds(0, 470, 520, 30);
        
        JLabel lblCreditos = new JLabel("Universidad Piloto de Colombia - Proyecto de Aula 2025");
        lblCreditos.setFont(new Font("Tahoma", Font.ITALIC, 10));
        lblCreditos.setBounds(80, 8, 400, 15);
        lblCreditos.setForeground(new Color(255,255,255));
        panelCreditos.add(lblCreditos);
        
        //Agregar todos los componentes
        add(panelTitulo);
        add(lblMagnitud);
        add(comboMagnitud);
        add(panelDescripcion);
        add(lblValor);
        add(txtValor);
        add(lblDe);
        add(comboUnidadOrigen);
        add(lblA);
        add(comboUnidadDestino);
        add(panelResultado);
        add(btnConvertir);
        add(btnLimpiar);
        add(btnProcedimiento);
        add(panelCreditos);
    }
    
    private void setupListeners() {
        comboMagnitud.addActionListener(e -> {
            actualizarUnidades();
            actualizarDescripcion();
        });
        
        btnConvertir.addActionListener(e -> realizarConversion());
        
        btnLimpiar.addActionListener(e -> limpiarCampos());
        
        btnProcedimiento.addActionListener(e -> mostrarProcedimiento());
        
        txtValor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarConversion();
                }
            }
        });
        
        // Actualizar descripción inicial
        actualizarDescripcion();
    }
    
    private void actualizarUnidades() {
        String magnitud = (String) comboMagnitud.getSelectedItem();
        String[] unidades = conversionManager.obtenerUnidades(magnitud);
        
        comboUnidadOrigen.removeAllItems();
        comboUnidadDestino.removeAllItems();
        
        for (String unidad : unidades) {
            comboUnidadOrigen.addItem(unidad);
            comboUnidadDestino.addItem(unidad);
        }
        
        if (unidades.length > 1) {
            comboUnidadDestino.setSelectedIndex(1);
        }
    }
    
    private void actualizarDescripcion() {
        String magnitud = (String) comboMagnitud.getSelectedItem();
        txtDescripcion.setText(conversionManager.obtenerDescripcion(magnitud));
    }
    
    private void realizarConversion() {
        try {
            String valorTexto = txtValor.getText().trim().replace(",", ".");
            
            if (valorTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un valor numérico.",
                    "Campo vacío",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double valor = Double.parseDouble(valorTexto);
            String magnitud = (String) comboMagnitud.getSelectedItem();
            String unidadOrigen = (String) comboUnidadOrigen.getSelectedItem();
            String unidadDestino = (String) comboUnidadDestino.getSelectedItem();
            
            // Obtener resultado Y procedimiento
            ResultadoConversion resultadoConv = conversionManager.convertirConProcedimiento(
                magnitud, valor, unidadOrigen, unidadDestino);
            
            lblResultado.setText(df.format(resultadoConv.resultado) + " " + unidadDestino);
            
            // Guardar procedimiento
            ultimoProcedimiento = resultadoConv.procedimiento;
            hayConversionRealizada = true;
            btnProcedimiento.setEnabled(true);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese un valor numérico válido.",
                "Error de formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error en la conversión: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarCampos() {
        txtValor.setText("");
        lblResultado.setText("0.0");
        ultimoProcedimiento = "";
        hayConversionRealizada = false;
        btnProcedimiento.setEnabled(false);
        txtValor.requestFocus();
    }
    
    private void mostrarProcedimiento() {
        if (!hayConversionRealizada || ultimoProcedimiento.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Primero debe realizar una conversión.",
                "Sin procedimiento",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Crear ventana de procedimiento
        JDialog dialogoProcedimiento = new JDialog(this, "Procedimiento de Conversión", true);
        dialogoProcedimiento.setSize(650, 550);
        dialogoProcedimiento.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(Color.WHITE);
        
        // Título
        JLabel lblTitulo = new JLabel("Procedimiento Paso a Paso", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(0, 102, 204));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        // Área de texto para el procedimiento
        JTextArea areaProcedimiento = new JTextArea();
        areaProcedimiento.setText(ultimoProcedimiento);
        areaProcedimiento.setEditable(false);
        areaProcedimiento.setLineWrap(true);
        areaProcedimiento.setWrapStyleWord(true);
        areaProcedimiento.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaProcedimiento.setMargin(new Insets(10, 10, 10, 10));
        areaProcedimiento.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollProcedimiento = new JScrollPane(areaProcedimiento);
        scrollProcedimiento.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Tahoma", Font.PLAIN, 13));
        btnCerrar.setPreferredSize(new Dimension(100, 32));
        btnCerrar.addActionListener(e -> dialogoProcedimiento.dispose());
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.add(btnCerrar);
        
        // Agregar componentes
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollProcedimiento, BorderLayout.CENTER);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);
        
        dialogoProcedimiento.add(panelPrincipal);
        dialogoProcedimiento.setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar excepción
        }
        
        SwingUtilities.invokeLater(() -> {
            CalculadoraMultidimensional calc = new CalculadoraMultidimensional();
            calc.setVisible(true);
        });
    }
}

/**
 * Clase para almacenar el resultado y el procedimiento de una conversión
 */
class ResultadoConversion {
    public double resultado;
    public String procedimiento;
    
    public ResultadoConversion(double resultado, String procedimiento) {
        this.resultado = resultado;
        this.procedimiento = procedimiento;
    }
}

/**
 * Clase que gestiona las conversiones entre diferentes unidades
 */
class ConversionManager {
    
    private final Map<String, Map<String, Double>> factoresConversion;
    private final Map<String, String> descripciones;
    private final DecimalFormat df;
    
    public ConversionManager() {
        factoresConversion = new HashMap<>();
        descripciones = new HashMap<>();
        // Formato con coma para decimales y punto para miles
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,##0.##########", symbols);
        inicializarFactores();
        inicializarDescripciones();
    }
    
    private void inicializarFactores() {
        // LONGITUD (base: metros)
        Map<String, Double> longitud = new LinkedHashMap<>();
        longitud.put("Metros (m)", 1.0);
        longitud.put("Kilómetros (km)", 1000.0);
        longitud.put("Centímetros (cm)", 0.01);
        longitud.put("Milímetros (mm)", 0.001);
        longitud.put("Millas (mi)", 1609.344);
        longitud.put("Yardas (yd)", 0.9144);
        longitud.put("Pies (ft)", 0.3048);
        longitud.put("Pulgadas (in)", 0.0254);
        factoresConversion.put("Longitud", longitud);
        
        // ÁREA (base: metros cuadrados)
        Map<String, Double> area = new LinkedHashMap<>();
        area.put("Metros cuadrados (m²)", 1.0);
        area.put("Kilómetros cuadrados (km²)", 1000000.0);
        area.put("Centímetros cuadrados (cm²)", 0.0001);
        area.put("Hectáreas (ha)", 10000.0);
        area.put("Acres", 4046.86);
        area.put("Pies cuadrados (ft²)", 0.092903);
        area.put("Pulgadas cuadradas (in²)", 0.00064516);
        factoresConversion.put("Área", area);
        
        // VOLUMEN (base: litros)
        Map<String, Double> volumen = new LinkedHashMap<>();
        volumen.put("Litros (L)", 1.0);
        volumen.put("Metros cúbicos (m³)", 1000.0);
        volumen.put("Mililitros (mL)", 0.001);
        volumen.put("Galones (gal)", 3.78541);
        volumen.put("Cuartos (qt)", 0.946353);
        volumen.put("Pintas (pt)", 0.473176);
        volumen.put("Onzas líquidas (fl oz)", 0.0295735);
        volumen.put("Pies cúbicos (ft³)", 28.3168);
        factoresConversion.put("Volumen", volumen);
        
        // MASA (base: kilogramos)
        Map<String, Double> masa = new LinkedHashMap<>();
        masa.put("Kilogramos (kg)", 1.0);
        masa.put("Gramos (g)", 0.001);
        masa.put("Miligramos (mg)", 0.000001);
        masa.put("Toneladas (t)", 1000.0);
        masa.put("Libras (lb)", 0.453592);
        masa.put("Onzas (oz)", 0.0283495);
        factoresConversion.put("Masa", masa);
        
        // TIEMPO (base: segundos)
        Map<String, Double> tiempo = new LinkedHashMap<>();
        tiempo.put("Segundos (s)", 1.0);
        tiempo.put("Minutos (min)", 60.0);
        tiempo.put("Horas (h)", 3600.0);
        tiempo.put("Días (d)", 86400.0);
        tiempo.put("Semanas", 604800.0);
        tiempo.put("Meses (30 días)", 2592000.0);
        tiempo.put("Años (365 días)", 31536000.0);
        tiempo.put("Milisegundos (ms)", 0.001);
        factoresConversion.put("Tiempo", tiempo);
        
        // VELOCIDAD (base: m/s)
        Map<String, Double> velocidad = new LinkedHashMap<>();
        velocidad.put("Metros por segundo (m/s)", 1.0);
        velocidad.put("Kilómetros por hora (km/h)", 0.277778);
        velocidad.put("Millas por hora (mph)", 0.44704);
        velocidad.put("Pies por segundo (ft/s)", 0.3048);
        velocidad.put("Nudos (kn)", 0.514444);
        factoresConversion.put("Velocidad", velocidad);
        
        // TEMPERATURA (se usan conversiones especiales)
        Map<String, Double> temperatura = new LinkedHashMap<>();
        temperatura.put("Celsius (°C)", 1.0);
        temperatura.put("Fahrenheit (°F)", 1.0);
        temperatura.put("Kelvin (K)", 1.0);
        factoresConversion.put("Temperatura", temperatura);
        
        // FRECUENCIA (base: hertz)
        Map<String, Double> frecuencia = new LinkedHashMap<>();
        frecuencia.put("Hertz (Hz)", 1.0);
        frecuencia.put("Kilohertz (kHz)", 1000.0);
        frecuencia.put("Megahertz (MHz)", 1000000.0);
        frecuencia.put("Gigahertz (GHz)", 1000000000.0);
        factoresConversion.put("Frecuencia", frecuencia);
        
        // ALMACENAMIENTO (base: bytes)
        Map<String, Double> almacenamiento = new LinkedHashMap<>();
        almacenamiento.put("Bytes (B)", 1.0);
        almacenamiento.put("Kilobytes (KB)", 1024.0);
        almacenamiento.put("Megabytes (MB)", 1048576.0);
        almacenamiento.put("Gigabytes (GB)", 1073741824.0);
        almacenamiento.put("Terabytes (TB)", 1099511627776.0);
        almacenamiento.put("Bits", 0.125);
        factoresConversion.put("Almacenamiento de Datos", almacenamiento);
        
        // RADIACIÓN (base: gray)
        Map<String, Double> radiacion = new LinkedHashMap<>();
        radiacion.put("Gray (Gy)", 1.0);
        radiacion.put("Rad", 0.01);
        radiacion.put("Sievert (Sv)", 1.0);
        radiacion.put("Rem", 0.01);
        factoresConversion.put("Radiación", radiacion);
    }
    
    private void inicializarDescripciones() {
        descripciones.put("Longitud",
            "La longitud es una magnitud física fundamental que expresa la distancia entre dos puntos. " +
            "Es una de las siete magnitudes fundamentales del Sistema Internacional. " +
            "Se utiliza en ingeniería, construcción, cartografía y numerosas aplicaciones técnicas.");
        
        descripciones.put("Área",
            "El área es una magnitud derivada que expresa la extensión de una superficie bidimensional. " +
            "Se calcula como el producto de dos longitudes. Es fundamental en arquitectura, " +
            "agronomía, urbanismo y cálculos de materiales en construcción.");
        
        descripciones.put("Volumen",
            "El volumen cuantifica el espacio tridimensional que ocupa un cuerpo. " +
            "Es el producto de tres longitudes. Se aplica en química, física de fluidos, " +
            "ingeniería de recipientes y cálculos de capacidad en diversas industrias.");
        
        descripciones.put("Masa",
            "La masa es una magnitud física fundamental que expresa la cantidad de materia de un cuerpo. " +
            "No debe confundirse con el peso, que depende de la gravedad. " +
            "Es esencial en mecánica, química, ingeniería de materiales y física.");
        
        descripciones.put("Tiempo",
            "El tiempo es una magnitud fundamental que permite ordenar la secuencia de los sucesos. " +
            "El segundo se define mediante la frecuencia de radiación del átomo de cesio-133. " +
            "Es crítico en física, ingeniería de procesos y sistemas de control.");
        
        descripciones.put("Velocidad",
            "La velocidad es una magnitud vectorial derivada que relaciona el cambio de posición " +
            "con el tiempo transcurrido. Es fundamental en mecánica, cinemática, " +
            "ingeniería de transporte y dinámica de fluidos.");
        
        descripciones.put("Temperatura",
            "La temperatura es una magnitud escalar relacionada con la energía cinética promedio " +
            "de las partículas. Existen tres escalas principales: Celsius, Fahrenheit y Kelvin. " +
            "Es fundamental en termodinámica, meteorología e ingeniería térmica.");
        
        descripciones.put("Frecuencia",
            "La frecuencia mide el número de repeticiones de un evento periódico por unidad de tiempo. " +
            "Se expresa en hertz (Hz). Es esencial en telecomunicaciones, electrónica, " +
            "acústica y procesamiento de señales.");
        
        descripciones.put("Almacenamiento de Datos",
            "Representa la cantidad de información digital que puede almacenarse en un dispositivo. " +
            "Se mide en bytes y sus múltiplos binarios. Es fundamental en informática, " +
            "ingeniería de sistemas y tecnologías de la información.");
        
        descripciones.put("Radiación",
            "Cuantifica la exposición a radiación ionizante y la dosis absorbida por la materia. " +
            "Gray (Gy) mide energía absorbida y Sievert (Sv) mide efectos biológicos. " +
            "Es crítico en física médica, protección radiológica y seguridad nuclear.");
    }
    
    public String[] obtenerUnidades(String magnitud) {
        Map<String, Double> unidades = factoresConversion.get(magnitud);
        if (unidades == null) return new String[0];
        return unidades.keySet().toArray(new String[0]);
    }
    
    public String obtenerDescripcion(String magnitud) {
        return descripciones.getOrDefault(magnitud, "Descripción no disponible.");
    }
    
    /**
     * Convierte con procedimiento detallado
     */
    public ResultadoConversion convertirConProcedimiento(String magnitud, double valor, 
                                                          String unidadOrigen, String unidadDestino) {
        StringBuilder procedimiento = new StringBuilder();
        double resultado;
        
        procedimiento.append("╔══════════════════════════════════════════════════════════╗\n");
        procedimiento.append("║          PROCEDIMIENTO DE CONVERSIÓN DETALLADO           ║\n");
        procedimiento.append("╚══════════════════════════════════════════════════════════╝\n\n");
        
        procedimiento.append("MAGNITUD: ").append(magnitud).append("\n");
        procedimiento.append("CONVERSIÓN: ").append(unidadOrigen).append(" → ").append(unidadDestino).append("\n");
        procedimiento.append("VALOR INICIAL: ").append(df.format(valor)).append(" ").append(unidadOrigen).append("\n\n");
        procedimiento.append("──────────────────────────────────────────────────────────\n\n");
        
        if (magnitud.equals("Temperatura")) {
            resultado = convertirTemperaturaConProcedimiento(valor, unidadOrigen, unidadDestino, procedimiento);
        } else {
            resultado = convertirGeneralConProcedimiento(magnitud, valor, unidadOrigen, unidadDestino, procedimiento);
        }
        
        procedimiento.append("\n──────────────────────────────────────────────────────────\n\n");
        procedimiento.append("RESULTADO FINAL:\n");
        procedimiento.append("   ").append(df.format(valor)).append(" ").append(unidadOrigen);
        procedimiento.append(" = ").append(df.format(resultado)).append(" ").append(unidadDestino).append("\n\n");
        procedimiento.append("══════════════════════════════════════════════════════════\n");
        
        return new ResultadoConversion(resultado, procedimiento.toString());
    }
    
    private double convertirGeneralConProcedimiento(String magnitud, double valor, 
                                                     String unidadOrigen, String unidadDestino,
                                                     StringBuilder procedimiento) {
        Map<String, Double> factores = factoresConversion.get(magnitud);
        
        Double factorOrigen = factores.get(unidadOrigen);
        Double factorDestino = factores.get(unidadDestino);
        
        procedimiento.append("PASO 1: Identificar los factores de conversión\n");
        procedimiento.append("   • Factor de ").append(unidadOrigen).append(": ").append(df.format(factorOrigen)).append("\n");
        procedimiento.append("   • Factor de ").append(unidadDestino).append(": ").append(df.format(factorDestino)).append("\n\n");
        
        procedimiento.append("PASO 2: Convertir a la unidad base\n");
        double valorBase = valor * factorOrigen;
        procedimiento.append("   Fórmula: Valor × Factor de origen\n");
        procedimiento.append("   Cálculo: ").append(df.format(valor)).append(" × ").append(df.format(factorOrigen));
        procedimiento.append(" = ").append(df.format(valorBase)).append("\n\n");
        
        procedimiento.append("PASO 3: Convertir de la unidad base a la unidad destino\n");
        double resultado = valorBase / factorDestino;
        procedimiento.append("   Fórmula: Valor base ÷ Factor de destino\n");
        procedimiento.append("   Cálculo: ").append(df.format(valorBase)).append(" ÷ ").append(df.format(factorDestino));
        procedimiento.append(" = ").append(df.format(resultado)).append("\n");
        
        return resultado;
    }
    
    private double convertirTemperaturaConProcedimiento(double valor, String origen, String destino,
                                                        StringBuilder procedimiento) {
        procedimiento.append("NOTA: La temperatura usa fórmulas específicas, no factores multiplicativos.\n\n");
        
        // Convertir a Celsius primero
        double celsius;
        procedimiento.append("PASO 1: Convertir a Celsius (unidad intermedia)\n");
        
        if (origen.contains("Celsius") || origen.contains("°C")) {
            celsius = valor;
            procedimiento.append("   Ya está en Celsius: ").append(df.format(celsius)).append(" °C\n\n");
        } else if (origen.contains("Fahrenheit") || origen.contains("°F")) {
            celsius = (valor - 32) * 5.0 / 9.0;
            procedimiento.append("   Fórmula: °C = (°F - 32) × 5/9\n");
            procedimiento.append("   Cálculo: (").append(df.format(valor)).append(" - 32) × 5/9\n");
            procedimiento.append("   Cálculo: ").append(df.format(valor - 32)).append(" × 0.5556\n");
            procedimiento.append("   Resultado: ").append(df.format(celsius)).append(" °C\n\n");
        } else if (origen.contains("Kelvin") || origen.contains("K")) {
            celsius = valor - 273.15;
            procedimiento.append("   Fórmula: °C = K - 273.15\n");
            procedimiento.append("   Cálculo: ").append(df.format(valor)).append(" - 273.15\n");
            procedimiento.append("   Resultado: ").append(df.format(celsius)).append(" °C\n\n");
        } else {
            throw new IllegalArgumentException("Unidad de temperatura no reconocida: " + origen);
        }
        
        // Convertir de Celsius a destino
        double resultado;
        procedimiento.append("PASO 2: Convertir de Celsius a ").append(destino).append("\n");
        
        if (destino.contains("Celsius") || destino.contains("°C")) {
            resultado = celsius;
            procedimiento.append("   Ya está en la unidad destino: ").append(df.format(resultado)).append(" °C\n");
        } else if (destino.contains("Fahrenheit") || destino.contains("°F")) {
            resultado = celsius * 9.0 / 5.0 + 32;
            procedimiento.append("   Fórmula: °F = (°C × 9/5) + 32\n");
            procedimiento.append("   Cálculo: (").append(df.format(celsius)).append(" × 9/5) + 32\n");
            procedimiento.append("   Cálculo: ").append(df.format(celsius * 9.0 / 5.0)).append(" + 32\n");
            procedimiento.append("   Resultado: ").append(df.format(resultado)).append(" °F\n");
        } else if (destino.contains("Kelvin") || destino.contains("K")) {
            resultado = celsius + 273.15;
            procedimiento.append("   Fórmula: K = °C + 273.15\n");
            procedimiento.append("   Cálculo: ").append(df.format(celsius)).append(" + 273.15\n");
            procedimiento.append("   Resultado: ").append(df.format(resultado)).append(" K\n");
        } else {
            throw new IllegalArgumentException("Unidad de temperatura no reconocida: " + destino);
        }
        
        return resultado;
    }
}