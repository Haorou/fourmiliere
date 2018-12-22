package fourmi;


public abstract class Fourmi implements IInsecte
{

	private Metier metier;
	private int age;
	private int besoinEnNourriture = 1;
	
	public Fourmi(Metier metier)
	{
		age = 0;
		this.metier = metier;
	}
	
	public Fourmi(Metier metier, int besoinEnNourriture)
	{
		age = 0;
		this.besoinEnNourriture = besoinEnNourriture;
		this.metier = metier;
	}

	public abstract float getProduction();
	
	public void setAge(int age) { this.age = age; }
	public int getAge() { return this.age;} 
	public Metier getMetier() { return this.metier;} 
	public void setMetier(Metier metier) { this.metier = metier; }
	public int getBesoinEnNourriture() { return this.besoinEnNourriture;}
	public void setBesoinEnNourriture(int besoinEnNourriture) { this.besoinEnNourriture = besoinEnNourriture;}

	public void vieillir() { this.age++; }

	public int calculerLaProbabilite(int age)
	{
		int probMax = 250000;
		
		for (int j = 1; j < age; ++j) 
			probMax = probMax /2;
		
		return probMax;
	}
	
	public boolean mourirAuHasard()
	{
		boolean estMorte  = false;
		int probabiliteDeMourirAuMax = 0;
		if(this.metier != Metier.CHEF)
		{
			probabiliteDeMourirAuMax = calculerLaProbabilite(getAge());
			probabiliteDeMourirAuMax = probabiliteDeMourirAuMax < 1?1:probabiliteDeMourirAuMax;
			probabiliteDeMourirAuMax = (int)((float) Math.random() * probabiliteDeMourirAuMax);
			estMorte = probabiliteDeMourirAuMax == 1?true:false;				
		}

		return estMorte;
	}
	
	public float alea(int valeur_entrante, float pourcentage_alea)
	{
		int valeur_a_modifier = valeur_entrante;
		int harsard = (int) (Math.random() * (valeur_a_modifier * pourcentage_alea));
		if(Math.random() < 0.5f)
			valeur_a_modifier -= harsard;
		else
			valeur_a_modifier += harsard;		
		return valeur_a_modifier;
	}
}
