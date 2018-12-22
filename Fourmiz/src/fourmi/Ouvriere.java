package fourmi;

public class Ouvriere extends Fourmi 
{

	public Ouvriere(Metier metier)
	{
		super(metier);
	}

	@Override
	public float getProduction() 
	{ 			
		return alea(this.getMetier().valeurProduction,0.1f);
	}
}
