/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.List;
import javax.swing.JOptionPane;
import graphservice.Graph;
import graphservice.Vertice;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TBinaryProtocol;
/**
 *
 * @author root
 */
public class TelaListarVizinhos extends javax.swing.JInternalFrame {

    private Graph.Client client;
    private int id;
    /**
     * Creates new form TelaListarVizinhos
     */
    public TelaListarVizinhos(int id, Graph.Client client) {
	this.client = client;
	this.id = id;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Listar Amigos");

        jButton1.setText("Listar Amigos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jButton1)
                .addContainerGap(232, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addComponent(jButton1)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         

	List<Vertice> amigos;

	try {
            
	    amigos = this.client.listNeighbors(id);
	    String amg = "";
	    System.out.println(amigos.size());
	    if(amigos != null){
                    
                    for(Vertice vert: amigos){
			amg += "\n";
			amg += vert.nome;
	                amg += " - ";
			amg += vert.cidade_atual;
			amg += " - ";
			amg += vert.id; 
                    }
                    JOptionPane.showMessageDialog(null,amg);
            }
            else{
                JOptionPane.showMessageDialog(null,"Sem amigos =/");
            }

        } catch (TException ex) {
            System.out.println(ex);
        }       

    }                                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    // End of variables declaration                   
}

