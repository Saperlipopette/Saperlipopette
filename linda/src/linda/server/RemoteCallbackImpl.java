package linda.server;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import linda.Callback;
import linda.Tuple;

@SuppressWarnings("serial")
public class RemoteCallbackImpl extends UnicastRemoteObject implements RemoteCallback {

	Callback callback;
	
	protected RemoteCallbackImpl(Callback callback) throws RemoteException {
		super();
		this.callback = callback;
	}

	@Override
	public void call(Tuple t) throws RemoteException {
		callback.call(t);
	}

}
