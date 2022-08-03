import java.util.*;

public class Buffer {
	private ArrayList<Integer> buffer;	// estrutura utilizada para o buffer
	private int tamanhoTotal;			// capacidade total do buffer

	// Buffer cria uma instancia de ArrayList e recebe o tamanho total do buffer
	public Buffer(int tamanhoTotal){
		buffer = new ArrayList<Integer>();
		this.tamanhoTotal = tamanhoTotal;
	}

	// imprime na tela os elementos presentes no buffer
	public void printBuffer() {
		int tamanho = buffer.size();
		System.out.print("Buffer: ");
		for(int i = 0; i < tamanho; i++) {
			if(i != tamanho-1)
				System.out.print(buffer.get(i) + " - ");
			else
				System.out.print(buffer.get(i));
		}
		System.out.println();
	}

	/* 
	 * metodo sincronizado com remover, garantindo exclusao mutua.
	 * insere um item no buffer, caso esteja cheio a thread Produtor espera por um notify
	 */
	public synchronized void inserir(Integer item) {
		if(buffer.size() == tamanhoTotal) {
			System.out.println("Buffer cheio!!");		
			printBuffer();
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		buffer.add(item);
		printBuffer();
		if(buffer.size() == 1)
			notify();
	}

	/*
	 * metodo sincronizado com inserir, garantindo exclusao mutua.
	 * remove um item do buffer, caso esteja vazio a thread Consumidor espera por um notify
	 */
	public synchronized void remover() {
		Integer item;
		if(buffer.size() == 0) {
			System.out.println("Buffer vazio!!");
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		item = buffer.get(0);
		System.out.println("Consumindo item: " + item);
		buffer.remove(item);
		printBuffer();
		if(buffer.size() == tamanhoTotal-1)
			notify();
	}

	// main com caso de teste envolvendo um Produtor e um Consumidor
	public static void main(String[] args) {
		Buffer buffer = new Buffer(10);
		Produtor produtor = new Produtor(buffer);
		Consumidor consumidor = new Consumidor(buffer);

		Thread p1 = new Thread(produtor);   // thread produtor1
		Thread c1 = new Thread(consumidor); // thread consumidor1

		p1.start();
		c1.start();
	}

}
