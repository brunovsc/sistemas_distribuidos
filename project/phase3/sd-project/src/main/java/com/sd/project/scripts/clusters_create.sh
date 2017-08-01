#!/bin/bash

cp ../../../../../../../target/sd-project-1.0-SNAPSHOT.jar ..

cd .. # returning to project's root directory
#rm logs/* # cleaning log files

numberClusters=$1
eachCluster=$2

echo "Initializing "$numberClusters" clusters with "$eachCluster" in each cluster" > logs/logClusters.txt

firstCluster=5000
id=0
numberReplicas=$((numberClusters * eachCluster))
echo "Total replicas "$numberReplicas > logs/logClusters.txt

while [ $id -lt $numberReplicas ]; do
	echo Running java CreateReplica $numberReplicas $id $firstCluster >> logs/logClusters.txt
	filename="logReplica"$id".txt"
	filePath=logs/$filename
	#java -cp ".:./jars/libthrift-0.10.0.jar:./jars/slf4j.jar:./jars/slf4j-simple-1.7.25.jar" graphservice.GraphServer $N $id $firstPort > $filePath &
	java -cp ".:./jars/libthrift-0.10.0.jar:./jars/slf4j.jar:./jars/slf4j-simple-1.7.25.jar:sd-project-1.0-SNAPSHOT.jar" graphservice.CreateReplica $numberReplicas $id $firstCluster > $filePath &
	((id++))
done

id=0
echo "Opened ports: "
echo "==== Opened ports: " >> logs/logClusters.txt
while [ $id -lt $numberReplicas ]; do
	port=$((firstCluster+id))
	echo "-----" $port
	echo "-----" $port >> logs/logClusters.txt
	((id++))
done 
