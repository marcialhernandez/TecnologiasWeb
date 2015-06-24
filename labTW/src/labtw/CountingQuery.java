/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labtw;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author Marcial
 */
public class CountingQuery extends CustomScoreQuery {

	public CountingQuery(Query subQuery) {
		super(subQuery);
	}
	
	
	public class CountingQueryScoreProvider extends CustomScoreProvider {

		String _field;
		
		public CountingQueryScoreProvider(String field, LeafReaderContext context) {
			super(context);
			_field = field;
		}
	
		// Rescores by counting the number of terms in the field
                @Override
		public float customScore(int doc, float subQueryScore, float valSrcScores[]) throws IOException {
			IndexReader r = context.reader();
			Terms tv = r.getTermVector(doc, _field);
			TermsEnum termsEnum = null;
			termsEnum = tv.iterator(termsEnum);
		    int numTerms = 0;
			while((termsEnum.next()) != null) {
		    	numTerms++;
		    }
			return (float)(numTerms);
		}

	
	}
	
        @Override
	protected CustomScoreProvider getCustomScoreProvider(
			LeafReaderContext context) throws IOException {
		return new CountingQueryScoreProvider("tag", context);
	}
	
	

}


//LeafReaderContext context