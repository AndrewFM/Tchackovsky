import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;


public class Generator {
	
	public final static int I = 0;
	public final static int II = 1;
	public final static int III = 2;
	public final static int IV = 3;
	public final static int V = 4;
	public final static int VI = 5;
	public final static int VII = 6;
	
	public final static int TREBLE = 0;
	public final static int BASS = 1; 
	
	public ArrayList<MusicalElement> treblenotes = new ArrayList<MusicalElement>();
	public ArrayList<MusicalElement> bassnotes = new ArrayList<MusicalElement>();

	private String fileName = "test.ly";
	private Note.Key keySignature = Note.randKey.random();
	
	private int tempo = 200;
	private String title = "";
	
	public void generate() {
		int curTrans = I;
		title = generateTitle();
		
		ArrayList<MusicalElement> allElem = new ArrayList<MusicalElement>();
		ArrayList<MusicalElement> elem = new ArrayList<MusicalElement>();
		generateMotive(elem);
		ArrayList<MusicalElement> elem2 = new ArrayList<MusicalElement>();
		generateMotive(elem2);
		
		boolean breaknext = false;
		while(true) {
			ArrayList<MusicalElement> useElem = elem;
			double rand = Math.random();
			if (rand < 0.5)
				useElem = elem2;
			
			ArrayList<MusicalElement> workingMot = transposeMotive(curTrans, useElem);
			rand = Math.random();
			if (rand < 0.25)
				workingMot = speedUpMotive(workingMot);
			else if (rand < 0.5)
				workingMot = slowDownMotive(workingMot);
			
			allElem.addAll(workingMot);
			curTrans = nextChord(curTrans);
			
			if (breaknext)
				break;
			if (curTrans == I) {
				rand = Math.random();
				if (rand < 0.5)
					breaknext = true;
			}
		}
		
		//Extend last note
		MusicalElement me = allElem.get(allElem.size()-1);
		if (me instanceof Note) {
			Note n = (Note)me;
			n.duration = 1;
		}
		if (me instanceof Chord) {
			Chord c = (Chord)me;
			c.duration = 1;
		}
		
		multiClef(allElem);
		//compressRests(treblenotes);
		//compressRests(bassnotes);
	}
	
	public String generateTitle() {
		String title = "HackRU ";
		double rand = Math.random();
		if (rand < 0.5)
			title += "Op. "+(int)Math.ceil(Math.random()*99)+", No. "+(int)Math.ceil(Math.random()*5);
		else
			title += "in "+keyToTitle(keySignature);
		return title;
	}
	
	
	
	public ArrayList<MusicalElement> slowDownMotive(ArrayList<MusicalElement> eles)
	{		
		ArrayList<MusicalElement> elementList = new ArrayList<MusicalElement>(eles);
		
		for(int i=0; i<elementList.size(); i++) {
			MusicalElement me = elementList.get(i);
			
			if (me instanceof Note) {
				Note n = new Note((Note)me);
				if (n.duration != 1)
				n.duration /= 2;
				elementList.set(i,n);
			}
			else if (me instanceof Chord) {
				Chord c = new Chord((Chord)me);
				for(Note n : c.chordNotes) {
					if (n.duration != 1)
						n.duration /= 2;
				}
				elementList.set(i,c);
			}
		}
		
		return elementList;
	}
	
	public ArrayList<MusicalElement> speedUpMotive(ArrayList<MusicalElement> eles)
	{		
		ArrayList<MusicalElement> elementList = new ArrayList<MusicalElement>(eles);
		
		for(int i=0; i<elementList.size(); i++) {
			MusicalElement me = elementList.get(i);
			
			if (me instanceof Note) {
				Note n = new Note((Note)me);
				if (n.duration < 8)
					n.duration *= 2;
				elementList.set(i,n);
			}
			else if (me instanceof Chord) {
				Chord c = new Chord((Chord)me);
				for(Note n : c.chordNotes) {
					n.duration *= 2;
				}
				elementList.set(i,c);
			}
		}
		
		return elementList;
	}
	
