package com.sd.project;

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

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
		
		Storage storage = Storage.builder().withDirectory(new File("logs")).withStorageLevel(StorageLevel.DISK).build();
		Transport transport = NettyTransport.builder().build();
		
		AtomixReplica replica = AtomixReplica.builder(new Address("localhost", 8700)).withStorage(storage).withTransport(transport).build();
		CompletableFuture<AtomixReplica> future = replica.bootstrap();
		future.join();
		
		
		DistributedLock lock = replica.getLock("my-lock").join();
		lock.lock().thenRun(() -> System.out.println("Acquired a lock!"));
		
		AtomixReplica replica2 = AtomixReplica.builder(new Address("localhost", 8701)).withStorage(storage).withTransport(transport).build();
		replica2.join(new Address("localhost", 8700)).join();
		/*
		AtomixReplica replica3 = AtomixReplica.builder(new Address("localhost", 8702)).withStorage(storage).withTransport(transport).build();
		replica3.join(new Address("localhost", 8700), new Address("localhost", 8701)).join();
		
		*/
		
		DistributedLock lock2 = replica2.getLock("my-lock2").join();
		lock2.lock().thenRun(() -> System.out.println("Acquired a lock 2!"));
		
		/*
		DistributedLock lock3 = replica3.getLock("my-lock3").join();
		lock3.lock().thenRun(() -> System.out.println("Acquired a lock 3!"));*/
		
    }
}
