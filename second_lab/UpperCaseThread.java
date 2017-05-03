/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

/**
 *
 * @author Bruno
 */
public class UpperCaseThread extends Thread{

    private final int id;
    private String message;
    private boolean process;
    private boolean kill;

    UpperCaseThread(int i) {
        this.id = i;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProcess(boolean process) {
        this.process = process;
    }
    
    public void setKill(boolean kill){
        this.kill = kill;
    }
    
    @Override
    public void run() {
       
        UpperCaseThread nextThread = Task.threads.get((this.id+1)%Task.N);
        
        System.out.printf("Started thread %2d\n", this.id);
        
        while(true){
            if(kill){
                nextThread.setKill(true);
                break;
            }
            if(process){           
                System.out.printf("---- Begin process in Thread %2d with message %s\n", this.id, this.message);
                String newMessage = this.message;
                              
                if(!message.toUpperCase().equals(message)){   
                    newMessage = updateMessage(message);
                       
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    } finally{
                    
                        this.process = false;
                        nextThread.setMessage(newMessage);
                        nextThread.setProcess(true);
                        
                    }
                }   
                else{
                    System.out.println("---- Processing done!\n");
                    Task.message = message;
                    nextThread.setKill(true);
                    break;
                }
                
                System.out.printf("------ End process in Thread %2d with message %s\n", this.id, newMessage);
                
            } else{
                try {
                    sleep(1);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }
        
        System.out.printf("Killed thread %2d\n", this.id);
        
    }
    
    private String updateMessage(String message){
        String newMessage = message;
        for(int i = 0; i < message.length(); i++){
            String c = message.substring(i, i+1);
            if(!c.toUpperCase().equals(c)){
                newMessage = message.replaceFirst(c, c.toUpperCase());
                Task.iterations++;
                break;
            }
        }
        return newMessage;
    }
    
}
