
public class Consumidor implements Runnable{
	private Buffer buffer; // Consumidor tem acesso ao buffer de memoria compartilhado
	
	// Consumidor recebe um buffer
	public Consumidor(Buffer buffer) {
		this.buffer = buffer;
	}
	
	// metodo para fazer com que a thread durma dado os milissegundos passados
	private void dormir(int mili) {
		try {
			Thread.sleep(mili);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// metodo run sobrescrito, Consumidor remove o primeiro item do buffer de memoria
	public void run() {
		while(true) {
			buffer.remover();
			dormir(3000);
		}
	}
}
