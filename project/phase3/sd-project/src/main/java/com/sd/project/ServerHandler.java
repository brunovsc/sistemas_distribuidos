package graphservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.thrift.TException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Thread.sleep;
import java.util.LinkedList;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import io.atomix.copycat.server.storage.StorageLevel;

import io.atomix.*;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.Address;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import io.atomix.concurrent.DistributedLock;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.catalyst.transport.local.LocalTransport;
import io.atomix.catalyst.transport.local.LocalServerRegistry;

public class ServerHandler implements Graph.Iface{
    
    private static CopycatClient []copycatClients;
    private final Grafo grafo = new Grafo();
    private static final long sleepTime = 1000;
    private static final int maxRetries = 10;
    private static final long waitToProcess = 5000; // test of concurrency
    private static final boolean testConcurrency = false; // test of concurrency
    
    // Multiple servers
    private TTransport []transports;
    private TProtocol []protocols;
    private Graph.Client []clients;
    private static int ports[]; // array of ports for others servers
    private int selfPort; // number of the port of this server
    private static int N; // number of servers
    private int selfId;
    
    private List<Integer> blockedVertices = new ArrayList<Integer>();

    public ServerHandler(String []args){

        grafo.vertices = new ArrayList<Vertice>();
        grafo.arestas = new ArrayList<Aresta>(); 
        
        N = Integer.parseInt(args[0]);
        selfId = Integer.parseInt(args[1]);
        int firstPort = Integer.parseInt(args[2]);
        selfPort = firstPort + selfId;
        ports = new int[N];
        for(int i = 0; i < N; i++){
            ports[i] = firstPort + i;
        } 
        
        transports = new TTransport[N];
        protocols = new TProtocol[N];
        clients = new Graph.Client[N];
        
        copycatClients = new CopycatClient[3];

		connectToClusters();
    }    
    
    public void connectToServerId(int id, int port) throws Exception{
        try{
            transports[id] = new TSocket("localhost", port);
            transports[id].open();
            protocols[id] = new TBinaryProtocol(transports[id]);
            clients[id] = new Graph.Client(protocols[id]);
            System.out.println("Server " + selfPort + " connected to server " + port);
        }catch(TTransportException e){
            throw e;
        }
    }
    
