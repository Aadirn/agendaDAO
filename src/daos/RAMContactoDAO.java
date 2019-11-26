package daos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Contactos;

public class RAMContactoDAO implements ContactoDAO {

    private ArrayList<Contactos> contacto = new ArrayList<>();
    private Contactos con;
    
    Contactos c1 = new Contactos("12345678A", "Pepe", "Popo", "Pupu", 981137131, new Date(11,5,11), "Amigo");
    Contactos c2 = new Contactos("12345678B", "Rara", "Popo", "Pupu", 753786786, new Date(10,5,6), "Enemigo");
    Contactos c3 = new Contactos("12345678C", "Titi", "Popo", "Pupu", 453583452, new Date(6,2,11), "Trabajo");
    Contactos c4 = new Contactos("12345678D", "Coco", "Popo", "Pupu", 453453453, new Date(3,5,11), "Amigo");

    public RAMContactoDAO() {
        contacto.add(c1);
        contacto.add(c2);
        contacto.add(c3);
        contacto.add(c4);
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
