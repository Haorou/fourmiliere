package fourmi;


public class Guerriere extends Fourmi 
{	
	public Guerriere(Metier metier)
	{
		super(Metier.GUERRIERE);
	}

	@Override
	public float getProduction() { return alea(this.getMetier().valeurProduction,0.3f);}

}
