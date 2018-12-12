package fourmiliere;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fourmi.Fourmi;
import fourmi.Guerriere;
import fourmi.LotOeufs;
import fourmi.Ouvriere;
import fourmi.Reine;

public class Fourmiliere 
{
	private int dateCourante = 0;
	private static int capacite_max_nourriture = 1000;
	private static int capacite_max_larves = 1000;
	private static int capacite_max_population = 1000;
	
	private static int nombre_de_menaces = 0;
	private static int menaces_incrementables = 0;
	private static float popularite = 0.1f;
	
	public static boolean estMorte = false;
	private int nourritures_courantes = 500;

	private Reine reine;
	private List<Fourmi> l_fourmis = new ArrayList<Fourmi>();
	private int nombre_de_larves_age_0 = 0;
	private int nombre_de_larves_age_1 = 0;

	
	public Fourmiliere()
	{
		// INITIALISATION DE LA FOURMILIERE //
		reine = new Reine(100000);
		Ouvriere fourmi_ouvriere;
		for (int i = 0; i < 200; i++) 
		{
			fourmi_ouvriere = new Ouvriere();
			if(i > 100)
				fourmi_ouvriere.setAge(11);				
			else if(i < 50)
				fourmi_ouvriere.setAge(7);
			else
				fourmi_ouvriere.setAge(1);

			l_fourmis.add(fourmi_ouvriere);
		}
	}
	
	public void evoluer()
	{
		dateCourante++;
		gestionPondaisonEtNaissanceLarve();
		statistiques("gestionPondaisonEtNaissanceLarve");	
		
		gestionLarveEtRepartitionOuvrirGuerriere();
		statistiques("gestionLarveEtRepartitionOuvrirGuerriere");	
		
		gestionOuvriereProductionNettoyage();
		statistiques("gestionOuvriereProductionNettoyage");		
		
		gestionMenace();
		statistiques("gestionMenace");	
		
		gestionAlimentation();
		statistiques("gestionAlimentation");
		
		gestionAugmentationMenace();
		statistiques("gestionAugmentationMenace");	
		
		gestionAge();
		statistiques("gestionAge");
	}
	
	private void gestionPondaisonEtNaissanceLarve()
	{
		reine.pondre();
		LotOeufs lotOeufsCourant; 

		// GESTION ECLOSION DES OEUFS ET NAISSANCE \ VIEILLISSEMENT  DES LARVES //
		nombre_de_larves_age_1 = nombre_de_larves_age_0;
		nombre_de_larves_age_0 = 0;
		
		for (int index = 0; index < reine.nbrLotsOeufs(); index++) 
		{
			lotOeufsCourant = reine.getLotOeufs(index);
			if(lotOeufsCourant.eclore())
			{
				nombre_de_larves_age_0 += reine.removeLotOeufs(0).getNbrLarves();
				index--;
			}
			else
				lotOeufsCourant.incubation();				
		}
		
		// GESTION DES LARVES EN TROP [PLUS DE LARVES QUE DE CAPACITE] //
		int nbrDeLarvesEnTrop = (nombre_de_larves_age_0 + nombre_de_larves_age_1) - capacite_max_larves;
		if(nbrDeLarvesEnTrop > 0)
		{
			nombre_de_larves_age_0 -= nbrDeLarvesEnTrop;
			nourritures_courantes += alea(nbrDeLarvesEnTrop,0.25f);
		}

		
		if(nombre_de_larves_age_0 < 0)
		{
			nombre_de_larves_age_1 += nombre_de_larves_age_0;
			nombre_de_larves_age_0 = 0;
		}

		if(nombre_de_larves_age_1 < 0)
			nombre_de_larves_age_1 = 0;
		
	}
	
	private void gestionLarveEtRepartitionOuvrirGuerriere()
	{
		// LES LARVES EN AGE DE TRANSFORMATION DEVIENNENT GUERRIERE OU OUVRIERE//
		for (int index = 0; index < nombre_de_larves_age_1; index++) 
		{
			if(index%2==0)
				l_fourmis.add(new Ouvriere());

			else
				l_fourmis.add(new Guerriere());				
		}
		nombre_de_larves_age_1 = 0;

		// ON RANGE LES FOURMIS PAR AGE - PLUS JEUNE A PLUS AGEE //
		Comparator<Fourmi> byAge = Comparator.comparing(Fourmi::getAge);
		l_fourmis.sort(byAge);
		
		// SI IL Y A PLUS DE FOURMI QUE DE CAPACITE DE POPULATION ON SE DEBARASSE DES JEUNES EN TROP //
		if(l_fourmis.size() > capacite_max_population)
		{
			for (int index = capacite_max_population; index < l_fourmis.size(); index++) 
				l_fourmis.remove(0);
		}
	}
	
