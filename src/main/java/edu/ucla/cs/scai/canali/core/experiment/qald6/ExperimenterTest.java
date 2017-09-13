/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.util.ArrayList;

public class ExperimenterTest {

	public static void main(String[] args) throws Exception {

		QASystem qas = new CanaliW2VQASystemD2RQ("/Users/Manu/Desktop/Semeraro/w2v/abstract_200_20.w2v.bin", "/Users/Manu/Desktop/Semeraro/dvd processed/supportFiles/property_labels");
		//System.setProperty("kb.index.dir", "/home/lucia/nlp2sparql-data/dbpedia-processed/2015-10/dbpedia-processed_onlydbo_mini_e/index/"); //!!!
		System.setProperty("kb.index.dir", "/Users/Manu/Desktop/Semeraro/dvd processed/index");

		System.setProperty("sparql.endpoint", "http://localhost:2020/sparql");

		/*
		 * System answers
		 */
		//String query = "Who are the developers of DBpedia?";
		//String query = "What is the active years end date of Boris Becker?";
		//String query = "What is the number of children of Jacques Cousteau?";
		//String query = "What is the count of movies directed by Park Chan-wook?";
		//String query = "What is the prize of Alain Connes?";
		//String query = "Is there a award of Aki Kaurismäki equal to Grand Prix (Cannes Film Festival) ?";
		//------------------------------------------
        //dvd
		//String query = "What is the payment rental id of payment #17503?";
		//String query = "What is the release year of Arizona Bang?";
		//String query = "What is the city country id of city #127?";
		//String query ="What is the film_actor actor id of film_actor #27/398?";
		//String query = "What is the actor first_name of actor #27?";

		//String query = "What is the count of city #127?";
		//String query = "What is the count of film actor actor_id [inverted] of actor #27?";
		//String query = "What is the count of payment rental id ?";
		//String query = "What is the rental last update of rental #1?";

		//---- nuove query
		//String query = "What is the release year of Arizona Bang?";
		//String query = "Who is Adam Grant?";
		String query = "What is the city country id of Abha?";
		//String query = "What is the actor last update of Al Garland?";
		//String query = "What is the count of actor?";
		//String query = "What is the film actor actor_id [inverted] of Adam Grant?";
		//String query = "What is the count of film actor actor_id [inverted] of Adam Grant?";


        //mydb_simple
		//String query = "What is the name of customer #1?";//inserendo un nuovo cliente per quel film il count aumenta
        //------------------------------------------

		//String query = "What are the award of Alain Connes?";
		
		//String query = "What is the location city of Heineken International?";
		//String query = "What are the movies starring Jesse Eisenberg?";
		ArrayList<String> systAns = new ArrayList<String>();
		systAns = qas.getAnswer(query, null);

		for (String a : systAns) {
			System.out.println("System = " + a);
		}

	}
}
