package linda.test;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class BasicTestAsyncCallbackReenregistre {

    private static class MyCallback implements Callback {

        Linda linda;
        Tuple motif;
    	
    	public MyCallback(Linda linda, Tuple motif) {
    		this.linda = linda;
    		this.motif = motif;
    	}
    	
        public void call(Tuple t) {
            System.out.println("Got "+t);
            linda.eventRegister(eventMode.READ, eventTiming.FUTURE, motif, new AsynchronousCallback(new MyCallback(linda, motif)));
            
        }
    }

    public static void main(String[] a) {

        Linda linda = new linda.shm.CentralizedLinda();
        //Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");


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
        linda.eventRegister(eventMode.READ, eventTiming.IMMEDIATE, motif, new AsynchronousCallback(new MyCallback(linda, motif)));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        
        Tuple t4 = new Tuple(5, "foo");
        System.out.println("(2) write: " + t4);
        linda.write(t4);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        
        Tuple t5 = new Tuple(6, "foo");
        System.out.println("(2) write: " + t5);
        linda.write(t5);
        
        linda.debug("(2)");

    }
}
