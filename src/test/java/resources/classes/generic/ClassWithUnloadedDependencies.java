package resources.classes.generic;

import javax.swing.JPanel;

public class ClassWithUnloadedDependencies {
	@SuppressWarnings("unused")
	private JPanel panel = new JPanel();
}