	/**
	 * Transpose all of the notes in the given musical element list up to
	 * a new key relative to the old one by pitchShift.
	 */
	public ArrayList<MusicalElement> transposeMotive(int pitchShift, ArrayList<MusicalElement> eles)
	{		
		ArrayList<MusicalElement> elementList = new ArrayList<MusicalElement>(eles);
		
		for(int i=0; i<elementList.size(); i++) {
			MusicalElement me = elementList.get(i);
			
			if (me instanceof Note) {
				Note n = new Note((Note)me);
				n.pitch += pitchShift;
				while (n.pitch > 6) {
					n.pitch -= 7;
					n.octave += 1;
				}
				while (n.pitch < 0) {
					n.pitch += 7;
					n.octave -= 1;
				}
				if (pitchShift != 0)
					n.setKey(pitchToKey((rootToPitch(I,n.key)+pitchShift)%7, n.key));
				elementList.set(i,n);
			}
			else if (me instanceof Chord) {
				Chord c = new Chord((Chord)me);
				for(Note n : c.chordNotes) {
					n.pitch += pitchShift;
					while (n.pitch > 6) {
						n.pitch -= 7;
						n.octave += 1;
					}
					while (n.pitch < 0) {
						n.pitch += 7;
						n.octave -= 1;
					}
					if (pitchShift != 0)
						n.setKey(pitchToKey((rootToPitch(I,n.key)+pitchShift)%7, n.key));
				}
				elementList.set(i,c);
			}
		}
		
		return elementList;
	}
	
	public void generateMotive(ArrayList<MusicalElement> elementList) {		
		//TODO: Random rhythm! Not just half notes all the time.
		//TODO: We can randomize the starting octave.
		//Start with a tonic chord
		boolean nonTonicEncountered = false; //Used to require at least one pre-dom note.
		boolean lastAppogg = false; //Existence of an appoggiatura increases chance of successive ones.
		Note root = new Note(rootToPitch(I,keySignature), 3, 2, keySignature);
		Chord c = new Chord(root, Note.shiftINote(root, 2), Note.shiftINote(root, 4));
		int curRoot = I;
		
		addChord(c, elementList);
		Chord lastChord = c;
		
		while(true) {
			int cDuration = 2;
			double rand = Math.random();
			int nextRoot = nextChord(curRoot);
			if (nextRoot != I)
				nonTonicEncountered = true;
			root = new Note(rootToPitch(nextRoot,keySignature), 4, cDuration, keySignature);
			
			//Let V chords have a chance of being 7th-chords.
			if (nextRoot == V && rand < 0.4) {
				c = new Chord(root, Note.shiftINote(root, 2), Note.shiftINote(root, 4), Note.shiftINote(root, 6));
				rand = Math.random();
			}
			else
				c = new Chord(root, Note.shiftINote(root, 2), Note.shiftINote(root, 4));

			//Random chord inversions.
			if (rand < 0.5) {
				rand = Math.random();
				if (rand < 0.5)
					c.invert((int)Math.ceil(Math.random()*(double)(c.chordNotes.size()-1)));
				else
					c.invert((int)-Math.ceil(Math.random()*(double)(c.chordNotes.size()-1)));
				Math.random();
			}
			
			//If both chords are the same root, we can add a Neighbor Tone.
			if (curRoot == nextRoot) {
				if (rand < 0.75) {
					rand = Math.random();
					Note nt = null;
					if (rand < 0.5)
						nt = new Note(rootToPitch(curRoot+1,keySignature), 4, cDuration*2, keySignature);
					else
						nt = new Note(rootToPitch(curRoot-1,keySignature), 4, cDuration*2, keySignature);
					addNote(nt, elementList);
				}
				rand = Math.random();
			}
			
			//If the two chords are <= 3 pitches of each other, we can add Passing Tone(s).
			int pitchDiff = c.rootNote.pitchDifference(lastChord.rootNote);
			if (Math.abs(pitchDiff) <= 3) {
				if (rand < 0.5) {
					int reduction = 2;
					Note pt = null;
					Note pt2 = null;
					
					if (pitchDiff == 3) {
						reduction = 4;
						if (pitchDiff < 0)
							pt2 = new Note(rootToPitch(curRoot-2,keySignature), 4, cDuration*reduction, keySignature);
						else
							pt2 = new Note(rootToPitch(curRoot+2,keySignature), 4, cDuration*reduction, keySignature);
					}
					if (pitchDiff < 0)
						pt = new Note(rootToPitch(curRoot-1,keySignature), 4, cDuration*reduction, keySignature);
					else
						pt = new Note(rootToPitch(curRoot+1,keySignature), 4, cDuration*reduction, keySignature);
				
					addNote(pt, elementList);
					
					if (pt2 != null) {
						addNote(pt2, elementList);
					}
					
					rand = Math.random();
					if (rand < 0.5)
						c.duration *= reduction;
					else if (!lastAppogg)
						lastChord.duration *= reduction;
				}
				rand = Math.random();
			}
			
			curRoot = nextRoot;
			//Only end the phrase on a Tonic chord.
			if (curRoot == I && nonTonicEncountered) {
				if (rand < 0.75) {
					rand = Math.random();
					c.duration = (int)Math.pow(2, (int)Math.floor(Math.random()*4));
					addChord(c, elementList);
					break;
				}
			}
			
			rand = Math.random();
			if (rand < 0.35 && lastAppogg == false) {
				addAppogg(c, cDuration*(int)Math.pow(2, 1+(int)Math.floor(Math.random()*2)), elementList);
				lastAppogg = true;
			}
			else if (rand < 0.55 && lastAppogg == true) {
				addAppogg(c, cDuration*(int)Math.pow(2, 1+(int)Math.floor(Math.random()*2)), elementList);
				lastAppogg = true;				
			}
			else {
				addChord(c, elementList);
				lastAppogg = false;
			}
			lastChord = c;
		}
	}
	
