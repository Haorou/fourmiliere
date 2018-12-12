package fourmi;

import java.util.Date;

public class Ouvriere extends Fourmi 
{
	private int production;
	private int puissanceNettoyage = 1;
	private int puissanceChasse = 1;

	public Ouvriere()
	{
		super();
		// TODO Auto-generated constructor stub
		this.estOuvriere = true;
	}
	
	int puissanceNbre()
	{
		return 0;
	}

	public int getPuissanceChasse()
	{
		return alea(puissanceChasse,0.1f);
	}
	
	public int getPuissanceNettoyage()
	{
		if (this.getAge() > 2)
			puissanceNettoyage = 20;
		
		return alea(puissanceNettoyage,0.1f);
	}
}
