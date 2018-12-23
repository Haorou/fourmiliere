package fourmiliere;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fourmi.Fourmi;
import fourmi.Guerriere;
import fourmi.IInsecte;
import fourmi.LotOeufs;
import fourmi.Metier;
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
	private List<IInsecte> l_fourmis = new ArrayList<IInsecte>();
	private int nombre_de_larves_age_0 = 0;
	private int nombre_de_larves_age_1 = 0;
	
	private int nbrGuerrier = 0;
	private int nbrOuvrier_NURSE = 0;
	private int nbrOuvrier_NETTOYEUSE = 0;
	private int nbrOuvrier_CHASSEUSE = 0;
	private int nbrOuvrier_INGENIEUSE = 0;

	
	public Fourmiliere()
	{
		// INITIALISATION DE LA FOURMILIERE //
		reine = new Reine();
		Ouvriere fourmi_ouvriere;
		for (int i = 0; i < 200; i++) 
		{
			if(i > 100) 
			{
				fourmi_ouvriere = new Ouvriere(Metier.CHASSEUSE);
				fourmi_ouvriere.setAge(10);				
			}
			else if(i > 50)
			{
				fourmi_ouvriere = new Ouvriere(Metier.NETTOYEUSE);
				fourmi_ouvriere.setAge(3);
			}
			else
				fourmi_ouvriere = new Ouvriere(Metier.NURSE);

			l_fourmis.add(fourmi_ouvriere);
		}
	}
	
	public void evoluer()
	{
		System.out.println("==============================================================================");
		System.out.println("-------------------------------- TOURI " + dateCourante++ +" -------------------------------------- ");
		System.out.println("==============================================================================");
		
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
		
		gestionEvolutionFourmi();
		statistiques("gestionEvolutionFourmi");
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
			nourritures_courantes += alea(nbrDeLarvesEnTrop,0.05f);
			if(nourritures_courantes > capacite_max_nourriture)
			{
				nourritures_courantes = capacite_max_nourriture;
			}
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
		int nombreDeGuerrieresNecessaire = nombre_de_menaces + menaces_incrementables;
		
		
		for (int index = 0; index < nombre_de_larves_age_1; index++) 
		{
			if(nbrGuerrier <= nombreDeGuerrieresNecessaire)
			{
				l_fourmis.add(new Guerriere());
				nbrGuerrier++;
			}
			else
				l_fourmis.add(new Ouvriere(Metier.NURSE));				
		}
		nombre_de_larves_age_1 = 0;

		rangerFourmiOrdreAge(true);
		
		// SI IL Y A PLUS DE FOURMI QUE DE CAPACITE DE POPULATION ON SE DEBARASSE DES JEUNES EN TROP //
		if(l_fourmis.size() > capacite_max_population)
		{
			for (int index = capacite_max_population; index < l_fourmis.size(); index++) 
				l_fourmis.remove(0);
		}
	}
	
	private void gestionOuvriereProductionNettoyage()
	{
		int nbrDeLarvesGereables = 0;
		float capaciteDeNettoyage = 0;
		float puissanceDeChasse = 0;
		float puissanceIngenieur = 0;
		
		for (IInsecte fourmi : l_fourmis)
		{
			if(fourmi.getMetier() == Metier.NURSE)
				nbrDeLarvesGereables += fourmi.getProduction();
		}
		// GESTION LARVES //
		if(nombre_de_larves_age_0 > nbrDeLarvesGereables)
			nombre_de_larves_age_0 = nbrDeLarvesGereables;
		
		for (IInsecte fourmi : l_fourmis)
		{
			if(fourmi.getMetier() == Metier.CHASSEUSE)
				puissanceDeChasse += fourmi.getProduction();
		}

		// GESTION DE LA NOURRITURE AVEC OUVRIERE POUR LA CHASSE //
		nourritures_courantes += puissanceDeChasse;
		
		if(nourritures_courantes > capacite_max_nourriture)
			nourritures_courantes = capacite_max_nourriture;
		
		for (IInsecte fourmi : l_fourmis)
		{
			if(fourmi.getMetier() == Metier.NETTOYEUSE)
				capaciteDeNettoyage += fourmi.getProduction();
		}
		
		// NETTOYAGE EFFECTIF - SI PAS ASSEZ DE CAPACITE NETTOYAGE ON SE DEBARASSE DES JEUNES EN TROP //
		if(capaciteDeNettoyage < l_fourmis.size())
		{
			for (int index = (int) capaciteDeNettoyage; index < l_fourmis.size(); index++)
				l_fourmis.remove(0);
		}

		for (IInsecte fourmi : l_fourmis)
		{
			if(fourmi.getMetier() == Metier.INGENIEUR)
				puissanceIngenieur += fourmi.getProduction();
		}
		
		capacite_max_nourriture += puissanceIngenieur;
		capacite_max_larves += puissanceIngenieur;
		capacite_max_population += puissanceIngenieur;
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
		for (IInsecte fourmi : l_fourmis) 
		{
			if(fourmi.getMetier() == Metier.GUERRIERE)
			{
				forceGuerriere += fourmi.getProduction();
				nbrGuerrieres++;
			}
		}
		
		/* SI LA PUISSANCE DES FOURMIS EST < AUX MENACES ON COMPTE LES MORTS 
		 * ON SUPPRIME AUTANT DE JEUNES FOURMIS QUE DE MORTS
		 * SINON ON COMPTE LES GUERRIERE ATTAQUANTS ET ON LEUR PERMET D'OBTENIR DE LA NOURRITURE POUR CHAQUE ATTAQUANTES */
		if(forceGuerriere <= (nombre_de_menaces + menaces_incrementables))
		{
			rangerFourmiOrdreAge(false);
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
				nourritures_courantes += Math.abs(alea(1,3f));
		}
		
		if(l_fourmis.size() == 0)
		{
			System.out.println("====================================================================================================\n"
							 + "================================> Il n'y a plus de fourmis en vie, le menace était trop importante, la fourmillière est morte "
						   + "\n=====================================================================================================");
			estMorte = true;
		}

	}
	
	public void gestionAlimentation()
	{	
		nourritures_courantes -= reine.getBesoinEnNourriture();
		rangerFourmiOrdreAge(true);
		if(nourritures_courantes < 0)
		{
			System.out.println("====================================================================================================\n"
							 + "================================> Il n'y a pas assez de nourriture, les fourmis sont mortent de faim "
						   + "\n=====================================================================================================");
			estMorte = true;	
		}
		else
		{
			int indexFourmiNourrie = 0;
			int nbrDeFourmis = l_fourmis.size() ;
			
			while(nourritures_courantes > 0 && indexFourmiNourrie != nbrDeFourmis)
			{
				nourritures_courantes -= l_fourmis.get(indexFourmiNourrie).getBesoinEnNourriture();
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
		int nourritures_potentielles = (int) (nourritures_courantes / 10 * popularite);
		
		if(nombre_de_menaces <= nourritures_potentielles)
		{
			popularite += 0.1f;
			
			while(nourritures_potentielles > nombre_de_menaces)
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
		for (IInsecte fourmi : l_fourmis) 
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
	
	public void gestionEvolutionFourmi()
	{
		// ON LANCE L'EVOLUTION DES FOURMIS NETTOYEUSES SI NECESSAIRE //
		int nombreDeFourmisNettoyeusesNeededAuTotal = l_fourmis.size() / 20;
		int nombreDeFourmisNettoyeusesNeededEnPlus = nombreDeFourmisNettoyeusesNeededAuTotal - nbrOuvrier_NETTOYEUSE;
		
		int compteurFourmisNettoyeusesNeeded = 0;
		for (IInsecte iInsecte : l_fourmis) 
		{

			if(iInsecte.getMetier() != Metier.GUERRIERE && iInsecte.getAge() >= 10)
			{
				iInsecte.setMetier(Metier.CHASSEUSE);
			}
			else
			{
				if(iInsecte.getMetier() != Metier.GUERRIERE && iInsecte.getAge() >= 3 && compteurFourmisNettoyeusesNeeded < nombreDeFourmisNettoyeusesNeededEnPlus)
				{
					iInsecte.setMetier(Metier.NETTOYEUSE);
					compteurFourmisNettoyeusesNeeded++;
				} 
				else if(iInsecte.getMetier() != Metier.GUERRIERE && iInsecte.getAge() >= 3)
				{
					iInsecte.setMetier(Metier.INGENIEUR);
				}				
			}
		}			
	}
	
	public void rangerFourmiOrdreAge(boolean dansOrdre)
	{
		if(dansOrdre)
		{
			// ON RANGE LES FOURMIS PAR AGE - PLUS JEUNE A PLUS AGEE //
			Comparator<? super IInsecte> byAge = Comparator.comparing(IInsecte::getAge);
			l_fourmis.sort(byAge);			
		}
		else
			Collections.shuffle(l_fourmis);
	}
	
	public void statistiques(String evenement)
	{	
		nbrGuerrier = 0;
		nbrOuvrier_CHASSEUSE = 0;
		nbrOuvrier_NETTOYEUSE = 0;
		nbrOuvrier_INGENIEUSE = 0;
		nbrOuvrier_NURSE = 0;
		
		for (IInsecte fourmi : l_fourmis) 
		{
			if(fourmi.getMetier() == Metier.GUERRIERE)
				nbrGuerrier++;
			else if(fourmi.getMetier() == Metier.CHASSEUSE)
				nbrOuvrier_CHASSEUSE++;
			else if(fourmi.getMetier() == Metier.NETTOYEUSE)
				nbrOuvrier_NETTOYEUSE++;
			else if(fourmi.getMetier() == Metier.INGENIEUR)
				nbrOuvrier_INGENIEUSE++;
			else if(fourmi.getMetier() == Metier.NURSE)
				nbrOuvrier_NURSE++;
		}
		System.out.println("Après ce moment : " + evenement + " \n"
				+ "On dénombre : " + l_fourmis.size() +" fourmis dont : \n"
						+ " " + nbrGuerrier + " Guerriers \n"
						+ " " + nbrOuvrier_CHASSEUSE + " ouvriers chasseuses \n"
						+ " " + nbrOuvrier_NETTOYEUSE + " ouvriers nettoyeuse \n"
						+ " " + nbrOuvrier_INGENIEUSE + " ouvrier ingénieuses \n"
						+ " " + nbrOuvrier_NURSE +  " ouvrier nurses \n"
				+ nombre_de_larves_age_0 + " larves qui vient de naitre et "+ nombre_de_larves_age_1 +" qui sont prêt à devenir fourmi \n"
				+ reine.nbrLotsOeufs() +" lots d'oeufs \n"
				+ "Et " + nourritures_courantes +" unités de nouriture \n");
	}
}
