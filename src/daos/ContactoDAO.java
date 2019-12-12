package daos;

import java.util.List;
import modelo.Contactos;

public interface ContactoDAO {

    
    //Operaciones CRUD de Contacto
    List <Contactos> getAllContacto();
    Contactos getContactoByNIF(String NIF);
    boolean addContacto(Contactos c);
    boolean removeContacto(Contactos c);
    boolean updateContacto (Contactos c);
    
}
