package linda.server;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.Callback;
import linda.shm.CentralizedLinda;

@SuppressWarnings("serial")
public class LindaMultiServerImpl extends UnicastRemoteObject implements LindaServer {

	private CentralizedLinda linda;
	private Collection<Integer> ports;
	private int port;
	private String URL;
	private Collection<LindaClient> autreServeur;
	
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
	
	public LindaMultiServerImpl(int port, Collection<Integer> ports) throws RemoteException {
		super();
		linda = new CentralizedLinda();
		this.ports = ports;
		this.port = port;
		this.URL = "//localhost:" + port + "/LindaServer";	
		this.autreServeur = new ArrayList<LindaClient>();
	}

	public void setAutreServeur() {
		for (int p : ports) {
			this.autreServeur.add(new LindaClient("//localhost:" + p + "/LindaServer"));
		}
	}

	public Collection<Integer> getPorts() {
		return ports;
	}

	public int getPort() {
		return port;
	}

	public String getURL() {
		return URL;
	}

	@Override
	public void write(Tuple t) throws RemoteException {	
		linda.write(t);
	}

	@Override
	public Tuple tryTake(Tuple template) throws RemoteException {
		Tuple t = linda.tryTake(template);
		if (t==null) {
			Iterator<LindaClient> it = autreServeur.iterator();
			while (t==null && it.hasNext()) {
				t = it.next().tryTake(template);
			}
		}
		return t;
	}

	@Override
	public Tuple tryRead(Tuple template) throws RemoteException {
		Tuple t = linda.tryRead(template);
		if (t==null) {
			Iterator<LindaClient> it = autreServeur.iterator();
			while (t==null && it.hasNext()) {
				t = it.next().tryRead(template);
			}
		}
		return t;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
		Collection<Tuple> tuples = linda.takeAll(template);
		Iterator<LindaClient> it = autreServeur.iterator();
		while (it.hasNext()) {
			tuples.addAll(it.next().takeAll(template));
		}		
		return tuples;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) throws RemoteException {
		Collection<Tuple> tuples = linda.readAll(template);
		Iterator<LindaClient> it = autreServeur.iterator();
		while (it.hasNext()) {
			tuples.addAll(it.next().readAll(template));
		}		
		return tuples;
	}

	@Override
	public void debug(String prefix) throws RemoteException {	
		linda.debug(prefix);
	}

	@Override
	public Tuple take(Tuple template) {
		
		Tuple tuple;
		final Semaphore s = new Semaphore(0);
		
		final class callbackTake implements Callback {
			private boolean stop = false;
			public void stopper(){
				stop = true;
			}
	        public void call(Tuple t) {
	        	if(!stop){
	        		 s.release();
	        	}
	        }
	    }
		
		callbackTake callbackLocal = new callbackTake();
		linda.eventRegister(eventMode.READ, eventTiming.FUTURE, template, callbackLocal);
		s.acquire();
		callbackLocal.stopper();
		
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
