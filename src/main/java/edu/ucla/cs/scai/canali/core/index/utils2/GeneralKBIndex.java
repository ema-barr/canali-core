package edu.ucla.cs.scai.canali.core.index.utils2;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Emanuele Pio Barracchia
 */
public class GeneralKBIndex {
    String sourceFile, destinationPath;
    HashMap<String, Integer> entityIds = new HashMap<>();
    HashMap<String, Integer> classIds = new HashMap<>();
    HashMap<String, Integer> propertyIds = new HashMap<>();
    String[] entityById;
    String[] classById;
    String[] propertyById;
    HashSet<String>[] classLabels;
    HashSet<String>[] propertyLabels;
    HashSet<String>[] entityLabels;
    HashSet<Integer>[] entityClasses;
    HashMap<Integer, HashMap<Integer, Set<Integer>>> triplesObjectValue;
    HashMap<Integer, HashMap<Integer, Set<String>>> triplesLiteralValue;
    LinkedList<String> typeLines, labelLines, subclassofLines, otherLinesObjectValue, otherLinesLiteralValue;

    public GeneralKBIndex(String sourceFile, String destinationPath) throws Exception {
        if (!destinationPath.endsWith(File.separator)) {
            destinationPath += File.separator;
        }
        this.sourceFile = sourceFile;
        this.destinationPath = destinationPath;
    }

