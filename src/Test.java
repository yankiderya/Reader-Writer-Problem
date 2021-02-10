import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class Test {

    public static void main(String [] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();							   	//We created a new thread pool that creates new threads.
        ReadWriteLock RW = new ReadWriteLock();														   	//We have a ReadWriteLock class and we create an object from there.							   

        executorService.execute(new Writer(RW));                                                       	// We start threads to run from Writer Runnable class.
        executorService.execute(new Writer(RW));													   	// We start threads to run from Writer Runnable class.
        executorService.execute(new Writer(RW));													 	// We start threads to run from Writer Runnable class.
        executorService.execute(new Writer(RW));													   	// We start threads to run from Writer Runnable class.

        executorService.execute(new Reader(RW));													   	// We start threads to run from Reader Runnable class.
        executorService.execute(new Reader(RW));													   	// We start threads to run from Reader Runnable class.
        executorService.execute(new Reader(RW));													   	// We start threads to run from Reader Runnable class.
        executorService.execute(new Reader(RW));													   	// We start threads to run from Reader Runnable class.
        
    }
}

class Writer implements Runnable																		//Create a class to run the threads in writing process.
{
    private ReadWriteLock RW_lock;																		//We create an object from ReadWriteLock named RW_lock.

    public Writer(ReadWriteLock rw) {																	//A constructor of writer class which assigns object of ReadWriteLock class.
        RW_lock = rw;
    }

    public void run() {
        while (true){																					//While threads are still running,
            try {
                RW_lock.writeLock();																	//We use writerLock() function for the threads which get in the writing critical section.
                RW_lock.writeUnlock();																	//We use writerUnock() function for the threads after the thread exit from the critical section.
            } catch (InterruptedException e) {
                e.printStackTrace();																	//Use this to handle exceptions and errors.
            }
        }
    }
}


class Reader implements Runnable																		//A class to run the threads in reading process!
{
    private ReadWriteLock RW_lock;																		//We create an object from ReadWriteLock named RW_lock.

    public Reader(ReadWriteLock rw) {																	//A constructor of writer class which assigns object of ReadWriteLock class.
        RW_lock = rw;
    }
    public void run() {
        while (true){																					//While threads are still running,
            try {
                RW_lock.readLock();																		//We use readerLock() function for the threads which get in the reading critical section.
                RW_lock.readUnlock();																	//We use readerUnlock() function for the threads after the threat exit from the critical section.
            } catch (InterruptedException e) {
                e.printStackTrace();																	//Use this to handle exceptions and errors.
            }
        }
    }
}

class ReadWriteLock{
	
	
    private Semaphore R_Lock = new Semaphore(1);														//Specify Reader Lock semaphore!
    private Semaphore W_Lock = new Semaphore(1);														//Specify Writer Lock semaphore!
    	
    volatile static int counter = 0;																	//To keep count of the number of readers as an atomic integer.


    public void readLock() throws InterruptedException {                                              		
    	Thread.sleep((1500));																			//Wait 1500 millis before starting the reading process.
        R_Lock.acquire();																				//We use acquire() to prevent race conditions by getting permit from this semaphore,...
        																								// ... blocking until one is available.
        counter++;																						//Counter increases!
        
        if (counter == 1) {																				//If counter is equal to 1, do this!
        	
        	W_Lock.acquire();																			//It means this is the first "reader" so it is going to block "writer".
        }
        System.out.println("Thread "+Thread.currentThread().getName() + " ----> Reading!");				//Print "reading" next to the thread which is in the process of reading.
  	       
        R_Lock.release();																				//We use release() to prevent race conditions by releasing permit, ...
        																								// ... returning it to the semaphore.
    }
    public void readUnlock() throws InterruptedException {
        																								//Releasing section.
    	Thread.sleep((1500));																			//Wait 1500 millis before finishing the reading process.
        R_Lock.acquire();																				//We use acquire() to prevent race conditions by getting permit from this semaphore,...
																										// ... blocking until one is available.
        
        counter--;																						//When a "reader" exit from critical section, counter decreases!
        
        if(counter == 0) {                                                                              //If counter is equal to 0, do this!
        	
        	W_Lock.release();																			//It means this is the last "reader" so it is going to revive the "writer".
        	
        }
        System.out.println("Thread "+Thread.currentThread().getName() + " ----> Reading is OVER!");		//Print "reading is over" next to the reading process which is over.
        R_Lock.release();																				//We use release() to prevent race conditions by releasing permit, ...
																										// ... returning it to the semaphore.      
    }
    public void writeLock() throws InterruptedException {   	
    	Thread.sleep((1500));																			//Wait 1500 millis before starting the writing process.
    	W_Lock.acquire();																				//We use acquire() to prevent race conditions by getting permit from this semaphore,...
																										// ... blocking until one is available.
    	
            System.out.println("Thread "+Thread.currentThread().getName() + " ----> Writing!");         //Print "writing" next to the written thread.
   																			 	

    }
    public void writeUnlock() throws InterruptedException{         
    	Thread.sleep((1500));																			//Wait 1500 millis before finishing the writing process.
        System.out.println("Thread "+Thread.currentThread().getName() + " ----> Writing is OVER!");     //Print "writing is over" next to the writing process which is over.

    	W_Lock.release();																				//We use release() to prevent race conditions by releasing permit, ...
																										// ... returning it to the semaphore.						
    }

}




