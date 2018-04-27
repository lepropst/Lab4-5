import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
public class Lab3 extends JFrame implements ActionListener {
	static final long serialVersionUID = 1;
	JLabel scramletLabel = new JLabel("Scramlet");
	JTextField scramletText = new JTextField(10);
	JButton unscrmbleButton = new JButton("Unscramble");
	JTextArea result = new JTextArea(20,40);
	JScrollPane scroller = new JScrollPane();
	RandomAccessFile indexFile = null;
	RandomAccessFile wordsFile = null;
	public Lab3() {
		setTitle("Scramlet Solver");
		setLayout(new java.awt.FlowLayout());
		setSize(500,430);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(scramletLabel);
		add(scramletText);
		add(unscrmbleButton); unscrmbleButton.addActionListener(this);
		scroller.getViewport().add(result);
		add(scroller);
	}
	public static void main(String[] args) {
		Lab3 display = new Lab3();
		display.setVisible(true);
	}
	void getFiles() {
		String indexName = getFileName("Index File");
		if (indexName == null) {
			result.setText("No index file chosen");
			return;
		}
		String wordsName = getFileName("Words File");
		if (wordsName == null) {
			result.setText("No words file chosen");
			return;
		}
		try {
			indexFile = new RandomAccessFile(indexName, "r");
		} catch (FileNotFoundException e) {
			result.setText("Index file not found");
			return;
		}
		try {
			wordsFile = new RandomAccessFile(wordsName, "r");
		} catch (FileNotFoundException e) {
			result.setText("Words file not found");
			return;
		}
	}
	String getFileName(String title) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(title);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile().getPath();
		else
			return null;
	}
	public void actionPerformed(ActionEvent evt) {
		result.setText(""); //clear TextArea
		if (indexFile == null || wordsFile == null)
			getFiles();
		try {
			unscramble();
		} catch (IOException e) {
			result.setText("I/O eror occurred");
		}
	}
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	void unscramble () throws IOException {
		//get the unscrambled word
		String scramlet = scramletText.getText();
		scramlet = scramlet.trim().toLowerCase();
		//double checks that the entire string is a character/letter
		for(int i=0; i<scramlet.length(); i++) {
			if(!(Character.isLetter(scramlet.charAt(i)))) {
				result.setText("The entered value is not alphabetical, please enter a string of letters.");
			}
		}
		//sees if the string will be short enough to compute efficiently 
		if((int)(scramlet.length())>10) {
			result.setText("A string this long will run too long, please enter a shorter scramlet");
		}
		//creates an array of string permutations to then pass into binary search.
		String[] permutations = perm(scramlet);
	
		//stores a length that can be passed later for binary search as the "high" value.
		int wordsN = (int)(indexFile.length() / 4 - 1);
		/*		 
		 * iterates through the aforementioned array of permutations of the unscrambled word
		 * and puts them through binary search.
		 */
		
		if(permutations.length<1) {
			result.append("Invalid entry");
		} else {
		for	(int i =0; i<permutations.length; i++) {
			//if the binary search returns true (the word is found) and it isn't already in the result, then add it.
			if((binarySearch(permutations[i], 0, wordsN -1 ) >= 0) &&
					(!result.getText().contains(permutations[i] + "\n"))) {
						result.append(permutations[i] + "\n");
			}
			//otherwise, after all the permutations have been tried print "None found" to the user.
			if (!(binarySearch(permutations[i], 0, wordsN -1 ) >= 0)) {
				result.setText("None found");
			}
		}
		} 
	}
	/*
	 *permutation method, simply creates permutations of the given string.
	 */
	String[] perm(String s) {
		if (s.length() == 0) return null;
		String result[] = new String[fact(s.length())];
		if (s.length() == 1)
			result[0] = s;
		else {
			int j = 0;
			for(int i=0; i<s.length(); i++) {
				char c = s.charAt(i);
				String temp = s.substring(0,i) + s.substring(i+1);
				String[] tempPerm = perm(temp);
				for(int k=0; k<tempPerm.length; k++) {
					result[j] = c + tempPerm[k];
					j++;
				}
			}
		}
		return result;
	}
	// Recursive factorial method
	int fact(int i) {
		if(i==0)return 1;
		else return i * fact(i-1);
	}
	/*
	 * Binary method
	 * Will take a string and search the index file by halving it. Checks if the string is equal 
	 * to the string found from the words file.
	 * if it is not then use compareTo to find the direction of search and rerun binary search after adjusting
	 * high and low values.
	 */
	 int binarySearch(String w, int low, int high)
			    throws java.io.IOException
			  {
		 //if the low number is higher than the high number 
			    if (low > high)
			      return -1;
			    //find the middle
			    int mid = (low + high) / 2;
			    //create a empty string
			    String word = "";
			    //seek for the word and read it.
			    indexFile.seek(mid * 4);
			    //begin reading the word index is indicated by start
			    int start = indexFile.readInt();
			    int stop = indexFile.readInt();
			    //starts reading the word from the words file
			    wordsFile.seek(start);
			    //iterates through and reads the characters and adds them to the empty string.
			    for (int i = 0; i < (stop - start) / 2; i++)
			      word = word + wordsFile.readChar();
			    //if it is the word then just return it
			    if (w.equals(word))
			      return mid;
			    //if not then use compareTo to obtain a negative or positive number.
			    //if negative then search the index file before by changing the low and hi values for binary search
			    if (w.compareTo(word) < 0) {
			      return binarySearch(w, low, mid - 1);
			    }
			    //otherwise search the last end of the file.
			    return binarySearch(w, mid + 1, high);
			  }
}