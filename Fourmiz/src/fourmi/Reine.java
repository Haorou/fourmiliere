package fourmi;

import java.util.ArrayList;
import java.util.List;

public class Reine extends Fourmi {
	
	private List<LotOeufs> l_lot_oeufs = new ArrayList<LotOeufs>();
	
	public Reine()
	{
		super(Metier.CHEF, 50);
	}
	
	public LotOeufs getLotOeufs(int index) { return l_lot_oeufs.get(index); }
	public LotOeufs removeLotOeufs(int index) { return l_lot_oeufs.remove(index); }
	public int nbrLotsOeufs() { return l_lot_oeufs.size();}

	public void pondre()
	{
		l_lot_oeufs.add(new LotOeufs(this.getMetier().valeurProduction));
	}

	@Override
	public float getProduction() 
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