	private void gestionOuvriereProductionNettoyage()
	{
		Fourmi fourmiCourante;
		Ouvriere ouvriereCourante;
		int ageOuvriereCourante;
		
		// REPARTITION DES OUVRIERES EN FONCTION DE L'AGE //
		List<Fourmi> l_ouvrieres_age2max = new ArrayList<Fourmi>();
		List<Fourmi> l_ouvrieres_age2a10 = new ArrayList<Fourmi>();
		List<Fourmi> l_ouvrieres_11etPlus = new ArrayList<Fourmi>();
		
		for (int index = 0; index < l_fourmis.size(); index++) 
		{
			fourmiCourante = l_fourmis.get(index);
			
			if(fourmiCourante.estOuvriere)
			{
				ouvriereCourante = (Ouvriere) fourmiCourante;
				ageOuvriereCourante = ouvriereCourante.getAge();
				
				if(ageOuvriereCourante <= 2)
					l_ouvrieres_age2max.add(ouvriereCourante);
				else if(ageOuvriereCourante > 2 && ageOuvriereCourante <= 10)
					l_ouvrieres_age2a10.add(ouvriereCourante);
				else
					l_ouvrieres_11etPlus.add(ouvriereCourante);
			}
		}
		
		float capaciteDeNettoyageNecessaire = l_fourmis.size();
		
		
		float nombreDeLarvesGereableSuffisante = 0;
		
		// ATTRIBUTION DES ROLES //
		// OUVRIERE D'AGE 2 OU MOINS : GESTION DES LARVES //
		float repartitionGererLarve = 0;
		float reparttionGererAgrandissement = 0;
		
		int nbrDeLarvesGereables = (int)(l_ouvrieres_age2max.size() *0.9f) * 10;
		
		if(nombre_de_larves_age_0 > nbrDeLarvesGereables)
			nombre_de_larves_age_0 = nbrDeLarvesGereables;
		
		// OUVRIERE D'AGE 2 A 10 : NETTOYAGE //
		
		float capaciteDeNettoyage = 0;

		for (Fourmi fourmi : l_ouvrieres_age2max) 
			capaciteDeNettoyage += ((Ouvriere) fourmi).getPuissanceNettoyage();

		// OUVRIERE D'AGE 11 ET PLUS : SEPARATION DES RÔLES ENTRE NETTOYAGE ET CHASSE //
		float puissanceDeChasse = 0;
		
		for(int index = 0 ; index < l_ouvrieres_age2a10.size() ; index++)
		{
			if(index%2 == 0)
				capaciteDeNettoyage += ((Ouvriere)l_ouvrieres_age2a10.get(index)).getPuissanceNettoyage();

			else
				puissanceDeChasse += ((Ouvriere)l_ouvrieres_age2a10.get(index)).getPuissanceChasse();
		}


		// GESTION DE LA NOURRITURE AVEC OUVRIERE POUR LA CHASSE //
		nourritures_courantes += puissanceDeChasse;
		
		if(nourritures_courantes > capacite_max_nourriture)
			nourritures_courantes = capacite_max_nourriture;
		
		// NETTOYAGE EFFECTIF - SI PAS ASSEZ DE CAPACITE NETTOYAGE ON SE DEBARASSE DES JEUNES EN TROP //
		if(capaciteDeNettoyage < l_fourmis.size())
		{
			for (int index = (int) capaciteDeNettoyage; index < l_fourmis.size(); index++)
				l_fourmis.remove(0);
		}

		// ON COMPTE LE NOMBRE D'OUVRIERE ET ON AJOUTE AUTANT DE CAPACITE x 100 A CHAQUE LIMITATION //

		int ajoutDeCapacite = 100 * (int)(l_ouvrieres_age2max.size() *0.1f);;
		capacite_max_nourriture += ajoutDeCapacite;
		capacite_max_larves += ajoutDeCapacite;
		capacite_max_population += ajoutDeCapacite;
	}
	
