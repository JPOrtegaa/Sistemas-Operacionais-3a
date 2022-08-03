package escalonadores;

import java.util.LinkedList;

public class EscalonadorPrioridade extends Thread{
	private LinkedList<Processo> listaProcesso; 			   // lista com todos os processos inseridos!!
	private int totalProcesso;								   // total de processos!!
	private LinkedList<LinkedList<Processo>> listasPrioridade; // listas baseadas na prioridade de cada processo!!
	private int[] arrayPrioridade;							   // array contendo as prioridades diferentes!!
	private int totalPrioridade;							   // total de prioridades diferentes!!
	private int quantum;									   // limite de tempo que um processo pode utilizar a CPU!!


	EscalonadorPrioridade(LinkedList<Processo> listaProcesso, int totalProcesso, int totalPrioridade, int quantum){
		this.listaProcesso = listaProcesso;
		this.totalProcesso = totalProcesso;
		this.listasPrioridade = new LinkedList<LinkedList<Processo>>();
		this.arrayPrioridade = new int[totalPrioridade];
		this.totalPrioridade = totalPrioridade;
		this.quantum = quantum;
	}

	private boolean repetidoArrayPrioridade(int prioridade, int index) {
		for(int i = 0; i < index; i++)
			if(prioridade == arrayPrioridade[i])
				return true;
		return false;
	}

	private void inserirArrayPrioridade() {
		int index = 0;
		for(int i = 0; i < totalProcesso; i++) 
			if(!repetidoArrayPrioridade(listaProcesso.get(i).getPrioridade(), index))
				arrayPrioridade[index++] = listaProcesso.get(i).getPrioridade();
	}
	
	private void sortArrayPrioridade() {
		int aux;
		for(int i = arrayPrioridade.length-1; i > 0; i--) 
			for(int j = 0; j < i; j++) 
				if(arrayPrioridade[j] < arrayPrioridade[j+1]) {
					aux = arrayPrioridade[j];
					arrayPrioridade[j] = arrayPrioridade[j+1];
					arrayPrioridade[j+1] = aux;
				}
	}

	private void criarListasPrioridade() {
		LinkedList<Processo> aux;
		for(int i = 0; i < totalPrioridade; i++) {
			aux = new LinkedList<Processo>();
			listasPrioridade.add(aux);
		}
	}

	private boolean repetidoListaPrioridade(LinkedList<Processo> listaPrioridade, Processo x) {
		for(int i = 0; i < listaPrioridade.size(); i++)
			if(x.getIdProcesso() == listaPrioridade.get(i).getIdProcesso())
				return true;
		return false;
	}

	private void inserirListasPrioridade(int tempo) {
		Processo x;
		for(int i = 0; i < totalPrioridade; i++) {
			for(int j = 0; j < totalProcesso; j++) {
				x = listaProcesso.get(j);
				if((x.getPrioridade() == arrayPrioridade[i]) && (x.getArrivalTime() <= tempo) && 
						(!repetidoListaPrioridade(listasPrioridade.get(i), x)) && (x.getState() != Thread.State.TERMINATED))
					listasPrioridade.get(i).add(x);
			}
		}
	}

	private boolean listasVazias() {
		for(int i = 0; i < totalPrioridade; i++) {
			if(!listasPrioridade.get(i).isEmpty())
				return false;
		}
		return true;
	}
	
	private void atualizaListaProcesso(Processo x) {
		for(int i = 0; i < totalProcesso; i++)
			if(x.getIdProcesso() == listaProcesso.get(i).getIdProcesso())
				listaProcesso.set(i, x);
	}

	public void run() {
		int tempo, processosFinalizados, tempoRestante;
		Processo aux;
		tempo = processosFinalizados = 0;
		System.out.println("Iniciando Escalonador!!");
		inserirArrayPrioridade();
		sortArrayPrioridade(); // sort do mais importante ao menos (quanto maior o numero maior a prioridade)!
		criarListasPrioridade();
		while(processosFinalizados != totalProcesso) {
			inserirListasPrioridade(tempo);
			if(!listasVazias()) { // caso alguma das listas nao esteja vazia execute o processo de maior prioridade no momento!!
				for(int i = 0; i < totalPrioridade; i++) { // procura-se uma lista que nao esteja vazia da maior para a menor prioridade!!
					if(!listasPrioridade.get(i).isEmpty()) {
						aux = listasPrioridade.get(i).poll();
						aux.setTempoAtual(tempo);
						tempoRestante = aux.getTempoRestante();
						if(tempoRestante <= quantum) {
							aux.start();
							try {
								sleep(tempoRestante);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							tempo += tempoRestante;
							processosFinalizados++;
							atualizaListaProcesso(aux);
							break;
						}
						else {
							aux.start();
							try {
								sleep(quantum);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							tempo += quantum;
							Processo copia = new Processo(aux.getIdProcesso(), aux.getArrivalTime(), aux.getBurstTime(), 
														  aux.getTempoRestante(), quantum, aux.getPrioridade());
							copia.setEscalonador(aux.getEscalonador());
							inserirListasPrioridade(tempo);
							listasPrioridade.get(i).add(copia);
							break;
						}
					}
				}
			}
			else // caso todas as listas estejam vazias atualize o tempo!!
				tempo++;
		}
		System.out.println("Finalizando Escalonador!!");
	}
	
	public LinkedList<Processo> getListaProcesso(){
		return this.listaProcesso;
	}
	
}