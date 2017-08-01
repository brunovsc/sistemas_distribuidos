package graphservice;

import static java.lang.System.exit;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TBinaryProtocol;

//import view.TelaMenu;
//import view.*;

public class GraphClient {
    
    private static TTransport transport;
    private static TProtocol protocol;
    private static Graph.Client client;
    
    public static void main(String [] args) {
        try {
			int port;
			try{
            	port = Integer.parseInt(args[0]);
			} catch(Exception e){
				System.out.println("No port specified... finishing client");
				return;
			}
            transport = new TSocket("localhost", port);
            transport.open();

            protocol = new TBinaryProtocol(transport);
            client = new Graph.Client(protocol);
            System.out.print("\nClient connected to port " + port);
        } catch (TException x){

            x.printStackTrace();
        }
        if(client != null){


	try{
		int test = Integer.parseInt(args[1]);
		try{
			client.createVertice(1, "a", 1, "cidade_a", "1");
			client.createVertice(2, "b", 2, "cidade_b", "2");
			client.createVertice(3, "c", 3, "cidade_c", "3");
			client.createVertice(4, "d", 4, "cidade_d", "4");
			client.createVertice(5, "e", 5, "cidade_e", "5");
			client.createVertice(6, "f", 6, "cidade_f", "6");
			client.createVertice(7, "g", 7, "cidade_g", "7");
			client.createVertice(8, "h", 8, "cidade_h", "8");
			client.createVertice(9, "i", 9, "cidade_i", "9");

			client.createAresta(1, 2, 12, false, "12");
			client.createAresta(1, 3, 13, false, "13");
			client.createAresta(2, 3, 23, false, "23");
			client.createAresta(2, 5, 25, false, "25");
			client.createAresta(2, 6, 26, false, "26");
			client.createAresta(2, 9, 29, false, "29");
			client.createAresta(4, 1, 41, false, "41");
			client.createAresta(4, 3, 43, false, "43");
			client.createAresta(4, 8, 48, false, "48");
			client.createAresta(5, 1, 51, false, "51");
			client.createAresta(5, 7, 57, false, "57");
			client.createAresta(6, 4, 64, false, "64");
			client.createAresta(6, 5, 65, false, "65");
			client.createAresta(7, 8, 78, false, "78");
			client.createAresta(8, 3, 83, false, "83");
			client.createAresta(8, 9, 89, false, "89");
			client.createAresta(9, 1, 91, false, "91");
		}
		catch(Exception e){ System.out.println(e.toString()); }
	}catch(Exception e){}

	    //new TelaLogin(client);
            int op = -1;
            while(op != 0){
                op = showMenu();
                try {
                    handleOperation(op);
                } catch (TException ex) {
                    System.out.println(ex);
                }
            }
            try {
                List<Vertice> vertices = client.listVertices();
                List<Aresta> arestas = client.listArestas();
                showGrafo(vertices, arestas);
            } catch (TException ex) {
                System.out.println(ex);
            }
            System.out.println("\nEncerrando cliente ... ");
            try {
                transport.close();
            } catch (Exception e){
            }
            exit(0);
        }
        else{
            System.out.println("\nNao foi possivel estabelecer a conexao");
            exit(0);
        }
    }

    public static void showGrafo(List<Vertice> vertices, List<Aresta> arestas) throws TException{
        System.out.println("\n      GRAFO: ");
        showVertices(vertices);
        System.out.println("");
        showArestas(arestas);
    }

    public static void showVertices(List<Vertice> vertices) throws TException{
        System.out.print("      - Vertices: ");
        for(Vertice vertice: vertices){
            System.out.print(vertice.nome + ", ");
        }
    }

