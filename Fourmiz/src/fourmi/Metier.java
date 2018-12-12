package fourmi;

public enum Metier 
{
	CHEF(100000),GUERRIERE(1),CHASSEUSE(1),NETTOYEUSE(20),NURSE(10),INGENIEUR(100);
	
	int valeurProduction;
	
	private Metier(int production)
	{
		this.valeurProduction = production;
	}
}
