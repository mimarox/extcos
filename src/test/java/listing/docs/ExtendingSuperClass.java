package listing.docs;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;

public class ExtendingSuperClass {
	public static void main(String[] args) {
		final Set<Class<? extends SampleBaseClass>> samples = new HashSet<Class<? extends SampleBaseClass>>();

		ComponentScanner scanner = new ComponentScanner();

		scanner.getClasses(new ComponentQuery() {
		    protected void query() {
		        select().from("foo").andStore(
		            thoseExtending(SampleBaseClass.class).into(samples));
		    }
		});
	}
}

class SampleBaseClass {}