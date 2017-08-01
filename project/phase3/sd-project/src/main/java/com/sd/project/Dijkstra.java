package graphservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Dijkstra{

    private List<Vertice> vertices;
    private List<Aresta> arestas;
    private Set<Vertice> verticesMarcados;
    private Set<Vertice> verticesNaoMarcados;
    private HashMap<Vertice, Vertice> antecessores;
    private HashMap<Vertice, Double> distancia;

    public Dijkstra(Grafo grafo) {
        
        this.vertices = new ArrayList<Vertice>(grafo.getVertices());
        this.arestas = new ArrayList<Aresta>(grafo.getArestas());
    }

    public void executa(Vertice inicial) {
        
    	verticesMarcados = new HashSet<Vertice>();
        verticesNaoMarcados = new HashSet<Vertice>();
        distancia = new HashMap<Vertice, Double>();
        antecessores = new HashMap<Vertice, Vertice>();
        
        distancia.put(inicial, 0.0);
        
        verticesNaoMarcados.add(inicial);
        
        while (verticesNaoMarcados.size() > 0) {
            
            Vertice nodo = getMinimo(verticesNaoMarcados);
            verticesMarcados.add(nodo);
            verticesNaoMarcados.remove(nodo);
            buscaDistanciasMinimas(nodo);
        
        }
    }

    private void buscaDistanciasMinimas(Vertice nodo) {
        
    	List<Vertice> nodosAdjacentes = getVizinhos(nodo);
        
    	for (Vertice alvo : nodosAdjacentes) {
            
    		if (getMenorDistancia(alvo) > getMenorDistancia(nodo)
                    + getDistancia(nodo, alvo)) {
                distancia.put(alvo, getMenorDistancia(nodo)
                        + getDistancia(nodo, alvo));
                antecessores.put(alvo, nodo);
                verticesNaoMarcados.add(alvo);
            }
        }

    }

    private double getDistancia(Vertice nodo, Vertice alvo) {
        
    	for (Aresta aresta: arestas) {
    	 
  	    //direcionada
            if (aresta.isDirecionado() == true) {

		if(aresta.getPessoa1() == nodo.getId()
                    && aresta.getPessoa2() == alvo.getId())
                    return aresta.getDistancia();
            }
	    else { //não direcionada

	   	if(aresta.getPessoa1() == nodo.getId()
                    && aresta.getPessoa2() == alvo.getId())
		    return aresta.getDistancia();
		
		if(aresta.getPessoa2() == nodo.getId()
        	    && aresta.getPessoa1() == alvo.getId())	
		    return aresta.getDistancia();
	    }
        }
        throw new RuntimeException("Nao deveria acontecer");
    }

    private List<Vertice> getVizinhos(Vertice nodo) {
    	
        List<Vertice> vizinhos = new ArrayList<Vertice>();
        //aqui tbmmm
        for (Aresta aresta: arestas) {
            
	    if (aresta.isDirecionado() == true) {

		if(aresta.getPessoa1() == nodo.getId()
            	    && !verificaMarcado(findVertice(aresta.getPessoa2())))
        	    vizinhos.add(findVertice(aresta.getPessoa2()));
     	    }
	    else {

	    	if(aresta.getPessoa1() == nodo.getId()
            	    && !verificaMarcado(findVertice(aresta.getPessoa2()))) { 

		    vizinhos.add(findVertice(aresta.getPessoa2()));
	    	}
	    	if(aresta.getPessoa2() == nodo.getId()
            	    && !verificaMarcado(findVertice(aresta.getPessoa1()))) { 

		    vizinhos.add(findVertice(aresta.getPessoa1()));
	    	}
	    }   
	}    	
        return vizinhos;
    }

    private Vertice getMinimo(Set<Vertice> vertices) {
        
    	Vertice minimo = null;
        
    	for (Vertice v : vertices) {
            
	    if (minimo == null) {
            	minimo = v;
            } else {
                if (getMenorDistancia(v) < getMenorDistancia(minimo)) {
                    minimo = v;
                }
            }
        }
    	
        return minimo;
    }

    private boolean verificaMarcado(Vertice vertice) {
        return verticesMarcados.contains(vertice);
    }

    private double getMenorDistancia(Vertice destino) {
        
    	Double d = distancia.get(destino);
        
    	if (d == null) {
            return Double.MAX_VALUE;
        } 
    	else {
            return d;
        }
    	
    }

    /*
     * Esse método retorna o caminho do vertice inicial até o destino e
     * NULL caso não exista caminho
     */
    public LinkedList<Vertice> getCaminho(Vertice alvo) { 
        
    	LinkedList<Vertice> caminho = new LinkedList<Vertice>();
        Vertice atual = alvo;
        
        // verifica se um caminho existe
        if (antecessores.get(atual) == null) {
            return null;
        }
        
        caminho.add(atual);
        
        while (antecessores.get(atual) != null) {
            atual = antecessores.get(atual);
            caminho.add(atual);
        }
        
        //Coloca na ordem correta
        Collections.reverse(caminho);
        return caminho;
    }

    private Vertice findVertice(int vertice) {
        
        for(Vertice v: this.vertices){
            if(v.id == vertice){
                return v;
            }
        }
        return null;
        
    }

}

