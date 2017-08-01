package graphservice;

import java.util.ArrayList;

import io.atomix.*;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.Address;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.StorageLevel;
import io.atomix.concurrent.DistributedLock;

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.Command;
import io.atomix.copycat.Query;

public class GraphStateMachine extends StateMachine{

    private final Grafo grafo = new Grafo();
    
    public GraphStateMachine(){
    
        grafo.vertices = new ArrayList<Vertice>();
        grafo.arestas = new ArrayList<Aresta>(); 
        
    }
    
	public Object createVertice(Commit<CreateVertice> commit){
		try{
			int nome = (int)commit.operation().nome();
			int cor = (int)commit.operation().cor();
			double peso = (double)commit.operation().peso();
			String descricao = (String)commit.operation().descricao();
			Vertice v = new Vertice(nome, cor, peso, descricao);
        	grafo.addToVertices(v);
        	System.out.println("-- Create Vertice");
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}

	public Object updateVertice(Commit<UpdateVertice> commit){
	
		Vertice v = null;
		try{
		/*
			int nome = (int)commit.operation().nome();
			v = findVertice(v);
			v.cor = (int)commit.operation().cor();
			v.peso = (double)commit.operation().peso();
			v.descricao = (String)commit.operation().descricao();*/
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}


	public Object createAresta(Commit<CreateAresta> commit){
		try{
		/*
			int vertice1 = (int)commit.operation().vertice1();
			int vertice2 = (int)commit.operation().vertice2();
			double peso = (double)commit.operation().peso();
			boolean direcionada = (boolean)commit.operation().direcionada();
			String descricao = (String)commit.operation().descricao();
			Aresta a = new Aresta(vertice1, vertice2, peso, direcionada, descricao);
        	grafo.addToArestas(a);*/
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}

	
	public Object readVertice(Commit<ReadVertice> commit){
		Vertice v = null;
		try{
			int nome = (int)commit.operation().nome();
			v = findVertice( nome);
        	System.out.println("-- Read Vertice");
		} catch(Exception e) {
		
		} finally {
			commit.release();
		}
		return v;
	}

	public Object readAresta(Commit<ReadAresta> commit){
		Aresta a = null;
		try{
			int vertice1 = (int)commit.operation().vertice1();
			int vertice2 = (int)commit.operation().vertice2();
			a = findAresta(vertice1, vertice2);
		} catch(Exception e) {
		
		} finally {
			commit.release();
		}
		return a;
	}	

	public Object updateAresta(Commit<UpdateAresta> commit){
		Aresta a = null;
		try{
		/*
			int vertice1 = (int)commit.operation().vertice1();
			int vertice2 = (int)commit.operation().vertice2();
			a = findAresta(vertice1, vertice2);
			a.peso = (double)commit.operation().peso();
			a.direcionada = (boolean)commit.operation().direcionada();
			a.descricao = (String)commit.operation().descricao();*/
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}
	
	public Vertice findVertice( int vertice){
        for(Vertice v: grafo.vertices){
            if(v.nome == vertice){
                return v;
            }
        }
        return null;
    }
    
    public Aresta findAresta( int vertice1, int vertice2){
        for(Aresta a : grafo.arestas){
            if((a.vertice1 == vertice1 && a.vertice2 == vertice2) || (a.vertice1 == vertice2 && a.vertice2 == vertice1)){
               return a;
            }
        }
        return null;
    }

    /*public Aresta deleteArestasVertice(Commit<DeleteArestasVertice> commit){
        for(Aresta a : (Aresta)commit.operation().arestas()){
          	try{
	        	
			} catch(Exception e){
				return false;
			} finally {
				commit.release();
			}
        }
        return null;
    }*/
	
}

class ReadVertice implements Query<Object>{
	private final Object nome;

	public ReadVertice(Object nome){
		this.nome = nome;
	}
	
	public Object nome(){
		return nome;
	}
}

class CreateVertice implements Command<Object>{
	private final Object nome;
	private final Object cor;
	private final Object peso;
	private final Object descricao;
	
	public CreateVertice(Object nome, Object cor, Object peso, Object descricao){
		this.nome = nome;
		this.cor = cor;
		this.peso = peso;
		this.descricao = descricao;
	}
	
	public Object nome(){
		return nome;
	}
	
	public Object cor(){
		return cor;
	}
	
	public Object peso(){
		return peso;
	}
	
	public Object descricao(){
		return descricao;
	}
}

/*class DeleteArestasVertice implements Command<Object>{
	private final ArrayList<Object> arestas;

	public DeleteArestasVertice(ArrayList<Object> arestas) {
		this.arestas = arestas;
	}

	public ArrayList<Object> arestas() {
		return arestas;
	}

}*/

class DeleteVertice implements Command<Object>{
	private final Object nome;

	public DeleteVertice(Object nome){
		this.nome = nome;
	}
	
	public Object nome(){
		return nome;
	}
}

class UpdateVertice implements Command<Object>{
	private final Object nome;
	private final Object cor;
	private final Object peso;
	private final Object descricao;
	
	public UpdateVertice(Object nome, Object cor, Object peso, Object descricao){
		this.nome = nome;
		this.cor = cor;
		this.peso = peso;
		this.descricao = descricao;
	}
	
	public Object nome(){
		return nome;
	}
	
	public Object cor(){
		return cor;
	}
	
	public Object peso(){
		return peso;
	}
	
	public Object descricao(){
		return descricao;
	}
}

class ReadAresta implements Query<Object>{
	private final Object vertice1;
	private final Object vertice2;

	public ReadAresta(Object vertice1, Object vertice2){
		this.vertice1 = vertice1;
		this.vertice2 = vertice2;
	}
	
	public Object vertice1(){
		return vertice1;
	}
	
	public Object vertice2(){
		return vertice2;
	}
}

class CreateAresta implements Command<Object>{
	private final Object vertice1; // required
  	private final Object vertice2; // required
  	private final Object peso; // required
  	private final Object direcionado; // required
  	private final Object descricao; // required

  	public CreateAresta(Object vertice1, Object vertice2, Object peso, Object direcionado, Object descricao) {
  		this.vertice1 = vertice1;
  		this.vertice2 = vertice2;
  		this.peso = peso;
  		this.direcionado = direcionado;
  		this.descricao = descricao;
  	}

  	public Object vertice1(){
		return vertice1;
	}
	
	public Object vertice2(){
		return vertice2;
	}

  	public Object peso(){
		return peso;
	}
	
	public Object descricao(){
		return descricao;
	}

	public Object direcionado(){
		return direcionado;
	}

}

class DeleteAresta implements Command<Object>{
	private final Object vertice1; // required
  	private final Object vertice2; // required

 	public DeleteAresta(Object vertice1, Object vertice2) {
  		this.vertice1 = vertice1;
  		this.vertice2 = vertice2;
  	}
  	 	
  	public Object vertice1(){
		return vertice1;
	}
	
	public Object vertice2(){
		return vertice2;
	}
}

class UpdateAresta implements Command<Object>{
	private final Object vertice1; // required
  	private final Object vertice2; // required
  	private final Object peso; // required
  	private final Object direcionado; // required
  	private final Object descricao; // required

  	public UpdateAresta(Object vertice1, Object vertice2, Object peso, Object direcionado, Object descricao) {
  		this.vertice1 = vertice1;
  		this.vertice2 = vertice2;
  		this.peso = peso;
  		this.direcionado = direcionado;
  		this.descricao = descricao;
  	}

  	public Object vertice1(){
		return vertice1;
	}
	
	public Object vertice2(){
		return vertice2;
	}

  	public Object peso(){
		return peso;
	}
	
	public Object descricao(){
		return descricao;
	}

	public Object direcionado(){
		return direcionado;
	}
}