    public void connectToClusters(){
    
		CopycatClient.Builder builderCluster1 = CopycatClient.builder();
		builderCluster1.withTransport(NettyTransport.builder().withThreads(1).build());
		copycatClients[0] = builderCluster1.build();
		copycatClients[0].serializer().register(CreateVertice.class);
		copycatClients[0].serializer().register(ReadVertice.class);
		copycatClients[0].serializer().register(UpdateVertice.class);
		copycatClients[0].serializer().register(DeleteVertice.class);
		copycatClients[0].serializer().register(DeleteArestasFromVertice.class);
		copycatClients[0].serializer().register(CreateAresta.class);
		copycatClients[0].serializer().register(ReadAresta.class);
		copycatClients[0].serializer().register(UpdateAresta.class);
		copycatClients[0].serializer().register(DeleteAresta.class);
		copycatClients[0].serializer().register(GetClusterGraph.class);
		copycatClients[0].serializer().register(GetClusterVertices.class);
		copycatClients[0].serializer().register(GetClusterArestas.class);
		
   		System.out.println("===== Connecting Server Handler to Cluster 1");
		Collection<Address> cluster1 = Arrays.asList(
			new Address("localhost", 5000), new Address("localhost", 5001), new Address("localhost", 5002));
		try{
  			CompletableFuture<CopycatClient> future = copycatClients[0].connect(cluster1);
			future.join();
		} catch (Exception e){
			System.out.println("!!!! Failed to connect Server Handler to Cluster 1 ");
		}
		
		CopycatClient.Builder builderCluster2 = CopycatClient.builder();
		builderCluster2.withTransport(NettyTransport.builder().withThreads(1).build());
		copycatClients[1] = builderCluster2.build();
		copycatClients[1].serializer().register(CreateVertice.class);
		copycatClients[1].serializer().register(ReadVertice.class);
		copycatClients[1].serializer().register(UpdateVertice.class);
		copycatClients[1].serializer().register(DeleteVertice.class);
		copycatClients[1].serializer().register(DeleteArestasFromVertice.class);
		copycatClients[1].serializer().register(CreateAresta.class);
		copycatClients[1].serializer().register(ReadAresta.class);
		copycatClients[1].serializer().register(UpdateAresta.class);
		copycatClients[1].serializer().register(DeleteAresta.class);
		copycatClients[1].serializer().register(GetClusterGraph.class);
		copycatClients[1].serializer().register(GetClusterVertices.class);
		copycatClients[1].serializer().register(GetClusterArestas.class);
		
   		System.out.println("===== Connecting Server Handler to Cluster 2");
		Collection<Address> cluster2 = Arrays.asList(
			new Address("localhost", 5003), new Address("localhost", 5004), new Address("localhost", 5005));
		try{
  			CompletableFuture<CopycatClient> future = copycatClients[1].connect(cluster2);
			future.join();
		} catch (Exception e){
			System.out.println("!!!! Failed to connect Server Handler to Cluster 2 ");
		}
		
		CopycatClient.Builder builderCluster3 = CopycatClient.builder();
		builderCluster3.withTransport(NettyTransport.builder().withThreads(1).build());
		copycatClients[2] = builderCluster3.build();
		copycatClients[2].serializer().register(CreateVertice.class);
		copycatClients[2].serializer().register(ReadVertice.class);
		copycatClients[2].serializer().register(UpdateVertice.class);
		copycatClients[2].serializer().register(DeleteVertice.class);
		copycatClients[2].serializer().register(DeleteArestasFromVertice.class);
		copycatClients[2].serializer().register(CreateAresta.class);
		copycatClients[2].serializer().register(ReadAresta.class);
		copycatClients[2].serializer().register(UpdateAresta.class);
		copycatClients[2].serializer().register(DeleteAresta.class);
		copycatClients[2].serializer().register(GetClusterGraph.class);
		copycatClients[2].serializer().register(GetClusterVertices.class);
		copycatClients[2].serializer().register(GetClusterArestas.class);
		
   		System.out.println("===== Connecting Server Handler to Cluster 3");
		Collection<Address> cluster3 = Arrays.asList(
			new Address("localhost", 5006), new Address("localhost", 5007), new Address("localhost", 5008));
		try{
  			CompletableFuture<CopycatClient> future = copycatClients[2].connect(cluster3);
			future.join();
		} catch (Exception e){
			System.out.println("!!!! Failed to connect Server Handler to Cluster 3 ");
		}
    }
    
