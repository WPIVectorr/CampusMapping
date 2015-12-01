package main_package;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JComboBox;

import database.ServerDB;

public class dbAccess {

	private static JComboBox mapDropDown;
	private ArrayList<Map> maps = new ArrayList<Map>();
	private static ServerDB database;
		
	
	public dbAccess(ServerDB md)
	{
		database = md;
		mapDropDown = new JComboBox();
	}
	
	
	public JComboBox getMapDropDown()
	{
		maps = database.getMapsFromLocal();
		mapDropDown.addItem("Select Map");
		File vectorMapDir = new File("src/VectorMaps");
		vectorMapDir = new File(vectorMapDir.getAbsolutePath());
		File[] imgList = vectorMapDir.listFiles();
		for (int f = 0; f < imgList.length; f++) {
			/*
			 * tempMapName = imgList[f].getName(); nameLength =
			 * tempMapName.length();
			 * mapDropDown.addItem(tempMapName.substring(0, nameLength - 4));
			 */
			// includes extension
			if(!(imgList[f].getName().equals(".DS_Store"))){
				System.out.println("Dropdown:" );
				String temp = imgList[f].getName().substring(0, imgList[f].getName().length() -4);
				System.out.println(temp);

				//checks to make sure the names populating the drop down are in both the vector maps package and 
				//the database
				for(int count = 0; count < maps.size(); count++){
					System.out.println("printing from database: " + maps.get(count).getMapName());
					if(maps.get(count).getMapName().compareTo(temp) == 0){
						mapDropDown.addItem(temp);
						
					}
				}

			}
		}	
		
		return mapDropDown;
	}
	
	
}
