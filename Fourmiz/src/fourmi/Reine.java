package fourmi;

import java.util.ArrayList;
import java.util.List;

public class Reine extends Fourmi {
	
	private int capacitePondaison;
	private List<LotOeufs> l_lot_oeufs = new ArrayList<LotOeufs>();
	
	public Reine(int capacitePondaison)
	{
		super();
		this.capacitePondaison = capacitePondaison;
		this.besoinEnNourriture = 50;
	}
	
	public LotOeufs getLotOeufs(int index) { return l_lot_oeufs.get(index); }
	public LotOeufs removeLotOeufs(int index) { return l_lot_oeufs.remove(index); }
	public int nbrLotsOeufs() { return l_lot_oeufs.size();}
	
	public float getBesoinEnNourriture()
	{
		return alea(besoinEnNourriture,0.2f);
	}
	
	public void pondre()
	{
		l_lot_oeufs.add(new LotOeufs(capacitePondaison));
	}
	
	
}