    public int processRequest(int vertice){
        try{
            int server = MD5.md5(String.format("%d", vertice), String.format("%d", 3));  
            System.out.println("Cluster to process: " + server);
            return server;
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;        
    }
    
    public boolean isBlockedVertice(int nome){
        Integer vertice = nome;
        return blockedVertices.contains(vertice);
    }
    
    public void blockVertice(int nome){
        if(testConcurrency){
            System.out.println("!!! Blocked vertice " + nome);
        }
        blockedVertices.add(nome);
    }
    
    public void unblockVertice(int nome){
        if(testConcurrency){
            System.out.println("!!! Unblocked vertice " + nome);
        }
        blockedVertices.remove(new Integer(nome));
    }
    
    public void unblockAresta(int v1, int v2){
        unblockVertice(v1);
        unblockVertice(v2);
    }
    
    public void verifyResourceVertice(int vertice) throws ResourceInUse{
        int retries = 0;
        while(isBlockedVertice(vertice) && retries < maxRetries){
            if(testConcurrency){
                System.out.println("Resource " + vertice + " being used");
            }
            waitResource();
            retries++;
        }
        if(isBlockedVertice(vertice)){
            throw new ResourceInUse(vertice);
        }
        blockVertice(vertice);
        if(testConcurrency){ // test of concurrency
            try {            
                sleep(waitToProcess);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
        
    public void verifyResourceAresta(int pessoa1, int pessoa2) throws ResourceInUse{
        int retries = 0;
        while(isBlockedVertice(pessoa1) && retries < maxRetries){
            if(testConcurrency){
                System.out.println("Resource " + pessoa1 + " being used");
            }
            waitResource();
            retries++;
        }
        if(isBlockedVertice(pessoa1)){
            throw new ResourceInUse(pessoa1);
        }
        blockVertice(pessoa1);
        retries = 0;
        while(isBlockedVertice(pessoa2) && retries < maxRetries){
            if(testConcurrency){
                System.out.println("Resource " + pessoa2 + " being used");
            }
            waitResource();
            retries++;
        }
        if(isBlockedVertice(pessoa2)){
            unblockVertice(pessoa1);
            throw new ResourceInUse(pessoa2);
        }
        blockVertice(pessoa2);        
        if(testConcurrency){ // test of concurrency
            try {            
                sleep(waitToProcess);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void waitResource(){
        try {
            sleep(sleepTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Vertice findVertice(int vertice){
    	int server = processRequest(vertice);
        CompletableFuture<Object> future = copycatClients[server].submit(new ReadVertice(vertice));
		Object result = future.join();
        Vertice v = (Vertice)result;
        return v;
    }
    
    public Aresta findAresta(int pessoa1, int pessoa2){
    	int server = processRequest(pessoa1);
        CompletableFuture<Object> future = copycatClients[server].submit(new ReadAresta(pessoa1, pessoa2));
		Object result = future.join();
        Aresta a = (Aresta)result;
        return a;
    }
    
    @Override
    public boolean createVertice(int id, String nome, int idade, String cidade_atual, String contato) throws KeyAlreadyUsed, ResourceInUse, TException {
        logForOperation(1);
        verifyResourceVertice(id);
        
    	int server = processRequest(id);
    	CopycatClient client = copycatClients[server];
        Vertice vertice = findVertice(id);
        
    	if(vertice != null){
    	    unblockVertice(id);
            throw new KeyAlreadyUsed(id, "Vertice ja existente");  
    	}
    	
        CompletableFuture<Object> future2 = client.submit(new CreateVertice(id, nome, idade, cidade_atual, contato));
		Object result2 = future2.join();
		boolean b = (boolean)result2;
		
  		unblockVertice(id);
  		return b;
    }

    @Override
    public boolean deleteVertice(int key) throws KeyNotFound, ResourceInUse, TException {
        logForOperation(2);        
        verifyResourceVertice(key);
    	int server = processRequest(key);
    	CopycatClient client = copycatClients[server];
        
        Vertice vertice = findVertice(key);
        if(vertice == null){
    	    unblockVertice(key);
            throw new KeyNotFound(key, "Vertice nao encontrado");
    	}
    	
    	for(int i = 0; i < 3; i++) { 
			CompletableFuture<Object> future1 = copycatClients[i].submit(new DeleteArestasFromVertice(key));
			Object result1 = future1.join();
			boolean p = (boolean)result1;
            if(p){
                System.out.println("Deleted arestas at cluster " + i);
            }
        }

        CompletableFuture<Object> future2 = client.submit(new DeleteVertice(key));
		Object result2 = future2.join();
		boolean b = (boolean)result2;

        unblockVertice(key);
        return b;
    }

	@Override
    public Vertice readVertice(int key) throws KeyNotFound, ResourceInUse, TException {
        logForOperation(4);
    	verifyResourceVertice(key);
    	int server = processRequest(key);
    	CopycatClient client = copycatClients[server];
    
        Vertice vertice = findVertice(key);
		
        if(vertice == null){
            unblockVertice(key);
            throw new KeyNotFound(key, "Vertice nao encontrado");  
        }
        
        unblockVertice(key);
        return vertice;
    }

    @Override
    public boolean updateVertice(int id, String nome, int idade, String cidade_atual, String contato) throws KeyNotFound, ResourceInUse, TException {
        logForOperation(3);
        verifyResourceVertice(id);
    	int server = processRequest(id);
    	CopycatClient client = copycatClients[server];
    	
        Vertice vertice = findVertice(id);
        
    	if(vertice == null){
            unblockVertice(id);
            throw new KeyNotFound(id, "Vertice nao encontrado");  
        }
  		
        CompletableFuture<Object> future2 = client.submit(new UpdateVertice(id, nome, idade, cidade_atual, contato));
		Object result2 = future2.join();
        boolean b = (boolean)result2;
  		unblockVertice(id);
  		return b;
    }

    @Override
    public boolean createAresta(int pessoa1, int pessoa2, double distancia, boolean direcionado, String descricao) throws KeyAlreadyUsed, ResourceInUse, KeyNotFound, BadParameter, TException {
        logForOperation(5);
    	if(pessoa1 == pessoa2){
    		throw new BadParameter("Arestas with format (k, k) are not allowed.");
    	}
        verifyResourceAresta(pessoa1, pessoa2);
        
        Vertice v1 = findVertice(pessoa1);
        if(v1 == null){
        	unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(pessoa1, "Vertice nao encontrado");
    	}
    	Vertice v2 = findVertice(pessoa2);
    	if(v2 == null){
        	unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(pessoa2, "Vertice nao encontrado");
    	}
    	
    	int server = processRequest(pessoa1);
        CopycatClient client = copycatClients[server];
        
        // Restriction: restrição de criar aresta se ambos os vértices já existirem no grafo
        
        Aresta aresta = findAresta(pessoa1, pessoa2);
        
        if(aresta == null){
        	
		    CompletableFuture<Object> future2 = client.submit(new CreateAresta(pessoa1, pessoa2, distancia, direcionado, descricao));
			Object result2 = future2.join();
			boolean b = (boolean)result2;
            unblockAresta(pessoa1, pessoa2);
            return b;
        }
        // Aresta já existe, tratar bidirecionalidade
        if(!aresta.direcionado){ // aresta já é bidirecional
            unblockAresta(pessoa1, pessoa2);
            throw new KeyAlreadyUsed(0, "Aresta ja existente");
        }
        if(aresta.pessoa1 == pessoa1 && aresta.pessoa2 == pessoa2){ // direcionada de v1 pra v2 ja exist
            unblockAresta(pessoa1, pessoa2);
            throw new KeyAlreadyUsed(0, "Aresta direcionada ja existente do vertice 1 para o vertice 2");            
        }
        else{ // direcionada de v2 pra v1
            unblockAresta(pessoa1, pessoa2);
            throw new KeyAlreadyUsed(0, "Aresta direcionada ja existente do vertice 2 para o vertice 1");     
        }
        //return true;
    }

    @Override
    public boolean deleteAresta(int pessoa1, int pessoa2) throws KeyNotFound, ResourceInUse, BadParameter, TException {
    	
        logForOperation(6);
        if(pessoa1 == pessoa2){
    		throw new BadParameter("Arestas with format (k, k) are not allowed.");
    	}
        verifyResourceAresta(pessoa1, pessoa2);      
        int server = processRequest(pessoa1);
    	CopycatClient client = copycatClients[server];  
        
        Aresta aresta = findAresta(pessoa1, pessoa2);        
        if(aresta == null){
            unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(0, "Aresta nao encontrada");
        }
        
        CompletableFuture<Object> future = client.submit(new DeleteAresta(pessoa1, pessoa2));
        Object result = future.join();
        boolean b = (boolean)result;


        unblockAresta(pessoa1, pessoa2);
        return b;        
    }

    @Override
    public Aresta readAresta(int pessoa1, int pessoa2) throws KeyNotFound, ResourceInUse, BadParameter, TException {
        logForOperation(8);
    	if(pessoa1 == pessoa2){
    		throw new BadParameter("Arestas with format (k, k) are not allowed.");
    	}
        verifyResourceAresta(pessoa1, pessoa2);
    	int server = processRequest(pessoa1);
    	CopycatClient client = copycatClients[server];
    
        CompletableFuture<Object> future = client.submit(new ReadAresta(pessoa1, pessoa2));
		Object result = future.join();
        Aresta aresta = (Aresta)result;
		
		if(aresta == null){
            unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(0, "Aresta nao encontrada");            
        }
        unblockAresta(pessoa1, pessoa2);
        return aresta;
    }

    @Override
    public boolean updateAresta(int pessoa1, int pessoa2, double distancia, boolean direcionado, String descricao) throws KeyNotFound, ResourceInUse, BadParameter, TException {
    
        logForOperation(7);
        
    	if(pessoa1 == pessoa2){
    		throw new BadParameter("Arestas with format (k, k) are not allowed.");
    	}
        verifyResourceAresta(pessoa1, pessoa2);
        int server = processRequest(pessoa1);

        
    	CopycatClient client = copycatClients[server];
    
        Aresta aresta = findAresta(pessoa1, pessoa2);
		
		if(aresta == null){
            unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(0, "Aresta nao encontrada");            
        }
        
        CompletableFuture<Object> future = client.submit(new UpdateAresta(pessoa1, pessoa2, distancia, direcionado,descricao));
        Object result = future.join();
        boolean b = (boolean)result;
		unblockAresta(pessoa1, pessoa2);
        return b;        
    }
    
    @Override
    public List<Vertice> listVerticesFromAresta(int pessoa1, int pessoa2) throws KeyNotFound, ResourceInUse, BadParameter, TException {

    	if(pessoa1 == pessoa2){
    		throw new BadParameter("Arestas with format (k, k) are not allowed.");
    	}
        verifyResourceAresta(pessoa1, pessoa2);
        logForOperation(9);

        Grafo fullGraph = getFullGraph();
        Aresta aresta = null;
        for(Aresta arest: fullGraph.arestas){
        	if(((arest.pessoa1 == pessoa1) && (arest.pessoa2 == pessoa2)) || ((arest.pessoa1 == pessoa2) && (arest.pessoa2 == pessoa1))){
        		aresta = arest;
        		break;
        	}
        }
        if(aresta == null){
            unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(0, "Aresta nao encontrada");
        }
        Vertice v1 = findVertice(pessoa1);
        if(v1 == null){
            unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(pessoa1, "Vertice nao encontrado");            
        }
        Vertice v2 = findVertice(pessoa2);
        if(v2 == null){
            unblockAresta(pessoa1, pessoa2);
            throw new KeyNotFound(pessoa2, "Vertice nao encontrado");            
        }
        
        List<Vertice> vertices = new ArrayList<Vertice>();
        vertices.add(v1);
        vertices.add(v2);
        unblockAresta(pessoa1, pessoa2);
        return vertices;
    }

    @Override
    public List<Aresta> listArestasFromVertice(int id) throws KeyNotFound, ResourceInUse, TException {
    
        verifyResourceVertice(id);
        logForOperation(10);
        Grafo fullGraph = getFullGraph();
        
        Vertice v = null;
        for(Vertice vert: fullGraph.vertices){
        	if(vert.id == id){
        		v = vert;
        		break;
        	}
        }
        if(v == null){
            unblockVertice(id);
            throw new KeyNotFound(id, "Vertice nao encontrado");
        }
        List<Aresta> arestas = new ArrayList<Aresta>();
        for(Aresta aresta: fullGraph.arestas){
            if(aresta.pessoa1 == id || aresta.pessoa2 == id){
                arestas.add(aresta);
            }
        }
        unblockVertice(id);
        return arestas;
    }

    @Override
    public List<Vertice> listNeighbors(int id) throws KeyNotFound, ResourceInUse, TException {
    
        verifyResourceVertice(id);
        logForOperation(11);
        Grafo fullGraph = getFullGraph();
        
        Vertice v = null;
        for(Vertice vert: fullGraph.vertices){
        	if(vert.id == id){
        		v = vert;
        		break;
        	}
        }
        if(v == null){
            unblockVertice(id);
            throw new KeyNotFound(id, "Vertice nao encontrado");
        }
        
        List<Integer> neighbors = new ArrayList<Integer>();
        for(Aresta aresta: fullGraph.arestas){
            if(aresta.pessoa1 == id){
                neighbors.add(aresta.pessoa2);
            }            
            else{
                if(aresta.pessoa2 == id && !aresta.direcionado){
                    neighbors.add(aresta.pessoa1);
                }
            }
        }
        List<Vertice> vertices = new ArrayList<Vertice>();
        for(Vertice vertice: fullGraph.vertices){
            if(neighbors.contains(vertice.id)){
                vertices.add(vertice);
            }
        }
        unblockVertice(id);
        return vertices;
    }

    @Override
    public List<Vertice> listVertices(){

        logForOperation(12);
        ArrayList<Vertice> vertices = new ArrayList<Vertice>();
        for(int i = 0; i < 3; i++){
		    CompletableFuture<Object> future = copycatClients[i].submit(new GetClusterVertices());
		    Object result = future.join();
		    ArrayList<Vertice> aux = (ArrayList<Vertice>)result;
		    vertices.addAll(aux);
        }
        showVertices(vertices);
        return vertices;
    }
    
    @Override
    public List<Vertice> listSelfVertices(){
    /*
        logForOperation(14);
        if(grafo.isSetVertices()){
            return grafo.vertices;
        }*/
        return null;
    }
    
    @Override
    public List<Aresta> listArestas(){
        logForOperation(13);
        ArrayList<Aresta> arestas = new ArrayList<Aresta>();
        for(int i = 0; i < 3; i++){
		    CompletableFuture<Object> future = copycatClients[i].submit(new GetClusterArestas());
		    Object result = future.join();
		    ArrayList<Aresta> aux = (ArrayList<Aresta>)result;
		    arestas.addAll(aux);
        }
        showArestas(arestas);
        return arestas;
    }

    @Override
    public List<Aresta> listSelfArestas() throws TException {
      /*  logForOperation(15);
        if(grafo.isSetArestas()){
            return grafo.arestas;
        }*/
        return null;
    }

    @Override
    public List<Vertice> menorCaminho(int pessoa1, int pessoa2) throws KeyNotFound, ResourceInUse, BadParameter, TException {
    
    	if(pessoa1 == pessoa2){
    		throw new BadParameter("Cannot calculate path from k to k.");
    	}
        logForOperation(16);
        Grafo fullGraph = getFullGraph();
        
        Vertice v = findVertice(pessoa1);
		if(v == null){
			throw new KeyNotFound(pessoa1, "Vertice nao encontrado");
		}
        Vertice destino = findVertice(pessoa2);
		if(destino == null){
			throw new KeyNotFound(pessoa2, "Vertice nao encontrado");
		}

        Dijkstra algoritmo = new Dijkstra(fullGraph);

        algoritmo.executa(v); 

        LinkedList<Vertice> caminho = algoritmo.getCaminho(destino);
        return caminho;
    }
    
    
    public static void showGrafo(List<Vertice> vertices, List<Aresta> arestas){
        System.out.println("\n      GRAFO: ");
        showVertices(vertices);
        System.out.println("");
        showArestas(arestas);
    }

    public static void showVertices(List<Vertice> vertices){
        System.out.print("      - Vertices: ");
        for(Vertice vertice: vertices){
            System.out.print(vertice.nome + ", ");
        }
    }

    public static void showArestas(List<Aresta> arestas){
        System.out.print("      - Arestas: ");
        String a;
        for(Aresta aresta: arestas){
            if(!aresta.direcionado){
                a = "[" + aresta.pessoa1 + ", " + aresta.pessoa2 + "], ";   
            }
            else{
                a = "(" + aresta.pessoa1 + ", " + aresta.pessoa2 + "), ";                     
            }
            System.out.print(a);
        }
    }
    
    public Grafo getFullGraph(){
        Grafo g = new Grafo();
        g.vertices = new ArrayList<Vertice>();
        g.arestas = new ArrayList<Aresta>();
        for(int i = 0; i < 3; i++){
		    CompletableFuture<Object> future = copycatClients[i].submit(new GetClusterGraph());
		    Object result = future.join();
		    Grafo aux = (Grafo)result;
            g.vertices.addAll(aux.vertices);
            g.arestas.addAll(aux.arestas);
        }
        showGrafo(g.vertices, g.arestas);
        return g;
    }

    @Override
    public boolean deleteArestasFromVertice(int key) throws KeyNotFound, ResourceInUse, TException {
    /*
        verifyResourceVertice(key);
        logForOperation(17);
        System.out.print("Removing local arestas of vertice " + key);
		ArrayList<Aresta> arestasToRemove = new ArrayList<Aresta>();
        for(Aresta a: grafo.arestas){
            if(a.pessoa2 == key){
                arestasToRemove.add(a);
            }
        }
		grafo.arestas.removeAll(arestasToRemove);
		unblockVertice(key);*/
        return true;
    }
    
    public void logForOperation(int operation){
        System.out.println("----- OPERATION on port " + selfPort + " - " + textForOperation(operation));
        
    }    
    
    public void logForwardedRequest(int server, int operation){
        System.out.println("----- FORWARDED request (" + textForOperation(operation) + ")  from server " + selfPort + " to server " + ports[server]);
    }
    
    public String textForOperation(int operation){
        switch(operation){
            case 0:
                return "";
            case 1:
                return "Create Vertice";
            case 2:
                return "Delete Vertice";
            case 3:
                return "Update Vertice";
            case 4:
                return "Read Vertice";
            case 5:
                return "Create Aresta";
            case 6:
                return "Delete Aresta";
            case 7:
                return "Update Aresta";
            case 8:
                return "Read Aresta";
            case 9:
                return "List Vertices Aresta";
            case 10:
                return "List Arestas Vertice";
            case 11:
                return "List Neighbors";
            case 12:
                return "List Vertices";
            case 13:
                return "List Arestas";
            case 14:
                return "List Self Vertices";
            case 15:
                return "List Self Arestas";
            case 16:
                return "Menor Caminho";
            case 17:
                return "Delete Arestas From Vertice";
            default:
                return "Invalid operation";
        }
    }
}