	/*
	 * Randomly pick the next type of chord to transition to.
	 */
	private int nextChord(int root) {
		int newRoot = root;
		double rand = Math.random();
		
		//Pre-Dominants should move amongst themselves or move to the dominant.
		if (root == II || root == IV || root == VI || root == III) {
			//Move amongst self.
			if (rand < 0.5) {
				rand = Math.random();
				if (rand < 0.3)
					newRoot = VI;
				else if (rand < 0.6)
					newRoot = IV;
				else if (rand < 0.9)
					newRoot = II;
				else
					newRoot = III;
			}
			//Move to dominant.
			else if (rand < 0.9) {
				rand = Math.random();
				if (rand < 0.25)
					newRoot = VII;
				else
					newRoot = V;
			}
			//Plagal moves from pre-dominant to the tonic.
			else 
				newRoot = I;
		}
		//Dominants should move to the tonic.
		else if (root == V || root == VII) {
			//Move to the tonic.
			if (rand < 0.6)
				newRoot = I;
			//Move to another dominant.
			else if (rand < 0.85) {
				rand = Math.random();
				if (rand < 0.25)
					newRoot = VII;
				else
					newRoot = V;
			}
			//Fake-out: Move back to pre-dominant.
			else 
				newRoot = VI;
		}
		//Tonic should end the phrase, or move to predominant(?)
		else if (root == I) {
			//Move to another tonic.
			if (rand < 0.25)
				newRoot = I;
			//Move to a Pre-dominant.
			else {
				rand = Math.random();
				if (rand < 0.3)
					newRoot = VI;
				else if (rand < 0.6)
					newRoot = IV;
				else if (rand < 0.9)
					newRoot = II;
				else
					newRoot = III;
			}
		}
		
		return newRoot;
	}
	
	private void multiClef(ArrayList<MusicalElement> elementList) {
		for(MusicalElement me : elementList) {
			if (me instanceof Note) {
				Note n = (Note)me;
				if (n.octave < 4) {
					bassnotes.add(n);
					treblenotes.add(new Rest(n.duration));
				}
				else {
					treblenotes.add(n);
					bassnotes.add(new Rest(n.duration));
				}
			}
			else if (me instanceof Chord) {
				Chord c = (Chord)me;
				if (c.chordNotes.get(0).octave < 4) {
					bassnotes.add(c);
					treblenotes.add(new Rest(c.duration));
				}
				else {
					treblenotes.add(c);
					bassnotes.add(new Rest(c.duration));
				}
			}
			else {
				treblenotes.add(me);
				bassnotes.add(me);
			}
		}
	}
	
