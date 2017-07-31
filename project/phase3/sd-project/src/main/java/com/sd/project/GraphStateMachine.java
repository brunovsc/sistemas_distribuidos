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
			v = findVertice(grafo, nome);
		} catch(Exception e){
		
		} finally {
			commit.release();
		}
		return v;
	}	
	
	public Vertice findVertice(Grafo grafo, int vertice){
        for(Vertice v: grafo.vertices){
            if(v.nome == vertice){
                return v;
            }
        }
        return null;
    }
    
    public Aresta findAresta(Grafo grafo, int vertice1, int vertice2){
        for(Aresta a : grafo.arestas){
            if((a.vertice1 == vertice1 && a.vertice2 == vertice2) || (a.vertice1 == vertice2 && a.vertice2 == vertice1)){
               return a;
            }
        }
        return null;
    }
	
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

class DeleteVertice implements Command<Object>{

}

class UpdateVertice implements Command<Object>{

}

class ReadAresta implements Query<Object>{

}

class CreateAresta implements Command<Object>{

}

class DeleteAresta implements Command<Object>{

}

class UpdateAresta implements Command<Object>{

}
/*
class FindVertice implements Query<Object> {
	private final Object nome;
	
	public ReadVertice(Object nome){
		this.nome = nome;
	}
	public Object nome(){
		return nome;
	}
}*/


