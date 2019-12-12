package vista;

import daos.RAMContactoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import modelo.Contactos;

public class Principal extends javax.swing.JFrame {
    
    ArrayList<Character> letrasNif = new ArrayList<>();
    ArrayList<Contactos> contacto = new ArrayList<>();
    RAMContactoDAO ram = new RAMContactoDAO();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Estado e;
    Estado origenData;
    int avance;
    int remove;
    int count;
    boolean respuesta;
    private static final String PREDETERMINADO = "default.png";
    Contactos c;
    ImageIcon iI = new ImageIcon();
    
    public Principal() throws ParseException {
        initComponents();
        añadeLetrasArrayList();
        this.setLocationRelativeTo(null);
        sdf.setLenient(false);
        count = 0;
        seleccionarOrigenDatos();
        
        if (contacto.isEmpty()) {
            e = Estado.ANHADIENDO;
        } else {
            e = Estado.NAVEGANDO;
            avance = 0;
        }
        seleccionEstado(e);
        
    }
    
    private void borraDeLista() {
        remove++;
        if (contacto.get(avance).getNif().equals(txtNif.getText())) {
            contacto.remove(avance);
            avance = 0;
            count = 0;
        }
        
        if (contacto.isEmpty()) {
            e = Estado.ANHADIENDO;
            anhadir();
        } else {
            navegar();
        }
        
    }
    
    private void editarContacto() throws ParseException, NumberFormatException {
        c = contacto.get(avance);
        c.setNombre(txtNombre.getText());
        c.setApellido1(txtApellido1.getText());
        c.setApellido2(txtApellido2.getText());
        c.setTelefono(Integer.parseInt(txtTelefono.getText()));
        c.setNacimiento(sdf.parse(txtNacimiento.getText()));
        c.setTipo(cmboTipo.getSelectedItem().toString());
        c.setPerfil((ImageIcon) lblImgPerfil.getIcon());
        contacto.set(avance, c);
        e = Estado.NAVEGANDO;
        seleccionEstado(e);
    }
    
