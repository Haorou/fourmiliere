package fourmi;


public class Guerriere extends Fourmi 
{	
	public Guerriere(Metier metier)
	{
		super(Metier.GUERRIERE);
	}
	
	public void PetitGuerrier()
	{
		System.out.println("Je ne suis pas bien grand");
	}
	
	public void superGuerrier()
	{
		System.out.println("Je ne suis pas bien grand");
	}

	@Override
	public float getProduction() { return alea(this.getMetier().valeurProduction,0.3f);}

}
