
public class Note extends MusicalElement implements Comparable<Note> {

	public enum Accidental {
		SHARP, NATURAL, FLAT, NONE
	};
	public static enum Key {
		AMAJOR, BMAJOR, CMAJOR, DMAJOR, EMAJOR, FMAJOR, GMAJOR,
		AMINOR, BMINOR, CMINOR, DMINOR, EMINOR, FMINOR, GMINOR,
		BFLATMAJOR, EFLATMAJOR, AFLATMAJOR, DFLATMAJOR, GFLATMAJOR,
		BFLATMINOR, EFLATMINOR, FSHARPMINOR, CSHARPMINOR, GSHARPMINOR
	};
	public static final Generator.RandomEnum<Key> randKey =
	        new Generator.RandomEnum<Key>(Key.class);
	
	public static final int C = 0;
	public static final int D = 1;
	public static final int E = 2;
	public static final int F = 3;
	public static final int G = 4;
	public static final int A = 5;
	public static final int B = 6;
	public static final int SHARP = 0;
	public static final int FLAT = 1;
	
	public int[] keypitches;
	public int keyacc;
	
	public int pitch;
	public int octave;
	public Accidental acc;
	public Key key;
	
	public Note (int pitch, int octave, int duration, Key key) {
		setKey(key);
		this.pitch = pitch;
		this.duration = duration;
		this.octave = octave;
		acc = Accidental.NONE;
	}
	
	public Note (int pitch, int octave, int duration, Accidental acc, Key key) {
		setKey(key);
		this.pitch = pitch;
		this.duration = duration;
		this.octave = octave;
		this.acc = acc;
	}
	
	public Note (Note n) {
		setKey(n.key);
		pitch = n.pitch;
		octave = n.octave;
		acc = n.acc;
		duration = n.duration;
	}
	
	public void setKey(Key key) {
		this.key = key;
		switch(key) {
		case AMAJOR:
			keypitches = new int[3];
			keypitches[0] = F; keypitches[1] = C; keypitches[2] = G;
			keyacc = SHARP;
			break;
		case AFLATMAJOR:
			keypitches = new int[4];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keypitches[3] = D;
			keyacc = FLAT;
			break;
		case AMINOR:
			keypitches = new int[0];
			keyacc = SHARP;
			break;
		case BFLATMAJOR:
			keypitches = new int[2];
			keypitches[0] = B; keypitches[1] = E;
			keyacc = FLAT;
			break;
		case BFLATMINOR:
			keypitches = new int[5];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keypitches[3] = D; keypitches[4] = G;
			keyacc = FLAT;
			break;
		case BMAJOR:
			keypitches = new int[5];
			keypitches[0] = F; keypitches[1] = C; keypitches[2] = G; 
			keypitches[3] = D; keypitches[4] = A;
			keyacc = SHARP;
			break;
		case BMINOR:
			keypitches = new int[2];
			keypitches[0] = F; keypitches[1] = C;
			keyacc = SHARP;
			break;
		case CMAJOR:
			keypitches = new int[0];
			keyacc = SHARP;
			break;
		case CMINOR:
			keypitches = new int[3];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keyacc = FLAT;
			break;
		case CSHARPMINOR:
			keypitches = new int[4];
			keypitches[0] = F; keypitches[1] = C; keypitches[2] = G; 
			keypitches[3] = D;
			keyacc = SHARP;
			break;
		case DFLATMAJOR:
			keypitches = new int[5];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keypitches[3] = D; keypitches[4] = G;
			keyacc = FLAT;
			break;
		case DMAJOR:
			keypitches = new int[2];
			keypitches[0] = F; keypitches[1] = C;
			keyacc = SHARP;
			break;
		case DMINOR:
			keypitches = new int[1];
			keypitches[0] = B;
			break;
		case EFLATMAJOR:
			keypitches = new int[3];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keyacc = FLAT;
			break;
		case EFLATMINOR:
			keypitches = new int[6];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keypitches[3] = D; keypitches[4] = G; keypitches[5] = C; 
			keyacc = FLAT;
			break;
		case EMAJOR:
			keypitches = new int[4];
			keypitches[0] = F; keypitches[1] = C; keypitches[2] = G; 
			keypitches[3] = D;
			keyacc = SHARP;
			break;
		case EMINOR:
			keypitches = new int[1];
			keypitches[0] = F;
			keyacc = SHARP;
			break;
		case FMAJOR:
			keypitches = new int[1];
			keypitches[0] = B;
			keyacc = FLAT;
			break;
		case FMINOR:
			keypitches = new int[4];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keypitches[3] = D; 
			keyacc = FLAT;
			break;
		case FSHARPMINOR:
			keypitches = new int[3];
			keypitches[0] = F; keypitches[1] = C; keypitches[2] = G; 
			keyacc = SHARP;
			break;
		case GFLATMAJOR:
			keypitches = new int[6];
			keypitches[0] = B; keypitches[1] = E; keypitches[2] = A; 
			keypitches[3] = D; keypitches[4] = G; keypitches[5] = C; 
			keyacc = FLAT;
			break;
		case GMAJOR:
			keypitches = new int[1];
			keypitches[0] = F;
			keyacc = SHARP;
			break;
		case GMINOR:
			keypitches = new int[2];
			keypitches[0] = B; keypitches[1] = E;
			keyacc = FLAT;
			break;
		case GSHARPMINOR:
			keypitches = new int[5];
			keypitches[0] = F; keypitches[1] = C; keypitches[2] = G; 
			keypitches[3] = D; keypitches[4] = A;
			keyacc = SHARP;
			break;
		default:
			break;
		}
	}

