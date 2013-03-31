import java.util.ArrayList;


public class Chord extends MusicalElement {

	public ArrayList<Note> chordNotes = new ArrayList<Note>();
	public Note rootNote = null;
	public boolean isTriplet = false;
	
	public Chord(ArrayList<Note> notes) {
		chordNotes = new ArrayList<Note>(notes);
		duration = chordNotes.get(0).duration;
		rootNote = getLowestNote();
	}
	
	public Chord(Note... notes) {
		chordNotes = new ArrayList<Note>();
		for (Note n : notes)
			chordNotes.add(n);
		duration = chordNotes.get(0).duration;
		rootNote = getLowestNote();
	}
	
	public Chord(Chord c) {
		for(Note n : c.chordNotes)
			chordNotes.add(new Note(n));
		duration = c.duration;
		rootNote = c.rootNote;
		isTriplet = c.isTriplet;
	}
	
	/**
	 * Invert the chord the specified number of times.
	 * @param inversions Pos number to invert up, Neg number to invert down.
	 */
	public void invert(int inversions) {
		int curInvs = 0;
		Note highestNote = getHighestNote();
		
		if (inversions > 0) {
			while (curInvs < inversions) {
				while(rootNote.octave < highestNote.octave
						|| (rootNote.octave == highestNote.octave && rootNote.pitch <= highestNote.pitch))
					rootNote.octave += 1;
				highestNote = rootNote;
				rootNote = getLowestNote();
				curInvs++;
			}
		}
		else {
			while (curInvs > inversions) {
				while(highestNote.octave > rootNote.octave
						|| (rootNote.octave == highestNote.octave && highestNote.pitch >= rootNote.pitch))
					highestNote.octave -= 1;
				rootNote = highestNote;
				highestNote = getHighestNote();
				curInvs--;
			}
		}
	}
	
	private Note getLowestNote() {
		Note lowest = chordNotes.get(0);
		for(int i=1;i<chordNotes.size();i++) {
			Note n = chordNotes.get(i);
			if (n.octave < lowest.octave)
				lowest = n;
			if (n.octave == lowest.octave && n.pitch < lowest.pitch)
				lowest = n;
		}
		return lowest;
	}
	
	private Note getHighestNote() {
		Note highest = chordNotes.get(0);
		for(int i=1;i<chordNotes.size();i++) {
			Note n = chordNotes.get(i);
			if (n.octave > highest.octave)
				highest = n;
			if (n.octave == highest.octave && n.pitch > highest.pitch)
				highest = n;
		}
		return highest;
	}
	
	@Override
	public String toString() {
		String retString = "";

		if (!isTriplet) {
			retString += "<";

			for(Note n : chordNotes) {
				String noteStr = n.toString();
				retString += noteStr.substring(0,noteStr.length()-1)+" ";
			}

			retString += ">"+duration;
		}
		else {
			retString += "\\times 2/3 { ";
			for(Note n : chordNotes) {
				String noteStr = n.toString();
				retString += noteStr.substring(0,noteStr.length()-1)+" ";
			}
			retString += "}";
		}
		return retString;
	}

}
