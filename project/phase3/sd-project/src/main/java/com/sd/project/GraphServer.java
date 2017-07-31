package graphservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static java.lang.Thread.sleep;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TTransportException;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import io.atomix.*;
import io.atomix.copycat.server.storage.StorageLevel;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.local.LocalTransport;
import io.atomix.catalyst.transport.local.LocalServerRegistry;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import io.atomix.concurrent.DistributedLock;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.client.CopycatClient;

public class GraphServer {
    
    private static Graph.Processor processor;
    private static ServerHandler handler;
    private static int N;
    private static int selfId;
    private static int firstPort;
    private static int selfPort;
    private static final int RETRIES = 5;
    private static final int WAIT_RETRY_CONNECTION = 1000;
    
    public static void main(String [] args){

        N = Integer.parseInt(args[0]);
        selfId = Integer.parseInt(args[1]);
        firstPort = Integer.parseInt(args[2]);
        selfPort = firstPort + selfId;
        int replicaFirstPort = 5000;
        int selfReplicaPort = replicaFirstPort + selfId;
        
   		System.out.println("===== Creating copycat server on port " + selfReplicaPort);
   		
   		Address address = new Address("localhost", 5000);
		CopycatServer.Builder builder = CopycatServer.builder(address); 
		builder.withStateMachine(GraphStateMachine::new);
		builder.withTransport(NettyTransport.builder().withThreads(1).build());
		builder.withStorage(Storage.builder().withDirectory(new File("logs")).withStorageLevel(StorageLevel.DISK).build());
	
		CopycatServer copycatServer = builder.build();
		copycatServer.serializer().register(CreateVertice.class);
		copycatServer.serializer().register(ReadVertice.class);
		
   		//if(selfId % 3 == 0){ // create a new cluster
   			System.out.println("===== Creating new cluster on port " + selfReplicaPort);
			CompletableFuture<CopycatServer> future = copycatServer.bootstrap();
			future.join();
   		/*} else { // join existing cluster
   			System.out.println("===== Joining cluster on port " + selfReplicaPort);
   			Collection<Address> cluster = Arrays.asList(new Address("localhost", replicaFirstPort));
			copycatServer.join(cluster).join();
   		}*/

		
    
   		System.out.println("===== Created  copycat server on port 5000 ");
       		
        try {
               
        	System.out.println("===== Starting the server on port " + selfPort);
            TServerTransport serverTransport = new TServerSocket(selfPort);
            handler = new ServerHandler(args);
            processor = new Graph.Processor(handler);

            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            new Thread(new Runnable() {
                @Override
                public void run() {
                   connectServers();
                }
            }).start();
            server.serve();
        } catch (Exception x){
            System.out.println(x);
        }

    }
    
    public static void connectServers(){
        System.out.println("\n##### Creating connections to other servers #####\n");
        boolean connected;
        int retried;
        for(int i = 0; i < N; i++){
            if(i != selfId){
                connected = false;
                retried = 0;
                while(!connected && retried < RETRIES){
                    try{
                        System.out.println("Trying to connect to port " + (firstPort+i));
                        handler.connectToServerId(i, firstPort+i);
                        connected = true;
                    }catch(Exception e){
                        try {
                            System.out.println("Couldn't connect to server " + (firstPort+i) + " ... retrying in " + (WAIT_RETRY_CONNECTION / 1000) + " seconds");
                            sleep(WAIT_RETRY_CONNECTION);
                            retried++;
                        } catch (InterruptedException ex) {
                            System.out.println(ex);
                        }
                    }
                    if(!connected){
                        System.out.println("Unable to connect server on port " + (firstPort+i) + ". Finishing server...");
                    }
                }
            }
        }   
        System.out.println("\n##### Finished creating connections to other servers #####\n");
    }
}