	public void gestionMenace()
	{
		// ON INCREMENT DE UN LA MENACE A CHAQUE TOUR //
		menaces_incrementables += 1;
		System.out.println("-----------------------------------------------------");
		System.out.println("Il y a actuellement " + (nombre_de_menaces + menaces_incrementables) + " menaces");
		System.out.println("-----------------------------------------------------");

		// ON COMPTE LA FORCE DES GUERRIERES ET LE NOMBRE DES GUERRIERES //
		float forceGuerriere = 0;
		int nbrGuerrieres = 0;
		for (Fourmi fourmi : l_fourmis) 
		{
			if(fourmi.estGuerriere)
			{
				forceGuerriere += ((Guerriere) fourmi).getForce();
				nbrGuerrieres++;
			}
		}
		
		/* SI LA PUISSANCE DES FOURMIS EST < AUX MENACES ON COMPTE LES MORTS 
		 * ON SUPPRIME AUTANT DE JEUNES FOURMIS QUE DE MORTS
		 * SINON ON COMPTE LES GUERRIERE ATTAQUANTS ET ON LEUR PERMET D'OBTENIR DE LA NOURRITURE POUR CHAQUE ATTAQUANTES */
		if(forceGuerriere <= (nombre_de_menaces + menaces_incrementables))
		{
			int nombre_de_morts = (nombre_de_menaces + menaces_incrementables) - nbrGuerrieres;
			int compteur = 0;
			while(l_fourmis.size() != 0 && compteur != nombre_de_morts)
			{
				l_fourmis.remove(0);
				compteur++;
			}
		}
		else
		{
			int nbrDeGuerrieresAttaquantes = nombre_de_menaces + menaces_incrementables;
			
			for(int i = 0; i < nbrDeGuerrieresAttaquantes; i++)
				nourritures_courantes += Math.abs(alea(1,3));
		}
		
		if(l_fourmis.size() == 0)
			estMorte = true;
	}
	
	public void gestionAlimentation()
	{	
		nourritures_courantes -= reine.getBesoinEnNourriture();
		
		if(nourritures_courantes < 0)
			estMorte = true;
		else
		{
			int indexFourmiNourrie = 0;
			int nbrDeFourmis = l_fourmis.size() ;
			
			while(nourritures_courantes > 0 && indexFourmiNourrie != nbrDeFourmis)
			{
				nourritures_courantes -= l_fourmis.get(indexFourmiNourrie).besoinEnNourriture;
				indexFourmiNourrie++;
			}
			
			int nombreDeFoumisSansNourriture = nbrDeFourmis - indexFourmiNourrie;
			if(nombreDeFoumisSansNourriture > 0)
			{
				for (int  index = 0; index < nombreDeFoumisSansNourriture; index++)
					l_fourmis.remove(0);
			}
		}
		
		for (int index = 0; index < l_fourmis.size(); index++) 
		{
			if(l_fourmis.get(index).mourirAuHasard())
				l_fourmis.remove(index);
		}
	}
	
	public void gestionAugmentationMenace()
	{
		int nourritures_potentielles = (int) (nourritures_courantes * popularite);
		if(nombre_de_menaces <= nourritures_potentielles)
		{
			popularite += 0.1f;
			
			while(nourritures_potentielles > nombre_de_menaces * 5)
				nombre_de_menaces++;		
		}
		else
		{
			if(popularite > 0.1f)
				popularite -= 0.1f;
			
			else
				popularite = 0.1f;
			
			nombre_de_menaces -= (nombre_de_menaces * popularite);
			
			if(nombre_de_menaces < 0)
				nombre_de_menaces = 0;
		}
		
		System.out.println("-----------------------------------------------------");
		System.out.println("Il y a " + (nombre_de_menaces + menaces_incrementables) + " menaces");
		System.out.println("Le fourmiliere à " + popularite + " de popularité");
		System.out.println("-----------------------------------------------------");

	}
	
	public void gestionAge()
	{
		reine.vieillir();
		for (Fourmi fourmi : l_fourmis) 
			fourmi.vieillir();
	}

	public int alea(int valeur_entrante, float pourcentage_alea)
	{
		int valeur_a_modifier = valeur_entrante;
		int harsard = (int) (Math.random() * (valeur_a_modifier * pourcentage_alea));
		
		if(Math.random() < 0.5f)
			valeur_a_modifier -= harsard;
		
		else
			valeur_a_modifier += harsard;
		
		return valeur_a_modifier;
	}
	
	public void statistiques(String evenement)
	{
		int nbrGuerrier = 0;
		int nbrOuvrier = 0;
		for (Fourmi fourmi : l_fourmis) 
		{
			if(fourmi.estOuvriere)
				nbrOuvrier++;
			
			else if(fourmi.estGuerriere)
				nbrGuerrier++;
		}
		System.out.println("Après ce moment : " + evenement + " \n"
				+ "On dénombre : " + l_fourmis.size() +" fourmis dont " + nbrGuerrier + " guerriers et " + nbrOuvrier +" ouvriers \n"
				+ nombre_de_larves_age_0 + " larves qui vient de naitre et "+ nombre_de_larves_age_1 +" qui sont prêt à devenir fourmi \n"
				+ reine.nbrLotsOeufs() +" lots d'oeufs \n"
				+ "Et " + nourritures_courantes +" unités de nouriture \n");
	}
}
