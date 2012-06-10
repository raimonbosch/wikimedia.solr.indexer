package org.github.wikimedia;

import org.github.wikimedia.utils.CommandLineHelper;
import org.apache.commons.cli.ParseException;

public class EntryPoint
{
  public static void main(String[] args) throws ParseException
  {
    CommandLineHelper helper = new CommandLineHelper(args);

    helper.setRequiredOption("input", "input", true, "input");
    helper.setRequiredOption("solr", "solr", true, "solr");
    helper.setRequiredOption("output", "output", true, "output");
    //helper.setOption("contributor", "contributor", true, "contributor");
    helper.parsePosix();

    String inputPath = helper.getOptionValue("input");
    String solrConfPath = helper.getOptionValue("solr");
    String solrOutput = helper.getOptionValue("output");

    WikiMediaSolrIndexer indexer = new WikiMediaSolrIndexer();
    indexer.index(inputPath, solrConfPath, solrOutput);
  }
}