	/**
	 * Take a relative roman numeral description of a note, and return
	 * it's pitch value in a specific key.
	 */
	public int rootToPitch(int root, Note.Key key) {
		if (key == Note.Key.AFLATMAJOR || key == Note.Key.AMAJOR || key == Note.Key.AMINOR)
			return (Note.A + root) % 7;
		if (key == Note.Key.BFLATMAJOR || key == Note.Key.BFLATMINOR || key == Note.Key.BMAJOR || key == Note.Key.BMINOR)
			return (Note.B + root) % 7;
		if (key == Note.Key.CMAJOR || key == Note.Key.CMINOR || key == Note.Key.CSHARPMINOR)
			return (Note.C + root) % 7;
		if (key == Note.Key.DFLATMAJOR || key == Note.Key.DMAJOR || key == Note.Key.DMINOR)
			return (Note.D + root) % 7;
		if (key == Note.Key.EFLATMAJOR || key == Note.Key.EFLATMINOR || key == Note.Key.EMAJOR || key == Note.Key.EMINOR)
			return (Note.E + root) % 7;
		if (key == Note.Key.FMAJOR || key == Note.Key.FMINOR || key == Note.Key.FSHARPMINOR)
			return (Note.F + root) % 7;
		if (key == Note.Key.GFLATMAJOR || key == Note.Key.GMAJOR || key == Note.Key.GMINOR || key == Note.Key.GSHARPMINOR)
			return (Note.G + root) % 7;
		return 0;
	}
	
	public Note.Key pitchToKey(int pitchInC, Note.Key compareKey) {
		boolean isMinor = false;
		boolean isMajor = true;
		
		if (compareKey == Note.Key.AMINOR || compareKey == Note.Key.BFLATMINOR
				|| compareKey == Note.Key.BMINOR || compareKey == Note.Key.CMINOR
				|| compareKey == Note.Key.CSHARPMINOR || compareKey == Note.Key.DMINOR
				|| compareKey == Note.Key.EFLATMINOR || compareKey == Note.Key.EMINOR
				|| compareKey == Note.Key.FMINOR || compareKey == Note.Key.FSHARPMINOR
				|| compareKey == Note.Key.GMINOR || compareKey == Note.Key.GSHARPMINOR)
		{
			isMinor = true;
			isMajor = false;
		}
		
		if (pitchInC == Note.A && isMinor)
			return Note.Key.AMINOR;
		if (pitchInC == Note.A && isMajor)
			return Note.Key.AMAJOR;
		if (pitchInC == Note.B && isMinor)
			return Note.Key.BMINOR;
		if (pitchInC == Note.B && isMajor)
			return Note.Key.BMAJOR;
		if (pitchInC == Note.C && isMinor)
			return Note.Key.CMINOR;
		if (pitchInC == Note.C && isMajor)
			return Note.Key.CMAJOR;
		if (pitchInC == Note.D && isMinor)
			return Note.Key.DMINOR;
		if (pitchInC == Note.D && isMajor)
			return Note.Key.DMAJOR;
		if (pitchInC == Note.E && isMinor)
			return Note.Key.EMINOR;
		if (pitchInC == Note.E && isMajor)
			return Note.Key.EMAJOR;
		if (pitchInC == Note.F && isMinor)
			return Note.Key.FMINOR;
		if (pitchInC == Note.F && isMajor)
			return Note.Key.FMAJOR;
		if (pitchInC == Note.G && isMinor)
			return Note.Key.GMINOR;
		if (pitchInC == Note.G && isMajor)
			return Note.Key.GMAJOR;
		return Note.Key.CMAJOR;
	}
	
