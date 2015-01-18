package linda.mono.server;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.Callback;
import linda.shm.CentralizedLinda;

@SuppressWarnings("serial")
public class LindaServerImpl extends UnicastRemoteObject implements LindaServer {

	private CentralizedLinda linda;
	
	public static void main(String[] args) {
		System.out.println("Demarrage du serveur");	
		String URL = "//localhost:4000/LindaServer";
		int port = 4000;
		try{
			LindaServer serveur = new LindaServerImpl();
			LocateRegistry.createRegistry(port);
			Naming.rebind(URL,serveur);
		}
		catch(Exception e){
			System.out.println("Une erreur s'est produite");
			e.printStackTrace();
		}	
	}
	
	protected LindaServerImpl() throws RemoteException {
		super();
		linda = new CentralizedLinda();
	}

	@Override
	public void write(Tuple t) throws RemoteException {	
		linda.write(t);
	}

	@Override
	public Tuple tryTake(Tuple template) throws RemoteException {
		return linda.tryTake(template);
	}

	@Override
	public Tuple tryRead(Tuple template) throws RemoteException {
		return linda.tryRead(template);
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
		return linda.takeAll(template);
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) throws RemoteException {
		return linda.readAll(template);
	}

	@Override
	public void debug(String prefix) throws RemoteException {	
		linda.debug(prefix);
	}

	@Override
	public Tuple take(Tuple template) {
		Tuple t = null;
		t = linda.take(template);
		return t;
	}

	@Override
	public Tuple read(Tuple template) {
		Tuple t = null;
		t = linda.read(template);
		return t;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing,
		Tuple template, final RemoteCallback callback) throws RemoteException {
	
		final class newCallback implements Callback {
	        public void call(Tuple t) {
	            try {
					callback.call(t);
				} catch (RemoteException e) {
					System.out.println("Erreur lors de l'appel du callback");
					e.printStackTrace();
				}
	        }
	    }	
		linda.eventRegister(mode, timing, template, new newCallback() );
	}

}
