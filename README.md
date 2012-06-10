Wikimedia Solr indexer
======================

Solr indexer example using articles from wikipedia


This is an example to creare a Solr index with Wikipedia's documents.

You can download the Wikipedia's articles from:

1/ http://download.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2 (large version)

2/ http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles1.xml-p000000010p000010000.bz2 (small version)

You will need maven to compile the sources or hack the project to include the libraries manually.

Compilation example:

cd $HOME/wikimediaIndexer
mvn clean install

Execution example:

java -jar target/wmindexer-1.0-SNAPSHOT-jar-with-dependencies.jar --input=$HOME/wikimediaIndexer/enwiki-latest-pages-articles1.xml-p000000010p000010000 --solr=$HOME/wikimediaIndexer/src/main/resources/solr --output=/tmp/solr


This is a prototype, possible features for a real indexer from XML can be:
- Parallel indexation using Hadoop.
- Creation of solr shards.
- Read XML with a buffer instead of doing it line by line.
- Full performance profiling to be as fast as possible.
- Customization of the solr fields to prepare facets, special tokenizations, more like this, site collapsing, clustering, etc ...