	/**
	 * Output to File
	 */
	public void save() {
		PrintWriter data;
		try {
			data = new PrintWriter(fileName, "UTF-8");

			//Header
			data.println("\\version \"2.12.3\"");
			data.println("\\header {");
			data.println("title = \""+title+"\"");
			data.println("}");

			//Body
			data.println("\\score {");
			data.println("{");
			data.println("\\time 4/4");
			data.println("\\tempo 4="+tempo);
			
			data.println("\\new PianoStaff <<");
			data.println("\\new Staff { \\clef treble ");
			data.println(Note.keyToString(keySignature));
			for(MusicalElement n : treblenotes) {
				data.println(n.toString());
			}
			data.println("}");
			data.println("\\new Staff { \\clef bass ");
			data.println(Note.keyToString(keySignature));
			for(MusicalElement n : bassnotes) {
				data.println(n.toString());
			}
			data.println("}");
			data.println(">>");
			
			data.println("}");
			data.println("\\layout { }");
			data.println("\\midi { }");
			data.println("}");

			data.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void addNote(Note n, ArrayList<MusicalElement> elementList) {
		elementList.add(n);
		System.out.println("Adding: "+n.toString());
	}
	
	public void addChord(Chord c, ArrayList<MusicalElement> elementList) {
		elementList.add(c);
		System.out.println("Adding: "+c.toString());
	}
	
	public void addAppogg(Chord c, int duration, ArrayList<MusicalElement> elementList) {
		for(Note n : c.chordNotes) {
			n.duration = duration;
			elementList.add(n);
			System.out.println("Adding: "+n.toString());
		}
	}
	
	/**
	 * Combine consecutive rests together.
	 */
	public void compressRests(ArrayList<MusicalElement> list) {
		float curBeat = 0;
		boolean lastRest = false;
		
		for(int i=0; i<list.size(); i++) {
			MusicalElement me = list.get(i);
			int remDuration = me.duration;
			
			if (me instanceof Rest) {
				if (lastRest == false || curBeat == 0)
					lastRest = true;
				else {
					me.duration = (int)(1.0f/(1.0f/(float)me.duration+1.0f/(float)list.get(i-1).duration));
					list.remove(i-1);
					i--;
				}
			}
			else
				lastRest = false;
			
			curBeat += 1.0f/(float)remDuration;
			if (curBeat >= 1) //Assuming 4/4, so 4 beats per measure.
				curBeat = 0;
		}
	}
	
	/**
	 * Execute LilyPond and open generated PDF file
	 */
	public void execute() {
		try {
			System.out.println("Creating PDF and MIDI Files... please wait...");
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("lilypond "+fileName);

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line=null;

			while((line=input.readLine()) != null) {
				System.out.println(line);
			}
			pr.waitFor();

			File pdfFile = new File(fileName.substring(0, fileName.lastIndexOf("."))+".pdf");
			if (pdfFile.exists()) {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(pdfFile);
				} else {
					System.out.println("Awt Desktop is not supported! Cannot open PDF file.");
				}
			}

			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private String keyToTitle(Note.Key key) {
		switch(key) {
		case AMINOR:
			return "A Minor";
		case AFLATMAJOR:
			return "Ab Major";
		case AMAJOR:
			return "A Major";
		case BFLATMAJOR:
			return "Bb Major";
		case BFLATMINOR:
			return "Bb Minor";
		case BMAJOR:
			return "B Major";
		case BMINOR:
			return "B Minor";
		case CMAJOR:
			return "C Major";
		case CMINOR:
			return "C Minor";
		case CSHARPMINOR:
			return "C# Minor";
		case DFLATMAJOR:
			return "Db Major";
		case DMAJOR:
			return "D Major";
		case DMINOR:
			return "D Minor";
		case EFLATMAJOR:
			return "Eb Major";
		case EFLATMINOR:
			return "Eb Minor";
		case EMAJOR:
			return "E Major";
		case EMINOR:
			return "E Minor";
		case FMAJOR:
			return "F Major";
		case FMINOR:
			return "F Minor";
		case FSHARPMINOR:
			return "F# Minor";
		case GFLATMAJOR:
			return "Gb Major";
		case GMAJOR:
			return "G Major";
		case GMINOR:
			return "G Minor";
		case GSHARPMINOR:
			return "G# Minor";
		default:
			return "C Major";
		}
	}
	
	public static class RandomEnum<E extends Enum> {

		private static final Random RND = new Random();
		private final E[] values;

		public RandomEnum(Class<E> token) {
			values = token.getEnumConstants();
		}

		public E random() {
			return values[RND.nextInt(values.length)];
		}
	}
}