    public static void showArestas(List<Aresta> arestas) throws TException{
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

    public static int showMenu(){
        System.out.println("");
        System.out.println("================================");
        System.out.println("|         1. Vertice           |");
        System.out.println("|         2. Aresta            |");
        System.out.println("|         3. Consulta          |");
        System.out.println("|         4. Listar            |");
        System.out.println("|         0. Sair              |");
        System.out.println("================================");            
        System.out.print("Operacao -> ");
        Scanner in = new Scanner(System.in);
        int op1 = in.nextInt();
        int op2 = 0;
        if(op1 != 0 && op1 < 5){
            op2 = showSubMenu(op1);
        }
        return op1 * 10 + op2;
    }

    public static int showSubMenu(int op1){
        System.out.println("");
        System.out.println("================================");
        switch(op1){
            case 1:
            case 2:
                System.out.println("|         1. Criar             |");
                System.out.println("|         2. Remover           |");
                System.out.println("|         3. Atualizar         |");
                System.out.println("|         4. Consultar         |");
                break;
            case 3:
                System.out.println("|   1. Listar vertices aresta  |");
                System.out.println("|   2. Listar arestas vertice  |");
                System.out.println("|   3. Listar vizinhos         |");
                System.out.println("|   4. Menor caminho           |");
                break;
            case 4:
                System.out.println("|   1. Listar vertices         |");
                System.out.println("|   2. Listar arestas          |");
                System.out.println("|   3. Listar vertices locais  |");
                System.out.println("|   4. Listar arestas locais   |");
                System.out.println("|   5. Exibir grafo            |");
                break;
            default:
                System.out.println("");
        }
        System.out.println("================================");            
        System.out.print("Operacao -> ");
        Scanner in = new Scanner(System.in);
        int op2 = in.nextInt();
        return op2;
    }


    public static void handleOperation(int op) throws TException{
        int id, idade, pessoa1, pessoa2;
	String nome, contato, cidade_atual, descricao;
        double distancia;
        boolean direcionado;
        Scanner in = new Scanner(System.in);
        boolean result;
        Vertice vertice;
        Aresta aresta;

        switch (op){
            case 0:
                break;
            // Vertice
            case 11:
                System.out.print("Id: ");
                id = in.nextInt();
                System.out.print("Nome: ");
                in.nextLine();
                nome = in.nextLine();
                System.out.print("Idade: ");
                idade = in.nextInt();
                System.out.print("Cidade atual: ");
                in.nextLine();
                cidade_atual = in.nextLine();
                System.out.print("Contato: ");
                contato = in.nextLine();

                if(client.createVertice(id, nome, idade, cidade_atual, contato)){
                    System.out.println("Vertice criado com sucesso");
                }
                break;
            case 12:
                System.out.print("Id: ");
                id = in.nextInt();

                if(client.deleteVertice(id)){
                    System.out.println("Vertice removido com sucesso");
                }
                break;
            case 13:
                System.out.print("Id: ");
                id = in.nextInt();
                System.out.print("Nome: ");
                in.nextLine();
                nome = in.nextLine();
                System.out.print("Idade: ");
                idade = in.nextInt();
                System.out.print("Cidade atual: ");
                in.nextLine();
                cidade_atual = in.nextLine();
                System.out.print("Contato: ");
                contato = in.nextLine();

                if(client.updateVertice(id, nome, idade, cidade_atual, contato)){
                    System.out.println("Vertice atualizado com sucesso");                        
                }
                break;
            case 14:
                System.out.print("Id: ");
                id = in.nextInt();

                Vertice v = client.readVertice(id);
                if(v != null){
                    printVertice(v);
                }
                break;
            // Aresta
            case 21:
                System.out.print("Pessoa 1: ");
                pessoa1 = in.nextInt();
                System.out.print("Pessoa 2: ");
                pessoa2 = in.nextInt();
                System.out.print("Distancia: ");
                distancia = in.nextDouble();
                System.out.println("Direcionado: ");
                in.nextLine();
                direcionado = in.nextBoolean();
                System.out.print("Descricao: ");
                in.nextLine();
                descricao = in.nextLine();

                if(client.createAresta(pessoa1, pessoa2, distancia, direcionado, descricao)){
                    System.out.println("Aresta criada com sucesso");                         
                }
                break;
            case 22:
                System.out.print("Pessoa 1: ");
                pessoa1 = in.nextInt();
                System.out.print("Pessoa 2: ");
                pessoa2 = in.nextInt();

                if(client.deleteAresta(pessoa1, pessoa2)){
                    System.out.println("Aresta removida com sucesso"); 
                }
                break;
            case 23:
                System.out.print("Pessoa 1: ");
                pessoa1 = in.nextInt();
                System.out.print("Pessoa 2: ");
                pessoa2 = in.nextInt();
                System.out.print("Distancia: ");
                distancia = in.nextDouble();
                System.out.println("Direcionado: ");
                in.nextLine();
                direcionado = in.nextBoolean();
                System.out.print("Descricao: ");
                in.nextLine();
                descricao = in.nextLine();

                if(client.createAresta(pessoa1, pessoa2, distancia, direcionado, descricao)){
                    System.out.println("Aresta atualizada com sucesso"); 
                }
                break;
            case 24:
                System.out.print("Pessoa 1: ");
                pessoa1 = in.nextInt();
                System.out.print("Pessoa 2: ");
                pessoa2 = in.nextInt();

                Aresta a = client.readAresta(pessoa1, pessoa2);
                if(a != null){
                    printAresta(a);
                }
                break;
            // Consulta
            case 31:
                System.out.print("Pessoa 1: ");
                pessoa1 = in.nextInt();
                System.out.print("Pessoa 2: ");
                pessoa2 = in.nextInt();

                List<Vertice> vertices = client.listVerticesFromAresta(pessoa1, pessoa2);
                if(vertices != null){
                    for(Vertice vert:vertices){
                        printVertice(vert);
                    }
                }
                break;
            case 32:
                System.out.print("Id: ");
                id = in.nextInt();

                List<Aresta> arestas = client.listArestasFromVertice(id);
                if(arestas != null){
                    for(Aresta ares:arestas){
                        printAresta(ares);
                    }
                }
                break;
            case 33:
                System.out.print("Id: ");
                id = in.nextInt();

                List<Vertice> vizinhos = client.listNeighbors(id);
                if(vizinhos != null){
                    for(Vertice vert:vizinhos){
                        printVertice(vert);
                    }
                }
                break;
            case 34:
                System.out.print("Pessoa 1: ");
                pessoa1 = in.nextInt();
                System.out.print("Pessoa 2: ");
                pessoa2 = in.nextInt();
                
                List<Vertice> caminho = client.menorCaminho(pessoa1, pessoa2);
                if(caminho != null){
                    System.out.print("Caminho: ");
                    for(Vertice vert: caminho){
                        System.out.print(" " + vert.nome);
                    }
                    System.out.println("");
                }
                else{
                    System.out.println("Caminho não encontrado");
                }
                break;
            case 41:
                vertices = client.listVertices();
                showVertices(vertices);
                break;
            case 42:
                arestas = client.listArestas();
                showArestas(arestas);
                break;
            case 43:
                vertices = client.listSelfVertices();
                showVertices(vertices);
                break;
            case 44:
                arestas = client.listSelfArestas();
                showArestas(arestas);
                break;
            case 45:
                vertices = client.listVertices();
                arestas = client.listArestas();
                showGrafo(vertices, arestas);
                break;
            default:
                System.out.println("\nOperacao desconhecida");
        }
    }

    public static void printVertice(Vertice v){
        System.out.println("");
        System.out.println("Id: " + v.id);
        System.out.println("Nome: " + v.nome);
        System.out.println("Idade: " + v.idade);
        System.out.println("Cidade Atual: " + v.cidade_atual);
        System.out.println("Contato: " + v.contato);
    }        

    public static void printAresta(Aresta a){
        System.out.println("");
        System.out.println("Pessoa 1: " + a.pessoa1);
        System.out.println("Pessoa 2: " + a.pessoa2);
        System.out.println("Distancia: " + a.distancia);
        System.out.println("Direcionado: " + a.direcionado);
        System.out.println("Descricao: " + a.descricao);
    }
}

