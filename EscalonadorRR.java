package escalonadores;

import java.util.LinkedList;

public class EscalonadorRR extends Thread{
	private LinkedList<Processo> listaProcesso;
	private int totalProcesso;
	private LinkedList<Processo> listaCPU;
	private int totalCPU;
	private int quantum;

	EscalonadorRR(LinkedList<Processo> listaProcesso, int totalProcesso, int quantum){
		this.listaProcesso = listaProcesso;
		this.totalProcesso = totalProcesso;
		this.quantum = quantum;
		this.listaCPU = new LinkedList<>();
		this.totalCPU = 0;
	}

	private boolean repetidoCPU(Processo x) {
		for(int i = 0; i < totalCPU; i++) 
			if(x.getIdProcesso() == listaCPU.get(i).getIdProcesso())
				return true;
		return false;
	}

	private void inserirCPU(int tempo) {
		Processo x;
		for(int i = 0; i < totalProcesso; i++) {
			x = listaProcesso.get(i);
			if((x.getArrivalTime() <= tempo) && (!repetidoCPU(x)) && (x.getState() != Thread.State.TERMINATED)) {
				listaCPU.add(x);
				totalCPU++;
			}
		}
	}

	private void atualizaListaProcesso(Processo x) {
		for(int i = 0; i < totalProcesso; i++) 
			if(listaProcesso.get(i).getIdProcesso() == x.getIdProcesso())
				listaProcesso.set(i, x);
	}

	public void run() {
		int tempo, processosCompletados;
		Processo aux;
		int tempoProcessoExec;
		tempo = processosCompletados = 0;
		System.out.println("Iniciando Escalonador!!");
		while(processosCompletados != totalProcesso) {
			inserirCPU(tempo);
			if(!listaCPU.isEmpty()) {
				aux = listaCPU.poll();
				totalCPU--;
				aux.setTempoAtual(tempo);		
				tempoProcessoExec = aux.getTempoRestante();
				if(tempoProcessoExec <= quantum) { // processo sera finalizado!!
					aux.start();
					try {
						sleep(tempoProcessoExec);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tempo += tempoProcessoExec;
					processosCompletados++;
					atualizaListaProcesso(aux);
				}
				else {
					aux.start();
					try {
						sleep(quantum);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tempo += quantum;
					Processo copia = new Processo(aux.getIdProcesso(), aux.getArrivalTime(),
										 aux.getBurstTime(), aux.getTempoRestante(), quantum);
					copia.setEscalonador(aux.getEscalonador());
					inserirCPU(tempo); // antes de reinserir o processo na lista, analisar caso tenha algum novo que tenha chegado!!
					listaCPU.add(copia);
					totalCPU++;
				}
			}
			else
				tempo++;
		}
		System.out.println("Finalizando Escalonador!!");
	}
	
	public LinkedList<Processo> getListaProcesso() {
		return this.listaProcesso;
	}

}