	/**
	 * Return a new note shifted up or down by the desired number of
	 * whole pitches.
	 */
	public static Note shiftINote(Note n, int pitches) {
		Note ret = new Note(n);
		
		ret.pitch += pitches;
		while (ret.pitch > 6) {
			ret.pitch -= 7;
			ret.octave++;
		}
		while (ret.pitch < 0) {
			ret.pitch += 7;
			ret.octave--;
		}
		
		return ret;
	}
	
	public boolean isMemberOfKey() {
		for(int i=0; i<keypitches.length; i++) {
			if (keypitches[i] == pitch) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String returnStr = "";
		
		switch(pitch) {
		case A:
			returnStr += "a"; break;
		case B:
			returnStr += "b"; break;
		case C:
			returnStr += "c"; break;
		case D:
			returnStr += "d"; break;
		case E:
			returnStr += "e"; break;
		case F:
			returnStr += "f"; break;
		case G:
			returnStr += "g"; break;
		}
		
		//Is in key signature?
		boolean inKey = isMemberOfKey();
		
		if (acc == Accidental.SHARP) {
			if (inKey && keyacc == SHARP)
				returnStr += "isis";
			else if (inKey)
				returnStr += "is";
		}
		else if (acc == Accidental.FLAT) {
			if (inKey && keyacc == FLAT)
				returnStr += "eses";
			else if (inKey)
				returnStr += "es";
		}
		else if (acc == Accidental.NONE) {
			if (inKey && keyacc == FLAT)
				returnStr += "es";
			else if (inKey && keyacc == SHARP)
				returnStr += "is";
		}
			
		switch(octave) {
		case 0:
			returnStr += ",,,"; break;
		case 1:
			returnStr += ",,"; break;
		case 2:
			returnStr += ","; break;
		case 4:
			returnStr += "'"; break;
		case 5:
			returnStr += "''"; break;
		case 6:
			returnStr += "'''"; break;
		case 7:
			returnStr += "''''"; break;
		}
		
		returnStr += duration;
		
		return returnStr;
	}

	public static String keyToString(Key key) {
		switch (key) {
		case AMAJOR:
			return "\\key a \\major";
		case AFLATMAJOR:
			return "\\key aes \\major";
		case AMINOR:
			return "\\key a \\minor";
		case BFLATMAJOR:
			return "\\key bes \\major";
		case BFLATMINOR:
			return "\\key bes \\minor";
		case BMAJOR:
			return "\\key b \\major";
		case BMINOR:
			return "\\key b \\minor";
		case CMAJOR:
			return "\\key c \\major";
		case CMINOR:
			return "\\key c \\minor";
		case CSHARPMINOR:
			return "\\key cis \\minor";
		case DFLATMAJOR:
			return "\\key des \\major";
		case DMAJOR:
			return "\\key d \\major";
		case DMINOR:
			return "\\key d \\minor";
		case EFLATMAJOR:
			return "\\key ees \\major";
		case EFLATMINOR:
			return "\\key ees \\minor";
		case EMAJOR:
			return "\\key e \\major";
		case EMINOR:
			return "\\key e \\minor";
		case FMAJOR:
			return "\\key f \\major";
		case FMINOR:
			return "\\key f \\minor";
		case FSHARPMINOR:
			return "\\key fis \\minor";
		case GFLATMAJOR:
			return "\\key ges \\major";
		case GMAJOR:
			return "\\key g \\major";
		case GMINOR:
			return "\\key g \\minor";
		case GSHARPMINOR:
			return "\\key gis \\minor";
		default:
			break;
		}
		
		return "";
	}

	/**
	 * Returns the number of pitches difference between the two notes.
	 * If the passed note is lower than this note, the returned value is negative.
	 */
	public int pitchDifference(Note n) {
		if (n.octave == octave) 
			return n.pitch - pitch;
		else if (n.octave > octave)
			return n.pitch + (7-pitch);
		else 
			return pitch + (7-n.pitch);
	}
	
	@Override
	public int compareTo(Note n) {
		if (n.octave < octave || (n.octave == octave && n.pitch < pitch))
			return -1;
		else if (n.octave > octave || (n.octave == octave && n.pitch > pitch))
			return 1;
		else
			return 0;
	}
}
