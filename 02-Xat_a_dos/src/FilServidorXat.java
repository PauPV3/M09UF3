import java.io.*;

public class FilServidorXat implements Runnable {
    private ObjectInputStream in; 

    public FilServidorXat(ObjectInputStream in) {
        this.in = in;
    }
    
    @Override
    public void run() {
        try {
            String missatge = (String) in.readObject();
            System.out.println("Missatge rebut: " + missatge);
            
        } catch (Exception e) {
            System.err.println("Error al fil del servidor");
        }
    }
}