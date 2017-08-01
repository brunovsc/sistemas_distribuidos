package graphservice;

import static java.lang.Thread.sleep;
import java.util.Arrays;
import java.util.Collection;
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

public class CreateReplica {

    private static int totalReplicas;
    private static int selfId;
    private static int firstPort;
    private static int selfPort;
    private static final int RETRIES = 5;
    private static final int WAIT_RETRY_CONNECTION = 2000;
    
    public static void main(String [] args){

        totalReplicas = Integer.parseInt(args[0]);
        selfId = Integer.parseInt(args[1]);
        firstPort = Integer.parseInt(args[2]);
        selfPort = firstPort + selfId;
        boolean firstOnCluster = (selfId % 3 == 0);
        
   		System.out.println("===== Creating copycat server on port " + selfPort);
   		
   		Address address = new Address("localhost", selfPort);
		CopycatServer.Builder builder = CopycatServer.builder(address); 
		builder.withStateMachine(GraphStateMachine::new);
		builder.withTransport(NettyTransport.builder().withThreads(1).build());
		builder.withStorage(Storage.builder().withDirectory(new File("logs/Copycat/logsCopycat"+selfId)).withStorageLevel(StorageLevel.DISK).build());
	
		CopycatServer copycatServer = builder.build();
		copycatServer.serializer().register(CreateVertice.class);
		copycatServer.serializer().register(ReadVertice.class);
		copycatServer.serializer().register(UpdateVertice.class);
		copycatServer.serializer().register(DeleteVertice.class);
		copycatServer.serializer().register(DeleteArestasFromVertice.class);
		
		copycatServer.serializer().register(CreateAresta.class);
		copycatServer.serializer().register(ReadAresta.class);
		copycatServer.serializer().register(UpdateAresta.class);
		copycatServer.serializer().register(DeleteAresta.class);
		
		copycatServer.serializer().register(GetClusterGraph.class);
		copycatServer.serializer().register(GetClusterVertices.class);
		copycatServer.serializer().register(GetClusterArestas.class);
		
   		if(firstOnCluster){ // create a new cluster
   			System.out.println("===== Creating new cluster on port " + selfPort);
			CompletableFuture<CopycatServer> future = copycatServer.bootstrap();
   			System.out.println("===== Bootstrapping new cluster on port " + selfPort);
			future.join();
   		} else { // join existing cluster
   			try{
   				sleep(1000);
   			} catch(Exception e){}
   			int clusterPort = firstPort + ((selfId / 3) * 3);
   			System.out.println("===== !!! Joining cluster on port  " + clusterPort);
   			Collection<Address> cluster = Arrays.asList(new Address("localhost", clusterPort));
			copycatServer.join(cluster).join();
   		}

   		System.out.println("===== Created  copycat server on port " + selfPort);
   	}
}
       		
