package resources.classes.enums;

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
