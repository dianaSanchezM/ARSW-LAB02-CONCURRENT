package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private AtomicInteger health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    
    private String state;
   
    private Object objeto;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
        state="alive";
        objeto=new Object();
    }

    public void run() {
        while (true){
            synchronized (health){
                while ("alive".equals(state)) {
                
                    Immortal im;

                    int myIndex = immortalsPopulation.indexOf(this);

                    int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                    //avoid self-fight
                    if (nextFighterIndex == myIndex) {
                        nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                    }

                    im = immortalsPopulation.get(nextFighterIndex);
              
                        this.fight(im);
                
                    

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                synchronized(objeto){
                    try {
                        objeto.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Immortal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            
        }    
        

    }

     public void fight(Immortal i2) {
         synchronized(immortalsPopulation){
             
             if (i2.getHealth().get() > 0) {
                    i2.changeHealth(i2.getHealth().get() - defaultDamageValue);
                    
                    this.health.addAndGet(defaultDamageValue);
                    updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
         }
                
            

    }

    public void changeHealth(int v) {
        health.set(v);
    }

     public AtomicInteger getHealth() {
        
        return health;
    }

    @Override
    public String toString() {
        
        return name + "[" + health + "]";
    }

    public void pause() {
        state = "pause";
        
        
    }
    
    public void resumeImmortal(){
        state = "alive";
        synchronized(objeto){
            objeto.notifyAll();
        }
    }

}
