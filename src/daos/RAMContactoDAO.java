package daos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import modelo.Contactos;

public class RAMContactoDAO implements ContactoDAO {

    private ArrayList<Contactos> contacto = new ArrayList<>();
    private Contactos con;
    private static final String PREDETERMINADO = "default.png";

    /*ImageIcon icon = new ImageIcon(PREDETERMINADO);
    Contactos c1 = new Contactos("08647651L", "Pepito", "Pepon", "Papo", 981137131, new Date(91, 11, 2), "Amigo", icon);
    Contactos c2 = new Contactos("85364721Z", "Rodrigo", "Castro", "Fernandez", 562818654, new Date(99, 5, 20), "Enemigo", icon);
    Contactos c3 = new Contactos("32074238W", "Sonia", "Calvo", "Pelon", 268172697, new Date(89, 0, 15), "Trabajo", icon);
    Contactos c4 = new Contactos("78551354Y", "Marta", "Muerta", "Morto", 945678123, new Date(98, 6, 7), "Familiar", icon);*/

    public RAMContactoDAO() {
        /*contacto.add(c1);
        contacto.add(c2);
        contacto.add(c3);
        contacto.add(c4);*/
    }

    @Override
    public List<Contactos> getAllContacto() {
        return contacto;
    }

    @Override
    public Contactos getContactoByNIF(String NIF) {
        for (Contactos c : contacto) {
            if (c.getNif().equals(NIF)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public boolean addContacto(Contactos c) {
        if (contacto.contains(c)) {
            return false;
        } else {
            contacto.add(c);
            return true;
        }
    }

    @Override
    public boolean removeContacto(Contactos c) {
        return contacto.remove(c);
    }

    @Override
    public boolean updateContacto(Contactos c) {
        int indice;
        if ((indice = contacto.indexOf(c)) == -1) {
            return false;
        } else {
            contacto.set(indice, c);
            return true;
        }
    }

}
