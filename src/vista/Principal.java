package vista;


import daos.ContactoDAO;
import daos.FicheroDAO;
import daos.RAMContactoDAO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.Contactos;

public class Principal extends javax.swing.JFrame {
    
    ContactoDAO ram = new RAMContactoDAO();
    ContactoDAO fdao = new FicheroDAO();
    ArrayList<Character> letrasNif = new ArrayList<>();
    ArrayList<Contactos> contacto = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Estado e;
    int avance;
    int remove;
    int count;
    Contactos c;

    public Principal() throws ParseException {
        initComponents();
        añadeLetrasArrayList();
        this.setLocationRelativeTo(null);
        sdf.setLenient(false);
        contacto=(ArrayList<Contactos>) ram.getAllContacto();
        count = 0;

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
        c=contacto.get(avance);
        if (c.getNif().equals(txtNif.getText())) {
            ram.removeContacto(c);
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
        //c.setNif(txtNif.getText());
        c.setNombre(txtNombre.getText());
        c.setApellido1(txtApellido1.getText());
        c.setApellido2(txtApellido2.getText());
        c.setTelefono(Integer.parseInt(txtTelefono.getText()));
        c.setNacimiento(sdf.parse(txtNacimiento.getText()));
        c.setTipo(cmboTipo.getSelectedItem().toString());
        contacto.set(avance, c);
        navegar();
    }

    enum Estado {
        NAVEGANDO,
        ANHADIENDO,
        EDITANDO,
        BORRANDO
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
            String[] s = {"Añadir","Cargar","Salir"};
            //int respuesta=JOptionPane.showConfirmDialog(null,"Lista vacia,¿que desea hacer?","Aviso",JOptionPane.YES_NO_OPTION);
            int respuesta = JOptionPane.showOptionDialog(null, "Lista vacia,¿que desea hacer?", "Aviso", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, s, s[0]);
             
            
            switch (respuesta) {
                case -1:
                    anhadir();
                case 0:
                    break;
                case 1:
                try {
                   cargarDatos();
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

            txtNif.setText("");
            txtNombre.setText("");
            txtApellido1.setText("");
            txtApellido2.setText("");
            txtTelefono.setText("");
            txtNacimiento.setText("");

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

            txtNif.setText("");
            txtNombre.setText("");
            txtApellido1.setText("");
            txtApellido2.setText("");
            txtTelefono.setText("");
            txtNacimiento.setText("");

            txtNif.setEditable(true);
            txtNombre.setEditable(true);
            txtApellido1.setEditable(true);
            txtApellido2.setEditable(true);
            txtTelefono.setEditable(true);
            txtNacimiento.setEditable(true);
        }
    }

    private void navegar() {
        if (ram.getAllContacto().isEmpty()) {
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

        txtNif.setEnabled(true);
        txtNif.setEditable(false);
        txtNombre.setEditable(false);
        txtApellido1.setEditable(false);
        txtApellido2.setEditable(false);
        txtTelefono.setEditable(false);
        txtNacimiento.setEditable(false);

        cmboTipo.setEnabled(false);
        if (count == 0) {
            Contactos contact = ram.getAllContacto().get(0);
            
            txtNif.setText(contact.getNif());
              txtNombre.setText(contact.getNombre());
            txtApellido1.setText(contact.getApellido1());
            txtApellido2.setText(contact.getApellido2());
            txtTelefono.setText(String.valueOf(contact.getTelefono()));
            txtNacimiento.setText(sdf.format(contact.getNacimiento()));
            cmboTipo.setSelectedItem(contact.getTipo());
            //txtTipo.setText(contacto.get(0).getTipo());
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
        Contactos contact = ram.getAllContacto().get(valor);
        txtNif.setText(contact.getNif());
        txtNombre.setText(contact.getNombre());
        txtApellido1.setText(contact.getApellido1());
        txtApellido2.setText(contact.getApellido2());
        txtTelefono.setText(String.valueOf(contact.getTelefono()));
        txtNacimiento.setText(sdf.format(contact.getNacimiento()));
        cmboTipo.setSelectedItem(contact.getTipo());
        //txtTipo.setText(contacto.get(valor).getTipo());

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
            c = new Contactos(txtNif.getText(), txtNombre.getText(),
                    txtApellido1.getText(), txtApellido2.getText(), Integer.parseInt(txtTelefono.getText()),
                    sdf.parse(txtNacimiento.getText()), cmboTipo.getSelectedItem().toString());
            
            ram.addContacto(c);
            
            
            e = Estado.NAVEGANDO;
            avance = 0;
            count = 0;
            seleccionEstado(e);
        }
    }

    private void guardarDatos(String nombre) throws FileNotFoundException, IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombre, false));
            oos.writeObject(ram.getAllContacto());
    }
    
