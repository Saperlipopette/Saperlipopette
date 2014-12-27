package linda.test;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
//Test vérifiant le quatrième tiret de la spécification libérale : quand il y a un take et un callback enregistré pour le même motif, le take est prioritaire.
public class BasicTestTakeCallbackSpec4 {


    private static class MyCallback implements Callback {
        public void call(Tuple t) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Got "+t);
        }
    }
	
	public static void main(String[] a) {

		final Linda linda = new linda.shm.CentralizedLinda();
		// final Linda linda = new
		// linda.server.LindaClient("//localhost:4000/aaa");

		new Thread() {
			public void run() {
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Tuple motif = new Tuple(Integer.class, String.class);

				linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif, new MyCallback());
				Tuple resIntStr = linda.take(motif);
				System.out.println("(1) Resultat:" + resIntStr);

				linda.debug("(1)");
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Tuple t1 = new Tuple(4, "foo");
				System.out.println("(1) write: " + t1);
				linda.write(t1);


				linda.debug("(1)");

			}
		}.start();

	}

}
