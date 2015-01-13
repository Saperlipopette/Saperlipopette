package linda.testMonoServ;

import linda.Linda;
import linda.Tuple;

public class BasicTestTryTake {
	public static void main(String[] a) {

        //final Linda linda = new linda.shm.CentralizedLinda();
        final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple motif2 = new Tuple(Integer.class, Integer.class);
                
                

                Tuple resIntStr = linda.tryTake(motif);
                Tuple resIntStr2 = linda.tryTake(motif);
                
                Tuple resIntInt = linda.tryTake(motif2);
                Tuple resIntInt2 = linda.tryTake(motif2);
                Tuple resIntInt3 = linda.tryTake(motif2);

                System.out.println("(1) Resultat:" + resIntStr);
                System.out.println("(2) Resultat:" + resIntStr2);
                
                System.out.println("(3) Resultat:" + resIntInt);
                System.out.println("(4) Resultat:" + resIntInt2);
                System.out.println("(5) Resultat:" + resIntInt3);
                
                linda.debug("(1)");
            }
        }.start();
                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                System.out.println("(2) write: " + t1);
                linda.write(t1);

                Tuple t11 = new Tuple(5, 6);
                System.out.println("(2) write: " + t11);
                linda.write(t11);
                
                Tuple t111 = new Tuple(6, 7);
                System.out.println("(2) write: " + t111);
                linda.write(t111);

                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(2) write: " + t2);
                linda.write(t2);

                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(2) write: " + t3);
                linda.write(t3);
                
                Tuple t4 = new Tuple("foo", "foo");
                System.out.println("(2) write: " + t4);
                linda.write(t4);
                
                Tuple t5 = new Tuple(5, "foo");
                System.out.println("(2) write: " + t5);
                linda.write(t5);
                                
                linda.debug("(2)");

            }
        }.start();
                
    }
}
