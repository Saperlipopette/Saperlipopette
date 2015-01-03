package linda.server;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	
	String URI;
	LindaServer serveur;
	
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "//localhost:4000/LindaServer".
     *  @throws RemoteException 
     */
    public LindaClient(String serverURI) {
    	URI = serverURI;
    	try{
			serveur = (LindaServer) Naming.lookup(URI);
		}
		catch(Exception e){
			System.out.println("Erreur lors de la création du client");
			e.printStackTrace();
		}
    }

	@Override
	public void write(Tuple t) {
		try {
			serveur.write(t);
		} catch (RemoteException e) {
			System.out.println("Erreur lors de l'écriture du Tuple");
			e.printStackTrace();
		}
	}

	@Override
	public Tuple take(Tuple template) {
		Tuple t = null;
		try {
			t =  serveur.take(template);
		} catch (RemoteException e) {
			System.out.println("Erreur lors de take");
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public Tuple read(Tuple template) {
		Tuple tuple = null;
		try {
			tuple = serveur.read(template);
		} catch (RemoteException e) {
			System.out.println("Erreur lors de read");
			e.printStackTrace();
		}
		return tuple;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		Tuple tuple = null;
		try {
			tuple = serveur.tryTake(template);
		} catch (RemoteException e) {
			System.out.println("Erreur lors du tryTake");
			e.printStackTrace();
		}
		return tuple;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		Tuple tuple = null;
		try {
			tuple = serveur.tryRead(template);
		} catch (RemoteException e) {
			System.out.println("Erreur lors du tryRead");
			e.printStackTrace();
		}
		return tuple;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		Collection<Tuple> tuples = null;
		try {
			tuples = serveur.takeAll(template);
		} catch (RemoteException e) {
			System.out.println("Erreur lors de takeAll");
			e.printStackTrace();
		}
		return tuples;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		Collection<Tuple> tuples = null;
		try {
			tuples = serveur.readAll(template);
		} catch (RemoteException e) {
			System.out.println("Erreur lors de readAll");
			e.printStackTrace();
		}
		return tuples;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		try {
			RemoteCallback cb = new RemoteCallbackImpl(callback);
			serveur.eventRegister(mode, timing, template, cb);
		} catch (RemoteException e) {
			System.out.println("Erreur lors du eventRegister");
			e.printStackTrace();
		}
	}

	@Override
	public void debug(String prefix) {
		try {
			serveur.debug(prefix);
		} catch (RemoteException e) {
			System.out.println("Erreur lors de debug");
			e.printStackTrace();
		}
	
	}
    
	
	public static void main(String[] args) {
			final class MyCallback implements Callback {
				public void call(Tuple t) {
					System.out.println("Je suis un callback et j'ai reçu : "+t);
				}
			}
			final class MyOtherCallback implements Callback {
				public void call(Tuple t) {
					System.out.println("Je suis un autre callback et j'ai reçu : "+t);
				}
			}
			System.out.println("Demarrage du client");
			Linda linda = new LindaClient("//localhost:4000/LindaServer");
			Tuple template = new Tuple(Integer.class, Integer.class);
			linda.takeAll(template);
			System.out.println("On lance le callback");
			linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, template, new MyCallback());
			linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, template, new MyOtherCallback());
			System.out.println("Terminé");
	}

}