    private boolean cargarDatos() throws FileNotFoundException, IOException, ClassNotFoundException{
        count=0;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("contactos"));
        
        contacto=(ArrayList<Contactos>) ois.readObject();
        
        e=Estado.NAVEGANDO;
        seleccionEstado(e);
        
        return true;
    }
    
    private void añadeLetrasArrayList() {

        char letrasNifs[] = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
        for (int i = 0; i < letrasNifs.length; i++) {
            letrasNif.add(letrasNifs[i]);
        }
    }

    private boolean compruebaNif(String nif) {
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        lblNif.setText("NIF");

        lblNombre.setText("Nombre");

        lblApellido1.setText("Apellido 1");

        lblApellido2.setText("Apellido 2");

        lblTelefono.setText("Telefono");

        lblNacimiento.setText("Nacimiento");

        lblTipo.setText("Tipo");

        cmboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enemigo", "Amigo", "Trabajo", "Familiar" }));

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
                .addGap(56, 56, 56))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNif)
                            .addComponent(txtNif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAnadir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBorrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAceptar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCargar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(98, 98, 98))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAceptar, btnAnadir, btnBorrar, btnCancelar, btnEditar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(btnAnadir)
                        .addGap(48, 48, 48)
                        .addComponent(btnBorrar)
                        .addGap(48, 48, 48)
                        .addComponent(btnEditar)
                        .addGap(87, 87, 87)
                        .addComponent(btnAceptar)
                        .addGap(46, 46, 46)
                        .addComponent(btnCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardar))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSiguiente)
                    .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCargar))
                .addContainerGap(108, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAceptar, btnAnadir, btnBorrar, btnCancelar, btnEditar});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAnterior, btnPrimero});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        switch (e) {
            case ANHADIENDO: {
                if (compruebaNif(txtNif.getText().toUpperCase())) {
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
        txtNif.setText(contacto.get(contacto.size() - 1).getNif());
        txtNombre.setText(contacto.get(contacto.size() - 1).getNombre());
        txtApellido1.setText(contacto.get(contacto.size() - 1).getApellido1());
        txtApellido2.setText(contacto.get(contacto.size() - 1).getApellido2());
        txtTelefono.setText(String.valueOf(contacto.get(contacto.size() - 1).getTelefono()));
        txtNacimiento.setText(sdf.format(contacto.get(contacto.size() - 1).getNacimiento()));
        cmboTipo.setSelectedItem(contacto.get(contacto.size() - 1).getTipo());
        //txtTipo.setText(contacto.get(contacto.size() - 1).getTipo());
        avance = contacto.size() - 1;
        navegar();
    }//GEN-LAST:event_btnUltimoActionPerformed

    private void btnPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimeroActionPerformed
        txtNif.setText(contacto.get(0).getNif());
        txtNombre.setText(contacto.get(0).getNombre());
        txtApellido1.setText(contacto.get(0).getApellido1());
        txtApellido2.setText(contacto.get(0).getApellido2());
        txtTelefono.setText(String.valueOf(contacto.get(0).getTelefono()));
        txtNacimiento.setText(sdf.format(contacto.get(0).getNacimiento()));
        cmboTipo.setSelectedItem(contacto.get(0).getTipo());
        //txtTipo.setText(contacto.get(0).getTipo());
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
            guardarDatos("contactos");
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
        try {
            try {
                cargarDatos();
            } catch (FileNotFoundException | ClassNotFoundException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCargarActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblApellido1;
    private javax.swing.JLabel lblApellido2;
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
