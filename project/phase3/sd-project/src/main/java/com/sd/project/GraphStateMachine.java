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
			int id = (int)commit.operation().id();
			String nome = (String)commit.operation().nome();
			int idade = (int)commit.operation().idade();
			String cidade_atual = (String)commit.operation().cidade_atual();
			String contato = (String)commit.operation().contato();
			Vertice v = new Vertice(id, nome, idade, cidade_atual, contato);
        	grafo.addToVertices(v);
        	System.out.println("-- Create Vertice");
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
			int id = (int)commit.operation().id();
			v = findVertice(id);
        	System.out.println("-- Read Vertice");
		} catch(Exception e) {
		
		} finally {
			commit.release();
		}
		return v;
	}

	public Object updateVertice(Commit<UpdateVertice> commit){
		try{
			int id = (int)commit.operation().id();
			Vertice v = findVertice(id);
			v.nome = (String)commit.operation().nome();
			v.idade = (int)commit.operation().idade();
			v.cidade_atual = (String)commit.operation().cidade_atual();
			v.contato = (String)commit.operation().contato();
        	System.out.println("-- Update Vertice");
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}
	
	public Object deleteVertice(Commit<DeleteVertice> commit){
		try{
			int id = (int)commit.operation().id();
			Vertice v = findVertice(id);
			if(v != null){
				grafo.vertices.remove(v);
			}  	
        	System.out.println("-- Delete Vertice");
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}
		
	public Object deleteArestasFromVertice(Commit<DeleteArestasFromVertice> commit){
		try{
			int id = (int)commit.operation().id();
			ArrayList<Aresta> arestasToRemove = new ArrayList<Aresta>();
            for(Aresta a : grafo.arestas) {
                if(a.pessoa1 == id || a.pessoa2 == id){
                    arestasToRemove.add(a);
                }
            }
        	grafo.arestas.removeAll(arestasToRemove);  
        	System.out.println("-- Delete Arestas Vertice");	
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}

	public Object createAresta(Commit<CreateAresta> commit){
		try{
			int pessoa1 = (int)commit.operation().pessoa1();
			int pessoa2 = (int)commit.operation().pessoa2();
			double distancia = (double)commit.operation().distancia();
			boolean direcionado = (boolean)commit.operation().direcionado();
			String descricao = (String)commit.operation().descricao();
			Aresta a = new Aresta(pessoa1, pessoa2, distancia, direcionado, descricao);
        	grafo.addToArestas(a);
        	System.out.println("-- Create Aresta");
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}

	public Object readAresta(Commit<ReadAresta> commit){
		Aresta a = null;
		try{
			int pessoa1 = (int)commit.operation().pessoa1();
			int pessoa2 = (int)commit.operation().pessoa2();
			a = findAresta(pessoa1, pessoa2);
        	System.out.println("-- Read Aresta");
		} catch(Exception e) {
		
		} finally {
			commit.release();
		}
		return a;
	}	

	public Object updateAresta(Commit<UpdateAresta> commit){
		Aresta a = null;
		try{
			int pessoa1 = (int)commit.operation().pessoa1();
			int pessoa2 = (int)commit.operation().pessoa2();
			a = findAresta(pessoa1, pessoa2);
			a.distancia = (double)commit.operation().distancia();
			a.direcionado = (boolean)commit.operation().direcionado();
			a.descricao = (String)commit.operation().descricao();
        	System.out.println("-- Update Aresta");
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}
		
	public Object deleteAresta(Commit<DeleteAresta> commit){
		try{
			int pessoa1 = (int)commit.operation().pessoa1();
			int pessoa2 = (int)commit.operation().pessoa2();
			Aresta a = findAresta(pessoa1, pessoa2);
			if(a != null){
				grafo.arestas.remove(a);
			}  	
        	System.out.println("-- Delete Aresta");
		} catch(Exception e){
			return false;
		} finally {
			commit.release();
		}
		return true;
	}
	
	public Vertice findVertice( int vertice){
        for(Vertice v: grafo.vertices){
            if(v.id == vertice){
                return v;
            }
        }
        return null;
    }
    
    public Aresta findAresta( int pessoa1, int pessoa2){
        for(Aresta a : grafo.arestas){
            if((a.pessoa1 == pessoa1 && a.pessoa2 == pessoa2) || (a.pessoa1 == pessoa2 && a.pessoa2 == pessoa1)){
               return a;
            }
        }
        return null;
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////// Vertices

class ReadVertice implements Query<Object>{
	private final Object id;
	public ReadVertice(Object id){
		this.id = id;
	}
	public Object id(){
		return id;
	}
}

class CreateVertice implements Command<Object>{
	private final Object id;
	private final Object nome;
	private final Object idade;
	private final Object cidade_atual;
	private final Object contato;
	
	public CreateVertice(Object id, Object nome, Object idade, Object cidade_atual, Object contato){
		this.id = id;
		this.nome = nome;
		this.idade = idade;
		this.cidade_atual = cidade_atual;
		this.contato = contato;
	}
	
	public Object id(){
		return id;
	}
	
	public Object nome(){
		return nome;
	}
	
	public Object idade(){
		return idade;
	}
	
	public Object cidade_atual(){
		return cidade_atual;
	}
	
	public Object contato(){
		return contato;
	}
}

class UpdateVertice implements Command<Object>{
	private final Object id;
	private final Object nome;
	private final Object idade;
	private final Object cidade_atual;
	private final Object contato;
	
	public UpdateVertice(Object id, Object nome, Object idade, Object cidade_atual, Object contato){
		this.id = id;
		this.nome = nome;
		this.idade = idade;
		this.cidade_atual = cidade_atual;
		this.contato = contato;
	}
	
	public Object id(){
		return id;
	}
	
	public Object nome(){
		return nome;
	}
	
	public Object idade(){
		return idade;
	}
	
	public Object cidade_atual(){
		return cidade_atual;
	}
	
	public Object contato(){
		return contato;
	}
}

class DeleteVertice implements Command<Object>{
	private final Object id;

	public DeleteVertice(Object id){
		this.id = id;
	}
	
	public Object id(){
		return id;
	}
}

class DeleteArestasFromVertice implements Command<Object>{
	private final Object id;

	public DeleteArestasFromVertice(Object id){
		this.id = id;
	}
	
	public Object id(){
		return id;
	}
}


/////////////////////////////////////////////////////////////////////////////////////////////////// Arestas

class ReadAresta implements Query<Object>{
	private final Object pessoa1;
	private final Object pessoa2;

	public ReadAresta(Object pessoa1, Object pessoa2){
		this.pessoa1 = pessoa1;
		this.pessoa2 = pessoa2;
	}
	
	public Object pessoa1(){
		return pessoa1;
	}
	
	public Object pessoa2(){
		return pessoa2;
	}
}

class CreateAresta implements Command<Object>{
	private final Object pessoa1; // required
  	private final Object pessoa2; // required
  	private final Object distancia; // required
  	private final Object direcionado; // required
  	private final Object descricao; // required

  	public CreateAresta(Object pessoa1, Object pessoa2, Object distancia, Object direcionado, Object descricao) {
  		this.pessoa1 = pessoa1;
  		this.pessoa2 = pessoa2;
  		this.distancia = distancia;
  		this.direcionado = direcionado;
  		this.descricao = descricao;
  	}

  	public Object pessoa1(){
		return pessoa1;
	}
	
	public Object pessoa2(){
		return pessoa2;
	}

  	public Object distancia(){
		return distancia;
	}
	
	public Object descricao(){
		return descricao;
	}

	public Object direcionado(){
		return direcionado;
	}

}

class DeleteAresta implements Command<Object>{
	private final Object pessoa1; // required
  	private final Object pessoa2; // required

 	public DeleteAresta(Object pessoa1, Object pessoa2) {
  		this.pessoa1 = pessoa1;
  		this.pessoa2 = pessoa2;
  	}
  	 	
  	public Object pessoa1(){
		return pessoa1;
	}
	
	public Object pessoa2(){
		return pessoa2;
	}
}

class UpdateAresta implements Command<Object>{
	private final Object pessoa1; // required
  	private final Object pessoa2; // required
  	private final Object distancia; // required
  	private final Object direcionado; // required
  	private final Object descricao; // required

  	public UpdateAresta(Object pessoa1, Object pessoa2, Object distancia, Object direcionado, Object descricao) {
  		this.pessoa1 = pessoa1;
  		this.pessoa2 = pessoa2;
  		this.distancia = distancia;
  		this.direcionado = direcionado;
  		this.descricao = descricao;
  	}

  	public Object pessoa1(){
		return pessoa1;
	}
	
	public Object pessoa2(){
		return pessoa2;
	}

  	public Object distancia(){
		return distancia;
	}
	
	public Object descricao(){
		return descricao;
	}

	public Object direcionado(){
		return direcionado;
	}
}


