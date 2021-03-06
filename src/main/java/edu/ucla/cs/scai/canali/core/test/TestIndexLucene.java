package edu.ucla.cs.scai.canali.core.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

public class TestIndexLucene {

	public static void main(String[] args) throws Exception {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("index.txt"));

		String indexPath = "/Users/Manu/Desktop/Semeraro/kb-processed/index/lucene";

		Directory directory = new RAMDirectory();
		FSDirectory tempDirectory;
		tempDirectory = FSDirectory.open(Paths.get(indexPath));
		for (String file : tempDirectory.listAll()) {
			directory.copyFrom(tempDirectory, file, file, IOContext.DEFAULT);
		}

		IndexReader reader = DirectoryReader.open(directory);
		System.out.println("# docs = " + reader.maxDoc());
		for (int i = 0; i < reader.maxDoc(); i++) {

			Document doc = reader.document(i);
			//System.out.println("id = " + doc.get("id") + ", label = " + doc.get("label") + ", type = " + doc.get("type") + ", domainOfProperty = " + doc.get("domainOfProperty") + ", rangeOfProperty = " + doc.get("rangeOfProperty")+ ", propertyDomain = " + doc.get("propertyDomain"));
			writer.write("id = " + doc.get("id") + ", label = " + doc.get("label") + ", type = " + doc.get("type") + ", domainOfProperty = " + doc.get("domainOfProperty") + ", rangeOfProperty = " + doc.get("rangeOfProperty")+ ", propertyDomain = " + doc.get("propertyDomain"));
			writer.newLine();
			//String docId = doc.get("id");

		}
		
		writer.close();
	}

}
