package fourmi.territoire;

import fourmiliere.Fourmiliere;

public class Territoire {

	public static void main(String[] args) 
	{
		Fourmiliere fourmiliere = new Fourmiliere();

		while(fourmiliere.estMorte == false)
		{
			fourmiliere.evoluer();
		}

	}


}
