
public class Program {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		

		System.out.println("Starting Generation...");
		Generator g = new Generator();

		g.generate();
		g.save();
		g.execute();
	}

}