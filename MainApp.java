package ie.tcd.singhr3;

public class MainApp {

	public static void main(String[] args) throws Exception {
		CreateIndex indexer = new CreateIndex();
        CreateSearch searcher = new CreateSearch();
        ClassicSimilaritySearch classicsearcher = new ClassicSimilaritySearch();
        LMDSearch lmd = new LMDSearch();

        indexer.indexing();
        searcher.querySearch();
        classicsearcher.querySearch();
        lmd.querySearch();
	}
}
