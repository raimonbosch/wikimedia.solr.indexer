package org.github.wikimedia;

import org.github.wikimedia.utils.ShellUtils;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WikiMediaSolrIndexer
{
  DOMParser parser = new DOMParser();
  Document xmlDoc;

  public SolrInputDocument createSolrDocument(String xmlString) throws SAXException, IOException
  {
    parser.parse(new InputSource(new StringReader(xmlString)));
    xmlDoc = parser.getDocument();

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", xmlDoc.getElementsByTagName("id").item(0).getTextContent());
    doc.addField("title", xmlDoc.getElementsByTagName("title").item(0).getTextContent());
    doc.addField("content", xmlDoc.getElementsByTagName("text").item(0).getTextContent());
    doc.addField("contributor", "NULL");

    return doc;
  }

  public void index(String inputPath, String solrConfPath, String solrOutput)
  {
    try{

      //Solr initialization
      int i = 1;
      ShellUtils.exec("rm -rf " + solrOutput);
      ShellUtils.exec("mkdir -p " + solrOutput);
      ShellUtils.exec("cp -r " + solrConfPath + " " + solrOutput);
      System.setProperty("solr.solr.home", solrOutput);
      CoreContainer.Initializer initializer = new CoreContainer.Initializer();

      CoreContainer coreContainer  = initializer.initialize();
      Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
      EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");

      //Read File Line By Line
      String strLine;
      StringBuilder page = new StringBuilder();
      FileInputStream fstream = new FileInputStream(inputPath);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      while ((strLine = br.readLine()) != null) {
        int start = strLine.indexOf(PAGE_START);
        int end = strLine.indexOf(PAGE_END);

        if(start != -1 && end == -1){
          page.delete(0, page.length());
          page.append(strLine.subSequence(start, strLine.length()));
        }
        else if(start == -1 && end != -1)
        {
          page.append(strLine.subSequence(0, end + OFFSET));
          docs.add( createSolrDocument(page.toString()) );
          i++;
        }
        else{
          page.append(strLine.subSequence(0, strLine.length()));
        }

        //ADDING a set of documents to solr embedded server if i % 100 == 0
        if(i % 100 == 0 && docs.size() > 0){
          server.add( docs );
          docs.clear();
        }
      }

      server.add(docs);
      in.close();
      server.commit();
      

      System.out.println("Optimizing...");
      server.optimize();

      if (coreContainer != null) {
        coreContainer.shutdown();
      }

      System.out.println("Indexing completed. Added " + i + " documents at " + solrOutput);

    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public static String PAGE_START = "<page>";
  public static String PAGE_END = "</page>";
  public static int OFFSET = "</page>".length();
}
