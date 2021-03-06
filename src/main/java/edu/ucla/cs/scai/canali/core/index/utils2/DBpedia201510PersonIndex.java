/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.index.utils2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lucia
 */
public class DBpedia201510PersonIndex {
    
    String sourcePath;
    String outputPath;
    HashSet<String> personSubclasses;
    HashSet<String> personEntities;

    public DBpedia201510PersonIndex(String sourceDir, String outputDir) {
        sourcePath = sourceDir;
        outputPath = outputDir;
    }
    
    public void computePersonSubclasses() {
    //the subclasses can be obtained through the following query
        /*
         SELECT DISTINCT ?ans
         WHERE {
         {?ans rdfs:subClassOf* dbo:Person} . 
         }        
         */
        String[] a = new String[]{"http://dbpedia.org/ontology/Person",
            "http://dbpedia.org/ontology/Writer",
            "http://dbpedia.org/ontology/TheatreDirector",
            "http://dbpedia.org/ontology/TelevisionPersonality",
            "http://dbpedia.org/ontology/TelevisionDirector",
            "http://dbpedia.org/ontology/SportsManager",
            "http://dbpedia.org/ontology/Scientist",
            "http://dbpedia.org/ontology/Royalty",
            "http://dbpedia.org/ontology/RomanEmperor",
            "http://dbpedia.org/ontology/Religious",
            "http://dbpedia.org/ontology/Referee",
            "http://dbpedia.org/ontology/Psychologist",
            "http://dbpedia.org/ontology/Producer",
            "http://dbpedia.org/ontology/Presenter",
            "http://dbpedia.org/ontology/PoliticianSpouse",
            "http://dbpedia.org/ontology/Politician",
            "http://dbpedia.org/ontology/PlayboyPlaymate",
            "http://dbpedia.org/ontology/Philosopher",
            "http://dbpedia.org/ontology/Orphan",
            "http://dbpedia.org/ontology/OrganisationMember",
            "http://dbpedia.org/ontology/OfficeHolder",
            "http://dbpedia.org/ontology/Noble",
            "http://dbpedia.org/ontology/MovieDirector",
            "http://dbpedia.org/ontology/Monarch",
            "http://dbpedia.org/ontology/Model",
            "http://dbpedia.org/ontology/MilitaryPerson",
            "http://dbpedia.org/ontology/MemberResistanceMovement",
            "http://dbpedia.org/ontology/Linguist",
            "http://dbpedia.org/ontology/Lawyer",
            "http://dbpedia.org/ontology/Judge",
            "http://dbpedia.org/ontology/Journalist",
            "http://dbpedia.org/ontology/HorseTrainer",
            "http://dbpedia.org/ontology/FictionalCharacter",
            "http://dbpedia.org/ontology/Farmer",
            "http://dbpedia.org/ontology/Engineer",
            "http://dbpedia.org/ontology/Egyptologist",
            "http://dbpedia.org/ontology/Economist",
            "http://dbpedia.org/ontology/Criminal",
            "http://dbpedia.org/ontology/Coach",
            "http://dbpedia.org/ontology/Cleric",
            "http://dbpedia.org/ontology/Chef",
            "http://dbpedia.org/ontology/Celebrity",
            "http://dbpedia.org/ontology/BusinessPerson",
            "http://dbpedia.org/ontology/BeautyQueen",
            "http://dbpedia.org/ontology/Athlete",
            "http://dbpedia.org/ontology/Astronaut",
            "http://dbpedia.org/ontology/Artist",
            "http://dbpedia.org/ontology/Aristocrat",
            "http://dbpedia.org/ontology/Architect",
            "http://dbpedia.org/ontology/Archeologist",
            "http://dbpedia.org/ontology/Ambassador",
            "http://dbpedia.org/ontology/Sculptor",
            "http://dbpedia.org/ontology/Photographer",
            "http://dbpedia.org/ontology/Painter",
            "http://dbpedia.org/ontology/MusicalArtist",
            "http://dbpedia.org/ontology/Humorist",
            "http://dbpedia.org/ontology/FashionDesigner",
            "http://dbpedia.org/ontology/Dancer",
            "http://dbpedia.org/ontology/ComicsCreator",
            "http://dbpedia.org/ontology/Comedian",
            "http://dbpedia.org/ontology/Actor",
            "http://dbpedia.org/ontology/Wrestler",
            "http://dbpedia.org/ontology/WinterSportPlayer",
            "http://dbpedia.org/ontology/WaterPoloPlayer",
            "http://dbpedia.org/ontology/VolleyballPlayer",
            "http://dbpedia.org/ontology/TennisPlayer",
            "http://dbpedia.org/ontology/TeamMember",
            "http://dbpedia.org/ontology/TableTennisPlayer",
            "http://dbpedia.org/ontology/Swimmer",
            "http://dbpedia.org/ontology/Surfer",
            "http://dbpedia.org/ontology/SquashPlayer",
            "http://dbpedia.org/ontology/SoccerPlayer",
            "http://dbpedia.org/ontology/SnookerPlayer",
            "http://dbpedia.org/ontology/RugbyPlayer",
            "http://dbpedia.org/ontology/Rower",
            "http://dbpedia.org/ontology/PokerPlayer",
            "http://dbpedia.org/ontology/NetballPlayer",
            "http://dbpedia.org/ontology/NationalCollegiateAthleticAssociationAthlete",
            "http://dbpedia.org/ontology/MotorsportRacer",
            "http://dbpedia.org/ontology/MartialArtist",
            "http://dbpedia.org/ontology/LacrossePlayer",
            "http://dbpedia.org/ontology/Jockey",
            "http://dbpedia.org/ontology/HorseRider",
            "http://dbpedia.org/ontology/HighDiver",
            "http://dbpedia.org/ontology/HandballPlayer",
            "http://dbpedia.org/ontology/Gymnast",
            "http://dbpedia.org/ontology/GridironFootballPlayer",
            "http://dbpedia.org/ontology/GolfPlayer",
            "http://dbpedia.org/ontology/GaelicGamesPlayer",
            "http://dbpedia.org/ontology/Fencer",
            "http://dbpedia.org/ontology/DartsPlayer",
            "http://dbpedia.org/ontology/Cyclist",
            "http://dbpedia.org/ontology/Cricketer",
            "http://dbpedia.org/ontology/ChessPlayer",
            "http://dbpedia.org/ontology/Canoeist",
            "http://dbpedia.org/ontology/BullFighter",
            "http://dbpedia.org/ontology/Boxer",
            "http://dbpedia.org/ontology/Bodybuilder",
            "http://dbpedia.org/ontology/BasketballPlayer",
            "http://dbpedia.org/ontology/BaseballPlayer",
            "http://dbpedia.org/ontology/BadmintonPlayer",
            "http://dbpedia.org/ontology/AustralianRulesFootballPlayer",
            "http://dbpedia.org/ontology/AthleticsPlayer",
            "http://dbpedia.org/ontology/ArcherPlayer",
            "http://dbpedia.org/ontology/Vicar",
            "http://dbpedia.org/ontology/Saint",
            "http://dbpedia.org/ontology/Priest",
            "http://dbpedia.org/ontology/Pope",
            "http://dbpedia.org/ontology/ChristianPatriarch",
            "http://dbpedia.org/ontology/ChristianBishop",
            "http://dbpedia.org/ontology/Cardinal",
            "http://dbpedia.org/ontology/VolleyballCoach",
            "http://dbpedia.org/ontology/CollegeCoach",
            "http://dbpedia.org/ontology/AmericanFootballCoach",
            "http://dbpedia.org/ontology/Murderer",
            "http://dbpedia.org/ontology/SoapCharacter",
            "http://dbpedia.org/ontology/NarutoCharacter",
            "http://dbpedia.org/ontology/MythologicalFigure",
            "http://dbpedia.org/ontology/DisneyCharacter",
            "http://dbpedia.org/ontology/ComicsCharacter",
            "http://dbpedia.org/ontology/SportsTeamMember",
            "http://dbpedia.org/ontology/VicePrimeMinister",
            "http://dbpedia.org/ontology/VicePresident",
            "http://dbpedia.org/ontology/Senator",
            "http://dbpedia.org/ontology/PrimeMinister",
            "http://dbpedia.org/ontology/President",
            "http://dbpedia.org/ontology/MemberOfParliament",
            "http://dbpedia.org/ontology/Mayor",
            "http://dbpedia.org/ontology/Lieutenant",
            "http://dbpedia.org/ontology/Governor",
            "http://dbpedia.org/ontology/Deputy",
            "http://dbpedia.org/ontology/Congressman",
            "http://dbpedia.org/ontology/Chancellor",
            "http://dbpedia.org/ontology/TelevisionHost",
            "http://dbpedia.org/ontology/RadioHost",
            "http://dbpedia.org/ontology/BritishRoyalty",
            "http://dbpedia.org/ontology/Professor",
            "http://dbpedia.org/ontology/Medician",
            "http://dbpedia.org/ontology/Entomologist",
            "http://dbpedia.org/ontology/Biologist",
            "http://dbpedia.org/ontology/SoccerManager",
            "http://dbpedia.org/ontology/Host",
            "http://dbpedia.org/ontology/SongWriter",
            "http://dbpedia.org/ontology/ScreenWriter",
            "http://dbpedia.org/ontology/Poet",
            "http://dbpedia.org/ontology/PlayWright",
            "http://dbpedia.org/ontology/MusicComposer",
            "http://dbpedia.org/ontology/Historian",
            "http://dbpedia.org/ontology/Baronet",
            "http://dbpedia.org/ontology/AnimangaCharacter",
            "http://dbpedia.org/ontology/SerialKiller",
            "http://dbpedia.org/ontology/AmateurBoxer",
            "http://dbpedia.org/ontology/CanadianFootballPlayer",
            "http://dbpedia.org/ontology/AmericanFootballPlayer",
            "http://dbpedia.org/ontology/RacingDriver",
            "http://dbpedia.org/ontology/MotorcycleRider",
            "http://dbpedia.org/ontology/SnookerChamp",
            "http://dbpedia.org/ontology/BeachVolleyballPlayer",
            "http://dbpedia.org/ontology/SpeedSkater",
            "http://dbpedia.org/ontology/Skier",
            "http://dbpedia.org/ontology/Ski_jumper",
            "http://dbpedia.org/ontology/Skater",
            "http://dbpedia.org/ontology/NordicCombined",
            "http://dbpedia.org/ontology/IceHockeyPlayer",
            "http://dbpedia.org/ontology/FigureSkater",
            "http://dbpedia.org/ontology/Curler",
            "http://dbpedia.org/ontology/CrossCountrySkier",
            "http://dbpedia.org/ontology/BobsleighAthlete",
            "http://dbpedia.org/ontology/Biathlete",
            "http://dbpedia.org/ontology/SumoWrestler",
            "http://dbpedia.org/ontology/VoiceActor",
            "http://dbpedia.org/ontology/AdultActor",
            "http://dbpedia.org/ontology/Singer",
            "http://dbpedia.org/ontology/MusicDirector",
            "http://dbpedia.org/ontology/Instrumentalist",
            "http://dbpedia.org/ontology/ClassicalMusicArtist",
            "http://dbpedia.org/ontology/BackScene",
            "http://dbpedia.org/ontology/Guitarist",
            "http://dbpedia.org/ontology/SpeedwayRider",
            "http://dbpedia.org/ontology/MotocycleRacer",
            "http://dbpedia.org/ontology/RallyDriver",
            "http://dbpedia.org/ontology/NascarDriver",
            "http://dbpedia.org/ontology/FormulaOneRacer",
            "http://dbpedia.org/ontology/DTMRacer"};
        personSubclasses = new HashSet<>();
        personSubclasses.addAll(Arrays.asList(a));
    }
    
