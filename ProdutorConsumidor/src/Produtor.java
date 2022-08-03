import java.util.*;
import java.lang.Thread;

public class Produtor implements Runnable{
	private Random rand;   // atributo rand para gerar numeros aleatorios
	private Buffer buffer; // Produtor tem acesso ao buffer de memoria compartilhado

	// Produtor cria uma instancia de random e recebe um buffer
	public Produtor(Buffer buffer) {
		rand = new Random(50);
		this.buffer = buffer;
	}
	
	// retorna um numero aleatorio entre 1 e 50
	private int geraItem() {
		return rand.nextInt(50)+1;
	}
	
	// metodo para fazer com que a thread durma dado os milissegundos passados
	private void dormir(int mili) {
		try {
			Thread.sleep(mili);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// metodo run sobrescrito, Produtor produz itens e tenta coloca-los no buffer compartilhado
	public void run() {
		Integer item;
		while(true) {
			item = geraItem();
			System.out.println("Produzindo item: " + item);
			buffer.inserir(item);
			dormir(1000);
		}
	}
}
