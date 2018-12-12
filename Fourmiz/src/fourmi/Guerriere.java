package fourmi;


public class Guerriere extends Fourmi 
{	
	public Guerriere(Metier metier)
	{
		super(Metier.GUERRIERE);
	}
	
	public void superGuerrier()
	{
	System.out.println("KAMEHAMEHA!!");
	}

	@Override
	public float getProduction() { return alea(this.getMetier().valeurProduction,0.3f);}

}
