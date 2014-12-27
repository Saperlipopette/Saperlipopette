package linda.test;

import linda.*;

//Test vérifiant le second tiret de la spécification libérale : quand plusieurs take sont en attente et qu'un dépôt peut en débloquer plusieurs,
//on débloque le premier take à avoir demandé FIFO
public class BasicTestTakeSpec2 {

    public static void main(String[] a) {
                
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/aaa");
                
        for (int i = 1; i <= 3; i++) {
            final int j = i;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread() {  
            	
                public void run() {
                	 
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res = linda.take(motif);
                    System.out.println("("+j+") Resultat:" + res);
                    linda.debug("("+j+")");
                }
            }.start();
        }
//        
//        for (int i = 1; i <= 3; i++) {
//            final int j = i;
//            new Thread() {  
//                public void run() {
//                    try {
//                        Thread.sleep(2);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Tuple motif2 = new Tuple(Integer.class, Integer.class);
//                    Tuple res = linda.take(motif2);
//                    System.out.println("("+j+") Resultat:" + res);
//                    linda.debug("("+j+")");
//                }
//            }.start();
//        }
                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                System.out.println("(1) write: " + t1);
                linda.write(t1);

                Tuple t11 = new Tuple(5, 6);
                System.out.println("(2) write: " + t11);
                linda.write(t11);
                
                Tuple t111 = new Tuple(6, 7);
                System.out.println("(3) write: " + t111);
                linda.write(t111);

                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(4) write: " + t2);
                linda.write(t2);

                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(5) write: " + t3);
                linda.write(t3);
                
                Tuple t4 = new Tuple("foo", "foo");
                System.out.println("(6) write: " + t4);
                linda.write(t4);
                
                Tuple t5 = new Tuple(5, "foo");
                System.out.println("(7) write: " + t5);
                linda.write(t5);
                linda.write(t5);
                                
                linda.debug("(2)");

            }
        }.start();
                
    }
}

