package resources.all.test.classes.in.use.generic;

import javax.swing.JPanel;

public class ClassWithUnloadedDependencies {
	@SuppressWarnings("unused")
	private JPanel panel = new JPanel();
}