    private String fromCamelCaseOrUnderscore(String s) {
        s = s.replaceAll("_", " ");
        s = StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(s),
                ' '
        );
        s = s.replace("  ", " ");
        s = s.replace("  ", " ");
        return s;
    }

    public void loadFile() {
        typeLines = new LinkedList<>();
        labelLines = new LinkedList<>();
        subclassofLines = new LinkedList<>();
        otherLinesLiteralValue = new LinkedList<>();
        otherLinesObjectValue = new LinkedList<>();
        int ln = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(sourceFile))) {
            String l;
            ln++;
            String regexType = "(\\s)*<([^<>]*)>(\\s)+<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>(\\s)+<([^<>]*)>(\\s)+\\.";
            String regexLabel = "(\\s)*<([^<>]*)>(\\s)+<http://www.w3.org/2000/01/rdf-schema#label>(\\s)+\"(.*)\"(\\s)+\\.";
            String regexSubclassOf = "(\\s)*<([^<>]*)>(\\s)+<http://www.w3.org/2000/01/rdf-schema#subClassOf>(\\s)+<([^<>]*)>(\\s)+\\.";
            String regexOtherObjectValue = "(\\s)*<([^<>]*)>(\\s)+<([^<>]*)>(\\s)+<([^<>]*)>(\\s)+\\.";
            String regexOtherLiteralValueNoDataType = "(\\s)*<([^<>]*)>(\\s)+<([^<>]*)>(\\s)+\"(.*)\"(\\s)+\\.";
            String regexOtherLiteralValueDataType = "(\\s)*<([^<>]*)>(\\s)+<([^<>]*)>(\\s)+(\"(.*)\"\\^\\^<([^<>]*)>)(\\s)+\\.";
            Pattern pt = Pattern.compile(regexType);
            Pattern pl = Pattern.compile(regexLabel);
            Pattern ps = Pattern.compile(regexSubclassOf);
            Pattern poov = Pattern.compile(regexOtherObjectValue);
            Pattern polvndt = Pattern.compile(regexOtherLiteralValueNoDataType);
            Pattern polvdt = Pattern.compile(regexOtherLiteralValueDataType);
            while ((l = in.readLine()) != null) {
                ln++;
                Matcher m = pt.matcher(l);
                if (m.find()) {
                    typeLines.add(m.group(2) + "\t" + m.group(5));
                } else {
                    m = pl.matcher(l);
                    if (m.find()) {
                        labelLines.add(m.group(2) + "\t" + m.group(5));
                    } else {
                        m = ps.matcher(l);
                        if (m.find()) {
                            subclassofLines.add(m.group(2) + "\t" + m.group(5));
                        } else {
                            m = poov.matcher(l);
                            if (m.find()) {
                                otherLinesObjectValue.add(m.group(2) + "\t" + m.group(4) + "\t" + m.group(6))  ;
                            } else {
                                m = polvndt.matcher(l);
                                if (m.find()) {
                                    otherLinesLiteralValue.add( m.group(2) + "\t" + m.group(4) + "\t\"" + m.group(6) + "\"");
                                } else {
                                    m = polvdt.matcher(l);
                                    if (m.find()) {
                                        otherLinesLiteralValue.add(m.group(2) + "\t" + m.group(4) + "\t" + m.group(6));
                                    } else {
                                        System.out.println("Line " + ln + " not valid: " + l);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //All'interno della lista dei tipi distingue tra classi e proprietà
    //IMPORTANTE: ogni qual volta che è identificata una nuova riga e indicizzata, tale riga viene rimossa dalla lista
    public void loadClassesAndProperties() {
        classIds = new HashMap<>();
        propertyIds = new HashMap<>();
        for (Iterator<String> it = typeLines.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            if (l[1].equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property")) {
                if (propertyIds.containsKey(l[0])) {
                    System.out.println("Property " + l[0] + " defined more than once");
                } else {
                    propertyIds.put(l[0], propertyIds.size() + 1);
                }
                it.remove();
            } else if (l[1].equals("http://www.w3.org/2000/01/rdf-schema#Class")) {
                if (classIds.containsKey(l[0])) {
                    System.out.println("Class " + l[0] + " defined more than once");
                } else {

                    classIds.put(l[0], classIds.size() + 1);
                }
                it.remove();
            }
        }
        classById = new String[classIds.size() + 1];
        classIds.entrySet().stream().forEach((e) -> {
            //istruzioni necessarie perchè d2rq genera gli URI relativi in base a http://localhost:2020/resource/
            String value;
            if (e.getKey().contains("vocab")){
                value = "http://localhost:2020/resource/" + e.getKey().split(":2020/")[1];
            } else {
                value = e.getKey();
            }
            classById[e.getValue()] = value;
        });
        propertyById = new String[propertyIds.size() + 1];

        propertyIds.entrySet().stream().forEach((e) -> {
            //istruzioni necessarie perchè d2rq genera gli URI relativi in base a http://localhost:2020/resource/
            String value;
            if (e.getKey().contains("vocab")){
                value = "http://localhost:2020/resource/" + e.getKey().split(":2020/")[1];
            } else {
                value = e.getKey();
            }
            propertyById[e.getValue()] = value;
        });
    }

    //All'interno della lista delle sottoclassi identifica le sottoclassi
    //IMPORTANTE: ogni qual volta che è identificata una nuova riga e indicizzata, tale riga viene rimossa dalla lista
    public void createClassParentsFile() throws IOException {
        try (
                PrintWriter out = new PrintWriter(new FileOutputStream(destinationPath + "class_parents"))) {
            for (Iterator<String> it = subclassofLines.iterator(); it.hasNext();) {
                String l[] = it.next().split("\t");
                String cc = l[0];
                String cp = l[1];
                if (!classIds.containsKey(cc)) {
                    System.out.println("The class " + l[0] + " is sublclass of " + l[1] + ", but " + l[0] + " was not defined");
                    continue;
                }
                if (!classIds.containsKey(cp)) {
                    System.out.println("The class " + l[0] + " is sublclass of " + l[1] + ", but " + l[1] + " was not defined");
                    continue;
                }
                out.println(cc + "\t" + cp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Cerca le entità ll'interno di tutte le liste tranne in quella delle sottoclassi, in quanto non contiene alcuna informazione sulle entità
    public void loadEntities() {
        entityIds = new HashMap<>();
        for (Iterator<String> it = typeLines.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            if (!entityIds.containsKey(l[0])) {
                entityIds.put(l[0], entityIds.size() + 1);
            }
        }
        for (Iterator<String> it = labelLines.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            if (!entityIds.containsKey(l[0]) && !classIds.containsKey(l[0]) && !propertyIds.containsKey(l[0])) {
                entityIds.put(l[0], entityIds.size() + 1);
                System.out.println(l[0] + " " + l[1] + " " + l[2]);
            }
        }
        for (Iterator<String> it = otherLinesObjectValue.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            if (!entityIds.containsKey(l[0])) {
                entityIds.put(l[0], entityIds.size() + 1);
                System.out.println(l[0] + " " + l[1] + " " + l[2]);
            }
            if (!entityIds.containsKey(l[2])) {
                entityIds.put(l[2], entityIds.size() + 1);
                System.out.println(l[0] + " " + l[1] + " " + l[2]);
            }
        }
        for (Iterator<String> it = otherLinesLiteralValue.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            if (!entityIds.containsKey(l[0])) {
                entityIds.put(l[0], entityIds.size() + 1);
                System.out.println(l[0] + " " + l[1] + " " + l[2]);
            }
        }
        entityById = new String[entityIds.size() + 1];
        entityIds.entrySet().stream().forEach((e) -> {
            //istruzione necessaria perchè d2rq genera gli URI relativi in base a http://localhost:2020/resource/
            String value = "http://localhost:2020/resource/" + e.getKey().split(":2020/")[1];
            entityById[e.getValue()] = value;
        });
    }

    public String labelFromURI(String uri) {
        try {
            String[] s = uri.split("/");
            return fromCamelCaseOrUnderscore(s[s.length - 1]);
        } catch (Exception e) {
            System.out.println("Could not generate label for URI " + uri);
        }
        return null;
    }

    //Cerca le label all'interno della lista della label e le distingue tra lebel di classe, proprietà ed entità.
    //Nel caso in cui esistano classi, proprietà o entità senza label, il sistema cerca di crearne una a partire dal suo URI
    // IMPORTANTE: ogni qual volta che è identificata una nuova riga e indicizzata, tale riga viene rimossa dalla lista
    public void loadLabels() {
        classLabels = new HashSet[classById.length];
        for (int i = 1; i < classLabels.length; i++) {
            classLabels[i] = new HashSet<>();
        }
        propertyLabels = new HashSet[propertyById.length];
        for (int i = 1; i < propertyLabels.length; i++) {
            propertyLabels[i] = new HashSet<>();
        }
        entityLabels = new HashSet[entityById.length];
        for (int i = 1; i < entityLabels.length; i++) {
            entityLabels[i] = new HashSet<>();
        }
        try (BufferedReader in = new BufferedReader(new FileReader(sourceFile))) {
            for (Iterator<String> it = labelLines.iterator(); it.hasNext();) {
                String[] l = it.next().split("\t");
                if (classIds.containsKey(l[0])) {
                    classLabels[classIds.get(l[0])].add(l[1]);
                    it.remove();
                } else if (propertyIds.containsKey(l[0])) {
                    propertyLabels[propertyIds.get(l[0])].add(l[1]);
                    it.remove();
                } else if (entityIds.containsKey(l[0])) {
                    entityLabels[entityIds.get(l[0])].add(l[1]);
                    it.remove();
                } else {
                    System.out.println("Could not assign the label " + l[1] + " to the unknown resource " + l[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //now create artificial labels
        for (int i = 1; i < classLabels.length; i++) {
            if (classLabels[i].isEmpty()) {
                String label = labelFromURI(classById[i]);
                if (label != null) {
                    classLabels[i].add(label);
                } else {
                    System.out.println("The class " + classById[i] + " was not assigned a label. Classes without a label cannot be used in questions.");
                }
            }
        }
        for (int i = 1; i < propertyLabels.length; i++) {
            if (propertyLabels[i].isEmpty()) {
                String label = labelFromURI(propertyById[i]);
                if (label != null) {
                    propertyLabels[i].add(label);
                } else {
                    System.out.println("The property " + propertyById[i] + " was not assigned a label. Properties without a label cannot be used in questions.");
                }
            }
        }
        for (int i = 1; i < entityLabels.length; i++) {
            if (entityLabels[i].isEmpty()) {
                String label = labelFromURI(entityById[i]);
                if (label != null) {
                    entityLabels[i].add(label);
                } else {
                    System.out.println("The entity " + propertyById[i] + " was not assigned a label. Entities without a label cannot be used in questions.");
                }
            }
        }
    }

    // Cerca le associazioni entità-classe all'interno della lista dei tipi.
    // IMPORTANTE: ogni qual volta che è identificata una nuova riga e indicizzata, tale riga viene rimossa dalla lista
    public void loadEntityClasses() {
        entityClasses = new HashSet[entityById.length];
        for (int i = 1; i < entityClasses.length; i++) {
            entityClasses[i] = new HashSet<>();
        }
        for (Iterator<String> it = typeLines.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            if (!classIds.containsKey(l[1])) {
                System.out.println("Class " + l[1] + " is unknown");
                continue;
            }
            if (!entityIds.containsKey(l[0])) {
                System.out.println("Entity " + l[0] + " is unknown");
                continue;
            }
            entityClasses[entityIds.get(l[0])].add(classIds.get(l[1]));
            it.remove();
        }
    }

    // Cerca le triple soggetto, proprietà, valore all'interno della lista otherLinesObjectValue e otherLinesLiteralValue, verificando la validità delle tre componenti
    // IMPORTANTE: ogni qual volta che è identificata una nuova riga e indicizzata, tale riga viene rimossa dalla lista
    public void loadTriples() {
        triplesObjectValue = new HashMap<>();
        triplesLiteralValue = new HashMap<>();
        for (Iterator<String> it = otherLinesObjectValue.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            Integer ids = entityIds.get(l[0]);
            if (ids == null) {
                System.out.println("Subject  not recognized in triple " + l[0] + "\t" + l[1] + "\t" + l[2]);
                continue;
            }
            Integer idp = propertyIds.get(l[1]);
            if (idp == null) {
                System.out.println("Property not recognized in triple " + l[0] + "\t" + l[1] + "\t" + l[2]);
                continue;
            }
            Integer idv = entityIds.get(l[2]);
            if (idv == null) {
                System.out.println("Value  not recognized in triple " + l[0] + "\t" + l[1] + "\t" + l[2]);
                continue;
            }
            HashMap<Integer, Set<Integer>> mapPairs = triplesObjectValue.get(ids);
            if (mapPairs == null) {
                mapPairs = new HashMap<>();
                triplesObjectValue.put(ids, mapPairs);
            }
            Set<Integer> setValues = mapPairs.get(idp);
            if (setValues == null) {
                setValues = new HashSet<>();
                mapPairs.put(idp, setValues);
            }
            setValues.add(idv);
        }
        for (Iterator<String> it = otherLinesLiteralValue.iterator(); it.hasNext();) {
            String[] l = it.next().split("\t");
            Integer ids = entityIds.get(l[0]);
            if (ids == null) {
                System.out.println("Subject  not recognized in triple " + l[0] + "\t" + l[1] + "\t" + l[2]);
                continue;
            }
            Integer idp = propertyIds.get(l[1]);
            if (idp == null) {
                System.out.println("Property not recognized in triple " + l[0] + "\t" + l[1] + "\t" + l[2]);
                continue;
            }
            HashMap<Integer, Set<String>> mapPairs = triplesLiteralValue.get(ids);
            if (mapPairs == null) {
                mapPairs = new HashMap<>();
                triplesLiteralValue.put(ids, mapPairs);
            }
            Set<String> setValues = mapPairs.get(idp);
            if (setValues == null) {
                setValues = new HashSet<>();
                mapPairs.put(idp, setValues);
            }
            setValues.add(l[2]);
        }
    }

    public void saveFiles() {
        //now save the file from the data structures loaded in main memory

        //property_labels
        try (PrintWriter out = new PrintWriter(new FileOutputStream(destinationPath + "property_labels", false), true)) {
            for (int i = 1; i < propertyById.length; i++) {
                for (String label : propertyLabels[i]) {
                    out.println(propertyById[i] + "\t" + label);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //class_labels
        try (PrintWriter out = new PrintWriter(new FileOutputStream(destinationPath + "class_labels", false), true)) {
            for (int i = 1; i < classById.length; i++) {
                for (String label : classLabels[i]) {
                    out.println(classById[i] + "\t" + label);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //entity_classes
        try (PrintWriter out = new PrintWriter(new FileOutputStream(destinationPath + "entity_classes", false), true)) {
            for (int i = 1; i < entityById.length; i++) {
                for (Integer cid : entityClasses[i]) {
                    out.println(entityById[i] + "\t" + classById[cid]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //entity_labels
        try (PrintWriter out = new PrintWriter(new FileOutputStream(destinationPath + "entity_labels", false), true)) {
            for (int i = 1; i < entityById.length; i++) {
                for (String label : entityLabels[i]) {
                    out.println(entityById[i] + "\t" + label);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //triples
        try (PrintWriter out = new PrintWriter(new FileOutputStream(destinationPath + "triples", false), true)) {
            for (Map.Entry<Integer, HashMap<Integer, Set<Integer>>> e1 : triplesObjectValue.entrySet()) {
                String subject = entityById[e1.getKey()];
                for (Map.Entry<Integer, Set<Integer>> e2 : e1.getValue().entrySet()) {
                    String property = propertyById[e2.getKey()];
                    for (Integer vid : e2.getValue()) {
                        String value = entityById[vid];
                        out.println("<"+subject + ">\t<" + property + ">\t<" + value+ ">");
                    }
                }
            }
            for (Map.Entry<Integer, HashMap<Integer, Set<String>>> e1 : triplesLiteralValue.entrySet()) {
                String subject = entityById[e1.getKey()];
                for (Map.Entry<Integer, Set<String>> e2 : e1.getValue().entrySet()) {
                    String property = propertyById[e2.getKey()];
                    for (String value : e2.getValue()) {
                        out.println("<" + subject + ">\t<" + property + ">\t" + value );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String... args) throws Exception {
        String pathInput, pathOutput;
        if (args != null && args.length == 2) {
            pathInput = args[0];
            pathOutput = args[1];
        } else {
            pathInput = "C:\\Users\\MANU\\Desktop\\Universita\\Semeraro\\dvd\\source\\dvdrental.nt";
            pathOutput = "C:\\Users\\MANU\\Desktop\\Universita\\Semeraro\\dvd\\output\\";
        }
        long start = System.currentTimeMillis();
        System.out.println("Started at " + new Date());
        GeneralKBIndex mykb = new GeneralKBIndex(pathInput, pathOutput);
        mykb.loadFile();
        mykb.loadClassesAndProperties();
        mykb.createClassParentsFile();
        mykb.loadEntities();
        mykb.loadLabels();
        mykb.loadEntityClasses();
        mykb.loadTriples();
        mykb.saveFiles();
        mykb.createBasicTypesLiteralTypesFile();
        System.out.println("Ended at " + new Date());
        long time = System.currentTimeMillis() - start;
        long sec = time / 1000;
        System.out.println("The process took " + (sec / 60) + "'" + (sec % 60) + "." + (time % 1000) + "\"");
    }

    public void createBasicTypesLiteralTypesFile() throws Exception {
        System.out.println("Saving basic types");
        try (PrintWriter out = new PrintWriter(new FileOutputStream(destinationPath + "basic_types_literal_types", false), true)) {
            out.println("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\tString");
            out.println("http://www.w3.org/2001/XMLSchema#gMonthDay\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#anyURI\tString");
            out.println("http://www.w3.org/2001/XMLSchema#boolean\tBoolean");
            out.println("http://www.w3.org/2001/XMLSchema#date\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#dateTime\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#double\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#float\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#gYear\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#gYearMonth\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#integer\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#int\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#nonNegativeInteger\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#positiveInteger\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#string\tString");
            out.println("http://www.w3.org/2001/XMLSchema#decimal\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#hexBinary\tString");

        }
    }
}
