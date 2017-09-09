/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.util.ArrayList;

public class ExperimenterTest {

	public static void main(String[] args) throws Exception {

		QASystem qas = new CanaliW2VQASystemD2RQ("/Users/Manu/Desktop/Semeraro/w2v/abstract_200_20.w2v.bin", "/Users/Manu/Desktop/Semeraro/kb-processed/supportFiles/property_labels");
		//System.setProperty("kb.index.dir", "/home/lucia/nlp2sparql-data/dbpedia-processed/2015-10/dbpedia-processed_onlydbo_mini_e/index/"); //!!!
		System.setProperty("kb.index.dir", "/Users/Manu/Desktop/Semeraro/kb-processed/index");

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
		
		String query = "What is the payment rental id of payment #17503?";
		//String query = "What is the city #127?";
		//String query = "What is the city country id of city #127?";
		//String query ="What is the film category film id of film #1?";


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
