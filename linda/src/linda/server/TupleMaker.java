package linda.server;

import java.rmi.Naming;
import linda.Tuple;

public class TupleMaker {

	public static void main(String[] args) {
		int nb1 = 0, nb2 = 0;	
		boolean parametresOk = false;
		try{
			Integer I = new Integer(args[0]);
			nb1 = I.intValue();
			I = new Integer(args[1]);
			nb2 = I.intValue();
			parametresOk = true;
		}
		catch(Exception e){
			System.out.println("Il faut 2 entiers en paramètres");
		}
		if(parametresOk){
			try{
			LindaServer serveur = (LindaServer) Naming.lookup("//localhost:4000/LindaServer");
			serveur.write(new Tuple(nb1, nb2));
			}
			catch(Exception e){
			System.out.println("Erreur de connection au serveur");
			e.printStackTrace();
			}
		}
	}
}
