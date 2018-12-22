package fourmi;

public interface IInsecte 
{

	float getProduction();

	Metier getMetier();
	void setMetier(Metier metier);

	int getAge();

	int getBesoinEnNourriture();

	void vieillir();

	boolean mourirAuHasard();

}
