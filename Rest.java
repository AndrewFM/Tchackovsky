public class Rest extends MusicalElement {
	
	public Rest(int duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		return "r"+duration;
	}

}