    private void seleccionarOrigenDatos() {
        String[] s = {"RAM", "Fichero", "Salir"};
        int respuestas = JOptionPane.showOptionDialog(null, "¿Qué Origen de Datos desea?", "Aviso", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, s, s[0]);
        
        switch (respuestas) {
            case -1: //Opcion Salir
                System.exit(0);
            case 0://Opcion Ram
                //Contactos de prueba
                ImageIcon icon = new ImageIcon(PREDETERMINADO);
                Contactos c1 = new Contactos("08647651L", "Pepito", "Pepon", "Papo", 981137131, new Date(91, 11, 2), "Amigo", icon);
                Contactos c2 = new Contactos("85364721Z", "Rodrigo", "Castro", "Fernandez", 562818654, new Date(99, 5, 20), "Enemigo", icon);
                Contactos c3 = new Contactos("32074238W", "Sonia", "Calvo", "Pelon", 268172697, new Date(89, 0, 15), "Trabajo", icon);
                Contactos c4 = new Contactos("78551354Y", "Marta", "Muerta", "Morto", 945678123, new Date(98, 6, 7), "Familiar", icon);
                
                if ((ram.addContacto(c1) == false
                        || ram.addContacto(c2) == false
                        || ram.addContacto(c3) == false
                        || ram.addContacto(c4) == false)) {
                    contacto.clear();
                    JOptionPane.showMessageDialog(null, "Fallo al cargar datos o no hay datos en RAM para cargar, pasando a modo añadir...", "Error", JOptionPane.ERROR_MESSAGE);
                    e = Estado.ANHADIENDO;
                    seleccionEstado(e);
                } else {
                    origenData = Estado.RAM;
                    e = Estado.NAVEGANDO;
                    seleccionEstado(e);
                    break;
                }
            case 1: //Opcion Fichero
                try {
                    respuesta = cargarDatos();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            default:
                System.exit(0);
        }
    }
    
    enum Estado {
        NAVEGANDO,
        ANHADIENDO,
        EDITANDO,
        BORRANDO,
        RAM,
        FICHERO
    }
    
    private void seleccionEstado(Estado e) {
        switch (e) {
            case ANHADIENDO:
                anhadir();
                break;
            case BORRANDO:
                borrar();
                break;
            case EDITANDO:
                editar();
                break;
            case NAVEGANDO:
                navegar();
                break;
            default:
                System.out.println("No hay seleccionado ningun estado\n");
        }
    }
    
    private void borrar() {
        btnAnterior.setEnabled(false);
        btnAceptar.setEnabled(true);
        btnCancelar.setEnabled(true);
        btnBorrar.setEnabled(false);
        btnEditar.setEnabled(false);
        btnPrimero.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnUltimo.setEnabled(false);
        btnAnadir.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnCargar.setEnabled(false);
        
        txtNif.setEditable(false);
        txtNombre.setEditable(false);
        txtApellido1.setEditable(false);
        txtApellido2.setEditable(false);
        txtTelefono.setEditable(false);
        txtNacimiento.setEditable(false);
        cmboTipo.setEditable(false);
        remove = 0;
        
    }
    
    private void anhadir() {
        
        if (contacto.isEmpty()) {
            String[] s = {"Añadir", "Cargar", "Salir"};
            int respuestas = JOptionPane.showOptionDialog(null, "Lista vacia,¿que desea hacer?", "Aviso", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, s, s[0]);
            
            switch (respuestas) {
                case -1:
                    System.exit(0);
                case 0:
                    break;
                case 1:
                    try {
                        respuesta = cargarDatos();
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                default:
                    System.exit(0);
            }
            cmboTipo.setSelectedIndex(-1);
            
            btnAnterior.setEnabled(false);
            btnAceptar.setEnabled(true);
            btnCancelar.setEnabled(false);
            btnBorrar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnPrimero.setEnabled(false);
            btnSiguiente.setEnabled(false);
            btnUltimo.setEnabled(false);
            btnAnadir.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnCargar.setEnabled(true);
            
            txtNif.setText("");
            txtNombre.setText("");
            txtApellido1.setText("");
            txtApellido2.setText("");
            txtTelefono.setText("");
            txtNacimiento.setText("");
            lblImgPerfil.setIcon(null);
            lblImgPerfil.setText("<html>Click para <br/>seleccionar imagen<br/>(190x254)<html>");
            //<html>Hello World!<br/>blahblahblah</html>

            txtNif.setEditable(true);
            txtNombre.setEditable(true);
            txtApellido1.setEditable(true);
            txtApellido2.setEditable(true);
            txtTelefono.setEditable(true);
            txtNacimiento.setEditable(true);
            
        } else {
            if (respuesta) {
                cmboTipo.setSelectedIndex(-1);
                
                btnAnterior.setEnabled(false);
                btnAceptar.setEnabled(true);
                btnCancelar.setEnabled(true);
                btnBorrar.setEnabled(false);
                btnEditar.setEnabled(false);
                btnPrimero.setEnabled(false);
                btnSiguiente.setEnabled(false);
                btnUltimo.setEnabled(false);
                btnAnadir.setEnabled(false);
                cmboTipo.setEnabled(true);
                btnGuardar.setEnabled(false);
                btnCargar.setEnabled(false);
                
                txtNif.setText("");
                txtNombre.setText("");
                txtApellido1.setText("");
                txtApellido2.setText("");
                txtTelefono.setText("");
                txtNacimiento.setText("");
                lblImgPerfil.setIcon(null);
                lblImgPerfil.setText("<html>Click para <br/>seleccionar imagen<br/>(190x254)<html>");
                
                txtNif.setEditable(true);
                txtNombre.setEditable(true);
                txtApellido1.setEditable(true);
                txtApellido2.setEditable(true);
                txtTelefono.setEditable(true);
                txtNacimiento.setEditable(true);
            } else {
                
                cmboTipo.setSelectedIndex(-1);
                
                btnAnterior.setEnabled(false);
                btnAceptar.setEnabled(true);
                btnCancelar.setEnabled(true);
                btnBorrar.setEnabled(false);
                btnEditar.setEnabled(false);
                btnPrimero.setEnabled(false);
                btnSiguiente.setEnabled(false);
                btnUltimo.setEnabled(false);
                btnAnadir.setEnabled(false);
                cmboTipo.setEnabled(true);
                btnGuardar.setEnabled(false);
                btnCargar.setEnabled(false);
                
                txtNif.setText("");
                txtNombre.setText("");
                txtApellido1.setText("");
                txtApellido2.setText("");
                txtTelefono.setText("");
                txtNacimiento.setText("");
                lblImgPerfil.setIcon(null);
                lblImgPerfil.setText("<html>Click para <br/>seleccionar imagen<br/>190x254)<html>");
                
                txtNif.setEditable(true);
                txtNombre.setEditable(true);
                txtApellido1.setEditable(true);
                txtApellido2.setEditable(true);
                txtTelefono.setEditable(true);
                txtNacimiento.setEditable(true);
            }
        }
    }
    
    private void navegar() {
        if (contacto.isEmpty()) {
            
            txtNif.setText("");
            txtNombre.setText("");
            txtApellido1.setText("");
            txtApellido2.setText("");
            txtTelefono.setText("");
            txtNacimiento.setText("");
            
            e = Estado.ANHADIENDO;
            seleccionEstado(e);
        }
        btnAnterior.setEnabled(true);
        btnAceptar.setEnabled(false);
        btnCancelar.setEnabled(false);
        btnBorrar.setEnabled(true);
        btnEditar.setEnabled(true);
        btnPrimero.setEnabled(true);
        btnSiguiente.setEnabled(true);
        btnUltimo.setEnabled(true);
        btnAnadir.setEnabled(true);
        btnGuardar.setEnabled(true);
        btnCargar.setEnabled(true);
        
        txtNif.setEnabled(true);
        txtNif.setEditable(false);
        txtNombre.setEditable(false);
        txtApellido1.setEditable(false);
        txtApellido2.setEditable(false);
        txtTelefono.setEditable(false);
        txtNacimiento.setEditable(false);
        lblImgPerfil.setText("");
        
        cmboTipo.setEnabled(false);
        if (count == 0) {
            Contactos contact = contacto.get(0);
            txtNif.setText(contact.getNif().toUpperCase());
            txtNombre.setText(contact.getNombre());
            txtApellido1.setText(contact.getApellido1());
            txtApellido2.setText(contact.getApellido2());
            txtTelefono.setText(String.valueOf(contact.getTelefono()));
            txtNacimiento.setText(sdf.format(contact.getNacimiento()));
            cmboTipo.setSelectedItem(contact.getTipo());
            lblImgPerfil.setIcon(contact.getPerfil());
        }
        count++;
        if (avance == 0) {
            btnAnterior.setEnabled(false);
            btnPrimero.setEnabled(false);
        } else if (avance == contacto.size() - 1) {
            btnSiguiente.setEnabled(false);
            btnUltimo.setEnabled(false);
        }
        if (contacto.size() == 1) {
            btnAnterior.setEnabled(false);
            btnPrimero.setEnabled(false);
            btnSiguiente.setEnabled(false);
            btnUltimo.setEnabled(false);
        }
        
    }
    
    private void rellenar(int valor) {
        Contactos contact = contacto.get(valor);
        txtNif.setText(contact.getNif().toUpperCase());
        txtNombre.setText(contact.getNombre());
        txtApellido1.setText(contact.getApellido1());
        txtApellido2.setText(contact.getApellido2());
        txtTelefono.setText(String.valueOf(contact.getTelefono()));
        txtNacimiento.setText(sdf.format(contact.getNacimiento()));
        cmboTipo.setSelectedItem(contact.getTipo());
        lblImgPerfil.setIcon(contact.getPerfil());
        
    }
    
    private void editar() {
        btnAnterior.setEnabled(false);
        btnAceptar.setEnabled(true);
        btnCancelar.setEnabled(true);
        btnBorrar.setEnabled(false);
        btnEditar.setEnabled(false);
        btnPrimero.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnUltimo.setEnabled(false);
        btnAnadir.setEnabled(false);
        cmboTipo.setEnabled(true);
        
        txtNif.setEnabled(false);
        txtNif.setEditable(false);
        txtNombre.setEditable(true);
        txtApellido1.setEditable(true);
        txtApellido2.setEditable(true);
        txtTelefono.setEditable(true);
        txtNacimiento.setEditable(true);
    }
    
    private void anhadeLista() throws ParseException, NumberFormatException {
        if (txtNif.getText().isEmpty() || txtNombre.getText().isEmpty()
                || txtApellido1.getText().isEmpty() || txtApellido2.getText().isEmpty()
                || txtTelefono.getText().isEmpty() || txtNacimiento.getText().isEmpty()
                || cmboTipo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Alguno de los campos está vacio", "Aviso", JOptionPane.ERROR_MESSAGE);
            
        } else {
            if (txtTelefono.getText().length() == 9) {
                if (lblImgPerfil.getIcon() == null) {
                    iI = new ImageIcon(PREDETERMINADO);
                    lblImgPerfil.setIcon(iI);
                }
                contacto.add(new Contactos(txtNif.getText(), txtNombre.getText(),
                        txtApellido1.getText(), txtApellido2.getText(), Integer.parseInt(txtTelefono.getText()),
                        sdf.parse(txtNacimiento.getText()), cmboTipo.getSelectedItem().toString(), iI));
                e = Estado.NAVEGANDO;
                avance = 0;
                count = 0;
                seleccionEstado(e);
                
            } else {
                JOptionPane.showMessageDialog(null, "Telefono erroneo", "Aviso", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void guardarDatos() throws FileNotFoundException, IOException {
        File file;
        fcGuardarCargar.showSaveDialog(null);
        file = fcGuardarCargar.getSelectedFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, false))) {
            oos.writeObject(contacto);
        } catch (Exception ex) {
            
        }
    }
    
    private boolean cargarDatos() throws IOException, ClassNotFoundException {
        count = 0;
        lblImgPerfil.setText("");
        File file;
        int respuestas = fcGuardarCargar.showOpenDialog(null);
        file = fcGuardarCargar.getSelectedFile();
        if (JFileChooser.APPROVE_OPTION == respuestas) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {;
                
                contacto = (ArrayList<Contactos>) ois.readObject();
            } catch (FileNotFoundException | ClassNotFoundException | StreamCorruptedException ex) {
                JOptionPane.showMessageDialog(null, "Error al cargar fichero", "Error", JOptionPane.ERROR_MESSAGE);
            }
            e = Estado.NAVEGANDO;
            seleccionEstado(e);
            return true;
        } else {
            e = Estado.ANHADIENDO;
            seleccionEstado(e);
            return false;
        }
    }
    
    private void añadeLetrasArrayList() {
        
        char letrasNifs[] = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
        for (int i = 0; i < letrasNifs.length; i++) {
            letrasNif.add(letrasNifs[i]);
        }
    }
    
    private boolean compruebaNif(String nif) throws NumberFormatException {
        if (nif.length() != 9 || Character.isDigit(nif.charAt(8)) || !letrasNif.contains(nif.charAt(8))) {
            return true;
        } else if (letrasNif.contains(nif.charAt(8))) {
            int numNif = Integer.parseInt(nif.substring(0, 8));
            int modNif = 0;
            
            modNif = numNif % 23;
            
            if (!letrasNif.get(modNif).equals(nif.charAt(8))) {
                return true;
            }
            
        }
        return false;
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcGuardarCargar = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        lblNif = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblApellido1 = new javax.swing.JLabel();
        lblApellido2 = new javax.swing.JLabel();
        lblTelefono = new javax.swing.JLabel();
        lblNacimiento = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        txtNif = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtApellido1 = new javax.swing.JTextField();
        txtApellido2 = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtNacimiento = new javax.swing.JTextField();
        cmboTipo = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        btnAnadir = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnPrimero = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        btnUltimo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnCargar = new javax.swing.JButton();
        lblImgPerfil = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        lblNif.setText("NIF");

        lblNombre.setText("Nombre");

        lblApellido1.setText("Apellido 1");

        lblApellido2.setText("Apellido 2");

        lblTelefono.setText("Telefono");

        lblNacimiento.setText("Nacimiento");

        lblTipo.setText("Tipo");

        txtNombre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNombreFocusLost(evt);
            }
        });