    public void computePersonEntities() {
        try {
            personEntities = new HashSet<>();
            BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/instance_types_en.ttl"));
            Pattern pattern = Pattern.compile("<(.*)> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <(.*)>");
            
            String l = in.readLine();
            //int n = 0; //!!!
            while (l != null /*&& n < 1000*/) { //!!!
                Matcher match = pattern.matcher(l);
                if (match.find()) {
                    String eUri = match.group(1);
                    String cUri = match.group(2);
                    if (personSubclasses.contains(cUri)) {
                        personEntities.add(eUri);
                    }
                }
                l = in.readLine();
                //n++; //!!!
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510PersonIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DBpedia201510PersonIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createTripleFile() {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/person_triples", false), true);
            BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/mappingbased_objects_en.ttl"));
            Pattern pattern = Pattern.compile("<([^<>]*)>\\s<([^<>]*)>\\s<([^<>]*)>");
        
            String l = in.readLine();
            //int n = 0; //!!! to remove
            while (l != null /*&& n < 1000*/) { //!!!
                Matcher match = pattern.matcher(l);
                if (match.find()) {
                    String sbj = match.group(1);
                    if (personEntities.contains(sbj)) {
                        out.println(l);
                    }
                }
                l = in.readLine();
                //n++; //!!!
            }
            in.close();
            
            in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/mappingbased_literals_en.ttl"));
            pattern = Pattern.compile("<([^<>]*)>\\s<([^<>]*)>\\s\"(.*)\"");
        
            l = in.readLine();
            while (l != null /*&& n < 2000*/) { //!!!
                Matcher match = pattern.matcher(l);
                if (match.find()) {
                    String sbj = match.group(1);
                    if (personEntities.contains(sbj)) {
                        out.println(l);
                    }
                }
                l = in.readLine();
                //n++; //!!!
            }
            in.close();
            
//            in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/infobox_properties_unredirected_en.ttl"));
        
//            l = in.readLine();
//            while (l != null) { 
//                Matcher match = pattern.matcher(l);
//                if (match.find()) {
//                    String sbj = match.group(1);
//                    if (personEntities.contains(sbj)) {
//                        out.println(l);
//                    }
//                }
//                l = in.readLine();
//                //n++; //!!!
//            }
//            in.close();
            out.close();
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510PersonIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DBpedia201510PersonIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("in DBpedia201510PersonIndex");
        DBpedia201510PersonIndex dbpedia = new DBpedia201510PersonIndex(args[0], args[1]);
        dbpedia.computePersonSubclasses();
        dbpedia.computePersonEntities();
        dbpedia.createTripleFile();
        System.out.println("Ended at " + new Date());
    }
}
