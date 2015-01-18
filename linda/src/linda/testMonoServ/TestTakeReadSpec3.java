package linda.testMonoServ;

import linda.*;

//Test vérifiant le troisieme tiret de la spécification libérale : quand des read et un take sont en attente, et qu'un dépôt peut les débloquer, on les débloque dans l'ordre de demande (FIFO)
public class TestTakeReadSpec3 {

	public static void main(String[] a) {

        //final Linda linda = new linda.shm.CentralizedLinda();
        final Linda linda = new linda.mono.server.LindaClient("//localhost:4000/LindaServer");
        
		for (int i = 1; i <= 4; i++) {
			final int j = i;

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Thread() {
				public void run() {
					Tuple motif = new Tuple(Integer.class, String.class);
					Tuple res;
					if (j == 1) {
						res = linda.read(motif);
					} else if (j == 3) {
						res = linda.take(motif);
					} else {
						res = linda.read(motif);
					}
					System.out.println("(" + j + ") Resultat:" + res);
					linda.debug("(" + j + ")");
				}
			}.start();
		}

		new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Tuple t1 = new Tuple(4, 5);
				System.out.println("(0) write: " + t1);
				linda.write(t1);

				Tuple t2 = new Tuple("hello", 15);
				System.out.println("(0) write: " + t2);
				linda.write(t2);

				Tuple t3 = new Tuple(4, "foo");
				System.out.println("(0) write: " + t3);
				linda.write(t3);
				linda.write(t3);

				// linda.debug("(0)");

			}
		}.start();

	}
}
