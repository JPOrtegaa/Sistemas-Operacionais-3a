package escalonadores;

import java.util.LinkedList;

public class EscalonadorSJF extends Thread{
	private LinkedList<Processo> listaProcessos; // lista com todos os processos inseridos!!
	private int totalProcessos;					 // total de processos!!
	private LinkedList<Processo> listaCPU;		 // lista dos processos que a CPU reconhece em determinado periodo de tempo!!
	private int totalCPU;						 // total de processos que a CPU reconhece em determinado momento!!
	
	EscalonadorSJF(LinkedList<Processo> newLista, int totalProcessos){
		this.listaProcessos = newLista;
		this.totalProcessos = totalProcessos;
		this.listaCPU = new LinkedList<>();
		this.totalCPU = 0;
	}
	
	private void sortBurstTime(int processosCompletados) {
		Processo aux;
		for(int i = totalCPU-1; i > processosCompletados; i--) {
			for(int j = processosCompletados; j < i; j++) {
				if(listaCPU.get(j).getBurstTime() > listaCPU.get(j+1).getBurstTime()) {
					aux = listaCPU.get(j);
					listaCPU.set(j, listaCPU.get(j+1));
					listaCPU.set(j+1, aux);
				}
			}
		}
	}
	
	private boolean repetidoCPU(Processo x) {
		for(int i = 0; i < totalCPU; i++) 
			if(listaCPU.get(i).getIdProcesso() == x.getIdProcesso())
				return true;
		return false;
	}
	
	private void insereListaCPU(int tempo) {
		Processo x;
		for(int i = 0; i < this.totalProcessos; i++) {
			x = listaProcessos.get(i);
			if(x.getArrivalTime() <= tempo && !repetidoCPU(x)) {
				listaCPU.add(x);
				totalCPU++;
			}
		}
	}
	
	private boolean processosFinalizadosCPU() { // checa se todos os processos da CPU estao finalizados!!
		for(int i = 0; i < totalCPU; i++)
			if(listaCPU.get(i).getFinishTime() == 0)
				return false;
		return true;
	}
	
	public void run() {
		int tempo, processosCompletados;
		tempo = processosCompletados = 0;
		System.out.println("Iniciando Escalonador!");
		while(processosCompletados != this.totalProcessos) {
			insereListaCPU(tempo);
			if(!listaCPU.isEmpty() && !processosFinalizadosCPU()) { // se a listaCPU estiver nao estiver vazia e tiver processo para executar!!
				sortBurstTime(processosCompletados);
				listaCPU.get(processosCompletados).setTempoAtual(tempo);
				listaCPU.get(processosCompletados).start();
				try {
					sleep(listaCPU.get(processosCompletados).getBurstTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tempo += listaCPU.get(processosCompletados).getBurstTime();
				processosCompletados++;
			}
			else // caso contrario atualiza o tempo!!
				tempo++;
		}
		System.out.println("Finalizando Escalonador!!");
	}
	
	public LinkedList<Processo> getListaCPU(){
		return listaCPU;
	}
}