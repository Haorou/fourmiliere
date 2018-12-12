package fourmi;


public class Guerriere extends Fourmi 
{
	private static final int BESSOIN_NOURRITURE = 1;
	
	public Guerriere(Metier metier)
	{
		super(Metier.GUERRIERE);
	}

	@Override
	public float getProduction() { return alea(this.getMetier().valeurProduction,0.3f);}

}
