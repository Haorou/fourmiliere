package fourmi;

public class LotOeufs 
{
	private static final int AGE_ECLOSION = 1;
	private int nbrOeufs;
	private int age = 0;
	
	
	public LotOeufs(int capacitePondaison)
	{
		nbrOeufs = capacitePondaison;
	}
	
	public int getNbrLarves() { return this.nbrOeufs; }
	public int getAge() { return this.age; }
	public void incubation() { this.age++; }
	
	public boolean eclore() 
	{ 
		boolean estEclot = false; 
		if(this.age >= AGE_ECLOSION)
		{
			estEclot = true;
		}
		return estEclot;
	}
}
