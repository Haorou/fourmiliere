package fourmi.territoire;

import fourmiliere.Fourmiliere;

public class Territoire {

	public static void main(String[] args) 
	{
		Fourmiliere fourmiliere = new Fourmiliere();
		int compteur = 0;
		while(fourmiliere.estMorte == false)
		{
			compteur++;
			System.out.println("==============================================================================");
			System.out.println("-------------------------------- TOUR " + compteur +" -------------------------------------- ");
			System.out.println("==============================================================================");
			fourmiliere.evoluer();
		}

	}


}
