package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda extends Observable implements Linda {

	Collection<Tuple> tuples;
	Map<Tuple, LinkedList<Integer>> MatchEnAttente;
	Lock moniteur;
	Condition[] classe;
	Condition writeCondition;
	int id;
	private Boolean takeEffectue; // utilisé pour le réveil en chaîne des match
									// en attente dans la méthode write et pour la méthode "update" de nos
	// observers

	public CentralizedLinda() {
		super();
		tuples = new LinkedList<Tuple>();
		MatchEnAttente = new HashMap<Tuple, LinkedList<Integer>>();
		moniteur = new ReentrantLock();
		classe = new Condition[200];
		writeCondition = moniteur.newCondition();
		id = 0;
	}

	// DEBUT GETTERS & SETTERS

	public Boolean getTakeEffectue() {
		return takeEffectue;
	}

	public void setTakeEffectue(Boolean takeEffectue) {
		this.takeEffectue = takeEffectue;
	}

	public Collection<Tuple> getTuples() {
		return tuples;
	}

	public void setTuples(Collection<Tuple> tuples) {
		this.tuples = tuples;
	}

	public Map<Tuple, LinkedList<Integer>> getMatchEnAttente() {
		return MatchEnAttente;
	}

	public void setMatchEnAttente(Map<Tuple, LinkedList<Integer>> matchEnAttente) {
		MatchEnAttente = matchEnAttente;
	}

	public Lock getMoniteur() {
		return moniteur;
	}

	public void setMoniteur(Lock moniteur) {
		this.moniteur = moniteur;
	}

	public Condition[] getClasse() {
		return classe;
	}

	public void setClasse(Condition[] classe) {
		this.classe = classe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// FIN GETTERS & SETTERS

	public void write(Tuple t) {
		Collection<Tuple> templatesCorrespondants;
		// TODO Auto-generated method stub
		moniteur.lock();
		this.setTakeEffectue(false); //tant que la variable est "false", cela indique qu'on n'a pas réveillé un Take
		tuples.add(t);
		templatesCorrespondants=recupererTemplate(t); // Pour savoir s'il y a qqn à reveiller il ne se passe rien
		while(!templatesCorrespondants.isEmpty() && !takeEffectue) {
			this.reveil(templatesCorrespondants);
			//on attend que le réveil ait été effectué jusqu'à un take ou jusqu'à la fin des read
			try {
				this.writeCondition.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			templatesCorrespondants=recupererTemplate(t);
		}
		//si on n'a pas effectué déjà un take de ce tuple, on regarde s'il y a des callback enregistrés sur ce tuple
		if (!takeEffectue) {
			this.setChanged();
			this.notifyObservers(t);
		}
		
		//on réinitialise la variable "takeEffectue"
		this.setTakeEffectue(false);
		moniteur.unlock();
	}

	public Tuple take(Tuple template) {
		// TODO Auto-generated method stub
		moniteur.lock();
		Tuple t = this.tryTake(template);
		int nb;
		if (t == null) {
			nb = id;
			this.id++;
			try {
				MatchEnAttente.get(template).add(nb);
			} catch (Exception e) {
				LinkedList<Integer> listes = new LinkedList<Integer>();
				listes.add(nb);
				MatchEnAttente.put(template, listes);
			}
			classe[nb] = moniteur.newCondition();
			try {
				classe[nb].await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("Take N�"+nb+" reveill�");
	
			// on indique pour les eventRegister que le template a été pris
			// (take)
			t = this.tryTake(template);
			MatchEnAttente.get(template).removeFirst();
			if (MatchEnAttente.get(template).size() == 0) {
				MatchEnAttente.remove(template);
			}
			// on indique au write qu'il peut continuer son exécution en indiquant qu'un take a été effectué
			this.setTakeEffectue(true);;
			this.writeCondition.signal();
		}
		moniteur.unlock();
		return t;
	}

	public Tuple read(Tuple template) {
		// TODO Auto-generated method stub
		moniteur.lock();
		Tuple t = this.tryRead(template);
		int nb;
		if (t == null) {
			nb = id;
			this.id++;
			try {
				MatchEnAttente.get(template).add(nb);
			} catch (Exception e) {
				LinkedList<Integer> listes = new LinkedList<Integer>();
				listes.add(nb);
				MatchEnAttente.put(template, listes);
			}
			classe[nb] = moniteur.newCondition();
			try {
				classe[nb].await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("Read N�"+nb+" reveill�");
			t = this.tryRead(template);
			// System.out.println("Avant :"+MatchEnAttente.get(template));
			MatchEnAttente.get(template).removeFirst();
			// System.out.println("Apres :"+MatchEnAttente.get(template));
			if (MatchEnAttente.get(template).size() == 0) {
				MatchEnAttente.remove(template);
			}
			// on indique au write qu'il peut continuer son exécution
			this.writeCondition.signal();
		}
		moniteur.unlock();
		return t;
	}

	public Tuple tryTake(Tuple template) {
		// TODO Auto-generated method stub
		Tuple resultat = null;
		Iterator<Tuple> it = tuples.iterator();
		while (it.hasNext() && resultat == null) {
			Tuple t = it.next();
			if (t.matches(template)) {
				resultat = t;
				tuples.remove(t);
			}
		}
		return resultat;
	}

	public Tuple tryRead(Tuple template) {
		// TODO Auto-generated method stub
		Tuple resultat = null;
		Iterator<Tuple> it = tuples.iterator();
		while (it.hasNext() && resultat == null) {
			Tuple t = it.next();
			if (t.matches(template)) {
				resultat = t;
			}
		}
		return resultat;
	}

	public Collection<Tuple> takeAll(Tuple template) {
		// TODO Auto-generated method stub
		Collection<Tuple> ts = new ArrayList<Tuple>();
		for (Tuple t : tuples) {
			if (t.matches(template)) {
				ts.add(t);
				tuples.remove(t);
			}
		}
		return ts;
	}

	public Collection<Tuple> readAll(Tuple template) {
		// TODO Auto-generated method stub
		Collection<Tuple> ts = new ArrayList<Tuple>();
		for (Tuple t : tuples) {
			if (t.matches(template)) {
				ts.add(t);
			}
		}
		return ts;
	}

	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		// TODO Auto-generated method stub
		Tuple tuple = null;
		Observer obs;

		// dans le cas o� l'action � effectuer est IMMEDIATE
		if (timing == eventTiming.IMMEDIATE) {
			if (mode == eventMode.READ) {
				tuple = this.tryRead(template);
			} else {
				tuple = this.tryTake(template);
			}
			// si on a trouv� un tuple immediatement, on effectue l'action
			if (tuple != null) {
				callback.call(tuple);
				// sinon on attend avec un observer
			} else {
				obs = new MyObserver(mode, template, callback);
				this.addObserver(obs);
				// System.out.println("obs added");
			}
			// dans le cas o� l'action � effectuer est FUTURE, on ajoute
			// simplement l'observer
		} else {
			obs = new MyObserver(mode, template, callback);
			this.addObserver(obs);
		}
	}

	public void debug(String prefix) {
		// TODO Auto-generated method stub

	}

	private Collection<Tuple> recupererTemplate(Tuple t) {
		Collection<Tuple> templates = new HashSet<Tuple>();
		if (t == null) {
			System.out.println("probleme recupererTemplate");
		}
		if (MatchEnAttente.keySet().isEmpty()) {
			// Rien a faire
		} else {
			for (Tuple template : MatchEnAttente.keySet()) {
				if (t.matches(template)) {
					templates.add(template);
				}
			}
		}
		return templates;
	}

	private void reveil(Collection<Tuple> templates) {
		TreeSet<Integer> ensemble = new TreeSet<Integer>();
		for (Tuple t : templates) {
			ensemble.addAll(MatchEnAttente.get(t));
		}
		if (ensemble.isEmpty()) {
			// Personne en attente, on continue notre write
			this.writeCondition.signal();
		} else {
			// System.out.println("Reveil de : "+ensemble.first());
			classe[ensemble.first()].signal();
		}
	}

	public void afficherTuples() {
		moniteur.lock();
		for (Tuple t : tuples) {
			System.out.println(t.toString());
		}
		moniteur.unlock();
	}

	/*
	 * private Collection<Tuple> trouve(Tuple t) { Collection<Tuple> templates =
	 * MatchEnAttente.keySet(); for (Tuple template : templates) { if
	 * (!t.matches(template)) { templates.remove(template); } } return
	 * templates; }
	 * 
	 * private void reveil(Tuple t) { int nb = MatchEnAttente.get(t).getFirst();
	 * classe[nb].signal(); }
	 */

}