        txtApellido1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtApellido1FocusLost(evt);
            }
        });

        txtApellido2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtApellido2FocusLost(evt);
            }
        });

        cmboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enemigo", "Amigo", "Trabajo", "Familiar" }));

        jButton1.setText("A.C.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNif)
                    .addComponent(lblNombre)
                    .addComponent(lblApellido1)
                    .addComponent(lblApellido2)
                    .addComponent(lblTelefono)
                    .addComponent(lblNacimiento)
                    .addComponent(lblTipo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNif, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtApellido1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtApellido2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNacimiento, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTelefono)
                    .addComponent(cmboTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNif)
                            .addComponent(txtNif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNombre)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblApellido1)
                            .addComponent(txtApellido1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblApellido2)
                            .addComponent(txtApellido2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addComponent(lblTelefono))
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNacimiento)
                    .addComponent(txtNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTipo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmboTipo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        btnAnadir.setText("Añadir");
        btnAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirActionPerformed(evt);
            }
        });

        btnBorrar.setText("Borrar");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnPrimero.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnPrimero.setText("|<--");
        btnPrimero.setMaximumSize(new java.awt.Dimension(51, 24));
        btnPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrimeroActionPerformed(evt);
            }
        });

        btnAnterior.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnAnterior.setText("<--");
        btnAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnteriorActionPerformed(evt);
            }
        });

        btnSiguiente.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnSiguiente.setText("-->");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        btnUltimo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnUltimo.setText("-->|");
        btnUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUltimoActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCargar.setText("Cargar");
        btnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarActionPerformed(evt);
            }
        });

        lblImgPerfil.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblImgPerfil.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImgPerfil.setText("Click para seleccionar  imagen (190x254) ");
        lblImgPerfil.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImgPerfil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImgPerfilMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCargar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAnadir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBorrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAceptar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(98, 98, 98))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(lblImgPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(35, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAceptar, btnAnadir, btnBorrar, btnCancelar, btnEditar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblImgPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAnadir)
                        .addGap(18, 18, 18)
                        .addComponent(btnBorrar)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditar)
                        .addGap(15, 15, 15))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSiguiente)
                    .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCargar)
                            .addComponent(btnGuardar))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancelar)
                        .addGap(41, 41, 41))))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAceptar, btnAnadir, btnBorrar, btnCancelar, btnEditar});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAnterior, btnPrimero});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        switch (e) {
            case ANHADIENDO: {
                try {
                    respuesta = compruebaNif(txtNif.getText().toUpperCase());
                } catch (NumberFormatException ex) {
                    respuesta = true;
                    JOptionPane.showMessageDialog(null, "NIF con letras donde debería de haber numeros", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                if (respuesta) {
                    JOptionPane.showMessageDialog(null, "NIF incorrecto (Tienen que ser 8 caracteres más una letra valida)", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        anhadeLista();
                        
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(null, "Formato de la fecha incorrecto (dd/MM/aaaa)", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Error en el apartado 'Telefono'", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            break;
            case BORRANDO:
                borraDeLista();
                break;
            case EDITANDO: {
                try {
                    editarContacto();
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Formato de la fecha incorrecto (dd/MM/aaaa)", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Error en el apartado 'Telefono'", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            }
            break;
            default:
                System.out.println("Algo raro ha pasado\n");
        }

    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        if (avance == contacto.size() - 1) {
            rellenar(avance);
        } else if (avance < contacto.size()) {
            avance++;
            rellenar(avance);
        }
        navegar();
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnAnadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirActionPerformed
        e = Estado.ANHADIENDO;
        seleccionEstado(e);
    }//GEN-LAST:event_btnAnadirActionPerformed

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        
        if (avance == 0) {
            rellenar(avance);
        } else if (avance > 0) {
            avance--;
            rellenar(avance);
        }
        
        navegar();

    }//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUltimoActionPerformed
        Contactos cont = contacto.get(contacto.size() - 1);
        txtNif.setText(cont.getNif().toUpperCase());
        txtNombre.setText(cont.getNombre());
        txtApellido1.setText(cont.getApellido1());
        txtApellido2.setText(cont.getApellido2());
        txtTelefono.setText(String.valueOf(cont.getTelefono()));
        txtNacimiento.setText(sdf.format(cont.getNacimiento()));
        cmboTipo.setSelectedItem(cont.getTipo());
        lblImgPerfil.setIcon(cont.getPerfil());
        
        avance = contacto.size() - 1;
        navegar();
    }//GEN-LAST:event_btnUltimoActionPerformed

    private void btnPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimeroActionPerformed
        Contactos cont = contacto.get(0);
        txtNif.setText(cont.getNif().toUpperCase());
        txtNombre.setText(cont.getNombre());
        txtApellido1.setText(cont.getApellido1());
        txtApellido2.setText(cont.getApellido2());
        txtTelefono.setText(String.valueOf(cont.getTelefono()));
        txtNacimiento.setText(sdf.format(cont.getNacimiento()));
        cmboTipo.setSelectedItem(cont.getTipo());
        lblImgPerfil.setIcon(cont.getPerfil());
        btnAnterior.setEnabled(false);
        btnPrimero.setEnabled(false);
        avance = 0;
        navegar();
        

    }//GEN-LAST:event_btnPrimeroActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        e = Estado.BORRANDO;
        seleccionEstado(e);
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        count = 0;
        avance = 0;
        e = Estado.NAVEGANDO;
        seleccionEstado(e);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        e = Estado.EDITANDO;
        seleccionEstado(e);
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try {
            guardarDatos();
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
        if (!contacto.isEmpty()) {
            String[] s = {"Guardar", "Cargar Igualmente"};
            
            int respuesta = JOptionPane.showOptionDialog(null, "Si cargas sin guardar perderas los contactos actuales,¿Seguro que deseas cargar?", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, s, s[0]);
            
            switch (respuesta) {
                case JOptionPane.CLOSED_OPTION: {
                    e = Estado.NAVEGANDO;
                    seleccionEstado(e);
                }
                break;
                case 0: {
                    try {
                        guardarDatos();
                    } catch (IOException ex) {
                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
                case 1: {
                    try {
                        cargarDatos();
                    } catch (IOException ex) {
                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "Error al cargar fichero", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            }
        } else {
            
            try {
                try {
                    cargarDatos();
                } catch (FileNotFoundException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Error al cargar fichero", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnCargarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        String[] s = {"Guardar", "Salir Sin Guardar"};
        
        int respuesta = JOptionPane.showOptionDialog(null, "Vas a salir sin guardar, ¿Estás seguro?", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, s, s[0]);
        
        switch (respuesta) {
            case JOptionPane.CLOSED_OPTION: {
                e = Estado.NAVEGANDO;
                seleccionEstado(e);
            }
            break;
            case 0: {
                try {
                    guardarDatos();
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case 1:
                break;
        }
    }//GEN-LAST:event_formWindowClosing

    private void txtNombreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNombreFocusLost
        
        if (comprobarTxts(txtNombre.getText())) {
            JOptionPane.showMessageDialog(null, "'Nombre' no tiene que incluir digitos", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.setText("");
        } else {
            
        }
    }//GEN-LAST:event_txtNombreFocusLost

    private void txtApellido1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtApellido1FocusLost
        if (comprobarTxts(txtApellido1.getText())) {
            JOptionPane.showMessageDialog(null, "'Apellido 1' no tiene que incluir digitos", "Error", JOptionPane.ERROR_MESSAGE);
            txtApellido1.setText("");
        } else {
            
        }
    }//GEN-LAST:event_txtApellido1FocusLost

    private void txtApellido2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtApellido2FocusLost
        if (comprobarTxts(txtApellido2.getText())) {
            JOptionPane.showMessageDialog(null, "'Apellido 2' no tiene que incluir digitos", "Error", JOptionPane.ERROR_MESSAGE);
            txtApellido2.setText("");
        } else {
            
        }
    }//GEN-LAST:event_txtApellido2FocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String nif = txtNif.getText();
        if (nif.length() == 9) {
            
        } else if (nif.length() == 8) {
            nif = nif.substring(0, 8);
            int calc = Integer.parseInt(nif) % 23;
            char letra = letrasNif.get(calc);
            txtNif.setText(txtNif.getText() + "" + letra);
        } else {
            
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void lblImgPerfilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImgPerfilMouseClicked
        
        imgPerfil();
    }//GEN-LAST:event_lblImgPerfilMouseClicked
    
    private void imgPerfil() {
        if (e == Estado.ANHADIENDO || e == Estado.EDITANDO) {
            File file;
            String path = null;
            int respuesta = fcGuardarCargar.showOpenDialog(null);
            file = fcGuardarCargar.getSelectedFile();
            try {
                path = file.getAbsolutePath();
            } catch (Exception ex) {
                System.out.println("holi");
            }
            if (JFileChooser.APPROVE_OPTION == respuesta) {
                iI = new ImageIcon(path);
                int heightIcon = iI.getIconHeight();
                int widthIcon = iI.getIconWidth();
                int heightLbl = lblImgPerfil.getHeight();
                int widthLbl = lblImgPerfil.getWidth();
                if (heightIcon != heightLbl || widthIcon != widthLbl) {
                    JOptionPane.showMessageDialog(null, "Dimensiones de imagen incorrectas, tamaño de imagen 190x254", "Error", JOptionPane.ERROR_MESSAGE);
                    imgPerfil();
                } else {
                    lblImgPerfil.setIcon(iI);
                }
                
            } else {
                
            }
        }
    }
    
    private boolean comprobarTxts(String txt) {
        
        for (int i = 0; i < txt.length(); i++) {
            
            if (Character.isDigit(txt.charAt(i))) {
                return true;
            } else {
                
            }
        }
        return false;
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Principal().setVisible(true);
            } catch (ParseException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnAnadir;
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnPrimero;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnUltimo;
    private javax.swing.JComboBox<String> cmboTipo;
    private javax.swing.JFileChooser fcGuardarCargar;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblApellido1;
    private javax.swing.JLabel lblApellido2;
    private javax.swing.JLabel lblImgPerfil;
    private javax.swing.JLabel lblNacimiento;
    private javax.swing.JLabel lblNif;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JTextField txtApellido1;
    private javax.swing.JTextField txtApellido2;
    private javax.swing.JTextField txtNacimiento;
    private javax.swing.JTextField txtNif;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
