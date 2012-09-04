package resources.all.test.classes.in.use.enums;

public enum MyTestEnum {
	One {
		@SuppressWarnings("unused")
		public void test() {
			class OneClass {
				public OneClass() {
					super();
				}
			}
		}
	},
	Two, Three
}
