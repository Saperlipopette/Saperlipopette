
package linda.testMonoServ;

import linda.*;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class BasicTestCallbackReadImmediate {

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


        //final Linda linda = new linda.shm.CentralizedLinda();
        final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");

        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "foo");
        System.out.println("(2) write: " + t3);
        linda.write(t3);

        
        Tuple motif = new Tuple(Integer.class, String.class);
        Tuple motif2 = new Tuple(Integer.class, Integer.class);
        
       
        // On vérifie que le eventRegister en mode read en timing Immediate fonctionne correctement
        linda.eventRegister(eventMode.READ, eventTiming.IMMEDIATE, motif, new MyCallback());
        linda.eventRegister(eventMode.READ, eventTiming.IMMEDIATE, motif2, new MyCallback());
        linda.eventRegister(eventMode.READ, eventTiming.IMMEDIATE, motif, new MyCallback());

        Tuple t4 = new Tuple(5, "foo");
        System.out.println("(2) write: " + t4);
        linda.write(t4);
        
        linda.eventRegister(eventMode.READ, eventTiming.IMMEDIATE, motif, new MyCallback());
        
        
        linda.debug("(2)");

    }

}
