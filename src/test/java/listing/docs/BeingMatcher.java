package listing.docs;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;

public class BeingMatcher {
	public static void main(String[] args) {
		final Set<Class<?>> samples = new HashSet<Class<?>>();

		ComponentScanner scanner = new ComponentScanner();

		scanner.getClasses(new ComponentQuery() {
		    protected void query() {
		        select().from("foo").andStore(
		            thoseBeing(and(subclassOf(SampleBaseClass.class),
		            		and(subclassOf(SampleBaseClass.class)))).into(samples));
		    }
		});
	}
}