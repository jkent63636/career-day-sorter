//Thomas Varano
//Information Team
//Nov 9, 2018

package com.atcs.career.io.importexport;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.atcs.career.program.ErrorManager;

public class CSVReader {

	public static void main(String[] args) {
		// URL importer = CSVReader.class.getResource("ExportLocation.scpt");
		// try {
		// System.out.println(ScriptInterpreter.getProcessValues(new
		// ProcessBuilder("osascript", importer.getPath()))[0]);
		// } catch (IOException | InterruptedException e) {
		// e.printStackTrace();
		// }

		readCSV("src/com/atcs/career/io/SampleData.csv");
		// ArrayList<String[]> read =
		// readCSV("/Users/mreineke20/Desktop/CareerSampleData.csv");
	}

	public static ArrayList<String[]> readCSV(String fileName) {
		ArrayList<String[]> lines = new ArrayList<String[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
			   boolean checkingForCommas = false;
			   for(int i = 0; i < line.length(); i++) {
			      if(line.charAt(i) == '"')
			         checkingForCommas = !checkingForCommas;
			      if(checkingForCommas)
			         if(line.charAt(i) == ',')
			            line = line.substring(0, i) + "/" + line.substring(i+1);
			   }
				String[] lineArr = line.split(",");
				lines.add(lineArr);
			}
			br.close();
		} catch (Exception e) {
			ErrorManager.processException(e, "Cannot Find File.\nPlease try again.", "readCSV FNF", false, true);
		}
		return lines;

	}
	
	public static String getFileLocation(String acceptableSuffix, String startDirectory) {
		JFrame parent = new JFrame();
		FileDialog fd = new FileDialog(parent, "Choose a file", FileDialog.LOAD);
		fd.setDirectory(startDirectory);
		fd.setFile("*."+acceptableSuffix);
		fd.setFilenameFilter((dir, name) -> name.endsWith(acceptableSuffix));
		fd.setVisible(true);
		String ret = (fd.getFile() == null) ? null : fd.getDirectory() + fd.getFile();
		parent.dispose();
		return ret;
	}
	
	/**
	 * returns a string location of a file to open, as selected by the user.
	 * @param acceptableSuffix the preferred file suffix (.txt, .csv) as a string
	 */
	public static String getFileLocation(String acceptableSuffix) {
		return getFileLocation(acceptableSuffix, System.getProperty("user.home"));
	}
}
