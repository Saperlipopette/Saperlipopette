package linda.shm;

import java.util.Observable;
import java.util.Observer;

import linda.Callback;
import linda.Linda;
import linda.Linda.eventMode;
import linda.Tuple;

public class MyObserver implements Observer {

	private Tuple template;
	private Callback callback;
	private eventMode mode;

	public MyObserver(eventMode mode, Tuple template, Callback callback) {
		super();
		this.mode = mode;
		this.template = template;
		this.callback = callback;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		// System.out.println("object updated");
		if (o instanceof Linda) {
			Tuple tuple = (Tuple) arg;
			// si le nouveau tuple correspond au template
			if (tuple.matches(template)) {
				// System.out.println("tuple matches");
				// on regarde si le tuple correspondant existe toujours dans la
				// liste (car d'autres observateurs en mode take ou des
				// takeMatchEnAttente pourront
				// avoir été prioritaires et l'avoir enlevé de la liste)

				// si le tuple est dans la liste
				if (((CentralizedLinda) o).getDernierElement() == true) {
					// on effectue l'action puis on supprime l'observer
					callback.call(tuple);
					o.deleteObserver(this);
					//dans le cas d'un take, on supprime le tuple et on indique qu'il a été pris pour les prochains observers
					if (mode == eventMode.TAKE) {
						((CentralizedLinda) o).setDernierElement(false);
						((CentralizedLinda) o).getTuples().remove(tuple);
					}
				}
			}
		}
	}
}
