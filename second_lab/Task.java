/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

import java.util.ArrayList;

/**
 *
 * @author Bruno
 */
public class Task {

    public static final int N = 30;
    public static ArrayList<UpperCaseThread> threads;
    public static String message;
    public static int iterations = 0;
    
    public static void main(String args[]) throws InterruptedException{
        threads = new ArrayList<>();
        message = "abCdEfgHiJkLMnOpqRsTuvWxYZAbcDefghIjKlmNOPqrstUvwxYzaBcdefgHijkLMnopqrStUVwxyZaBCDefGhijKLmnopqrsTUVWxyZ";
        System.out.printf("Message length: %d\n\n", message.length());
        
        for(int i = 0; i < N; i++){
            UpperCaseThread t = new UpperCaseThread(i);
            threads.add(t);
            t.start();
        }       
        
        threads.get(0).setMessage(message);
        threads.get(0).setProcess(true);
        
        for(UpperCaseThread t: threads){
            t.join();
        }
        
        System.out.println("\nProcess finished with message: " + message);
        System.out.println("Number of iterations: " + iterations);
    }
    
}
