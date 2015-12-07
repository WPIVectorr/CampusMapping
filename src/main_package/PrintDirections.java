package main_package;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class PrintDirections {

	public PrintDirections(ArrayList<String> directions)
	{
		File outputFilename = null;
		String fileName = null;
		JFrame chooseFile = new JFrame();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify Map to Add");

		int userSelection = fileChooser.showSaveDialog(chooseFile);


		if (userSelection == JFileChooser.APPROVE_OPTION) {
			outputFilename = fileChooser.getSelectedFile();
			fileName = outputFilename.toString();
		}




		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
			for(String str: directions) {
				writer.write(str);
				writer.append(System.getProperty("line.separator"));
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		public static void main(String[] args) {
			// TODO Auto-generated method stub
			ArrayList<String> directions = new ArrayList<String>();
			directions.add("test1");
			directions.add("test2");
			directions.add("test3");
			directions.add("test4");
			directions.add("test5");
			new PrintDirections(directions);
		}

	}
