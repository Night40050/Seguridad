package Security.Backend.seguridad.Controladores;
import Security.Backend.seguridad.Modelos.Usuario;
import Security.Backend.seguridad.Repositorios.RepositorioRol;
import Security.Backend.seguridad.Repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@CrossOrigin
//permite definir que esta clase servirá como puerta de entrada al servidor para llevar a cabo todas las tareas del CRUD
@RestController
//definir la sub ruta de acceso la cual se utilizará para activar los métodos programados desde el servidor
@RequestMapping("/usuarios")
public class ControladorUsuario {
    //inyecta la dependencia del objeto implícitamente. Utiliza internamente inyección de setter o constructor
    @Autowired
    //parámetro de la clase el repositorio de usuario el cual servirá para llevar a cabo las transacciones desde el controlador a la base de datos
    private RepositorioUsuario miRepositorioUsuario;
    //define que cuando se solicite una petición a la ruta “url-server/usuarios” por el método GET active el método “index”
    @Autowired
    private RepositorioRol miRepositorioRol;
    @GetMapping("")
    public List<Usuario> index(){
        return this.miRepositorioUsuario.findAll();
    }
    @ResponseStatus(HttpStatus.CREATED)
    //permite definir que allí está el método que se debe activar cuando se realice una petición a la ruta “url-server/usuarios” por el método POST
   //sirve fundamentalmente para llevar a cabo la creación la creación de un nuevo objeto para ser guardado en la base de datos
    @PostMapping
    public Usuario create(@RequestBody Usuario infoUsuario){
        infoUsuario.setContrasena(convertirSHA256(infoUsuario.getContrasena()));
        return this.miRepositorioUsuario.save(infoUsuario);
    }
    @GetMapping("{id}")
    public Usuario show(@PathVariable String id){
        Usuario usuarioActual=this.miRepositorioUsuario
                .findById(id)
                .orElse(null);
        return usuarioActual;
    }
    /**
    * Relacion (1 a n) entre Rol y Usuario
    */
    @PutMapping("{id}/rol/{id_rol}")
    public Usuario asignarRolAUsuario(@PathVariable String id,@PathVariable String id_rol){
        Usuario usuarioActual=this.miRepositorioUsuario.findById(id).orElseThrow(RuntimeException::new);
        //Rol rolActual=this.miRepositorioRol.findById(id_rol).orElseThrow(RuntimeException::new);
        //usuarioActual.setRol(rolActual);
        return this.miRepositorioUsuario.save(usuarioActual);
    }

    @PutMapping("{id}")
    public Usuario update(@PathVariable String id,@RequestBody Usuario infoUsuario){
        Usuario usuarioActual=this.miRepositorioUsuario.findById(id).orElse(null);

        if (usuarioActual!=null){
            usuarioActual.setSeudonimo(infoUsuario.getSeudonimo());
            usuarioActual.setCorreo(infoUsuario.getCorreo());
            usuarioActual.setContrasena(convertirSHA256(infoUsuario.getContrasena()));
            return this.miRepositorioUsuario.save(usuarioActual);
        }else{
            return null;
        }
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        Usuario usuarioActual=this.miRepositorioUsuario.findById(id).orElse(null);
        if (usuarioActual!=null){
            this.miRepositorioUsuario.delete(usuarioActual);
        }
    }
    public String convertirSHA256(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for(byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}