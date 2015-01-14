package linda.testmultiServ;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collection;

import linda.server.LindaMultiServerImpl;
import linda.server.LindaServer;

public class DemarrageServeurs {

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		int i;
		int nbServeur = 4;
		try {
			Collection<Integer> ports = new ArrayList<Integer>();
			Collection<LindaMultiServerImpl> serveurs = new ArrayList<LindaMultiServerImpl>();
			for (i=0;i<nbServeur;i++) {
				int port = 4000+i;
				ports.add(port);
				LindaMultiServerImpl serveur = new LindaMultiServerImpl(port,ports);
				LocateRegistry.createRegistry(port);
				Naming.rebind(serveur.getURL(),serveur);
				serveurs.add(serveur);
			}
			for (LindaMultiServerImpl s : serveurs) {
				s.setAutreServeur();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
