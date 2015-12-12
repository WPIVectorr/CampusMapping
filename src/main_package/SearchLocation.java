package main_package;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import database.ServerDB;

public class SearchLocation {

	private static HashMap<String, ArrayList<String>> allNames = new HashMap<String, ArrayList<String>>();

	private static ArrayList<String> shortNames = new ArrayList<String>();
	private static ArrayList<String> roomNums = new ArrayList<String>();
	private static FuzzyHashMap pointNames = new FuzzyHashMap(FuzzyHashMap.PRE_HASHING_METHOD.SOUNDEX);
	
	

	
	private static void prepData() {
		// TODO Auto-generated constructor stub
		

		
		
		ArrayList<String> StrattonHall 		= 	new ArrayList<String>();
		ArrayList<String> BoyntonHall 		= 	new ArrayList<String>();
		ArrayList<String> SalisburyLabs 	= 	new ArrayList<String>();
		ArrayList<String> GordonLibrary 	= 	new ArrayList<String>();
		ArrayList<String> CampusCenter 		= 	new ArrayList<String>();
		ArrayList<String> HigginsLabs 		= 	new ArrayList<String>();
		ArrayList<String> FullerLabs 		= 	new ArrayList<String>();
		ArrayList<String> KavenHall 		= 	new ArrayList<String>();
		ArrayList<String> HigginsHouse 		= 	new ArrayList<String>();
		ArrayList<String> WashburnShops 	= 	new ArrayList<String>();
		ArrayList<String> AldenHall 		= 	new ArrayList<String>();
		ArrayList<String> OlinHall 			= 	new ArrayList<String>();
		ArrayList<String> AtwaterKent 		= 	new ArrayList<String>();
		ArrayList<String> West157 			= 	new ArrayList<String>();
		ArrayList<String> Campus 			= 	new ArrayList<String>();
		ArrayList<String> AlumniGym 		= 	new ArrayList<String>();
		ArrayList<String> ProjectCenter		= 	new ArrayList<String>();


		StrattonHall.add("SH");		StrattonHall.add("Stratton Hall");		StrattonHall.add("Stratton");
		BoyntonHall.add("BH");		BoyntonHall.add("Boynton Hall");		BoyntonHall.add("Boynton");
		SalisburyLabs.add("SL");	SalisburyLabs.add("Salisbury Labs");	SalisburyLabs.add("Salisbury");
		GordonLibrary.add("GL");	GordonLibrary.add("Gordon Library");	GordonLibrary.add("Library");
		CampusCenter.add("CC");		CampusCenter.add("Campus Center");		CampusCenter.add("Rubin Campus Center");
		HigginsLabs.add("HL");		HigginsLabs.add("Higgins Labs");		HigginsLabs.add("Higgins");
		FullerLabs.add("FL");		FullerLabs.add("Fuller Labs");		 	FullerLabs.add("Fuller");
		KavenHall.add("KH");		KavenHall.add("Kaven Hall");		 	KavenHall.add("Kaven");
		HigginsHouse.add("HH");		HigginsHouse.add("Higgins House");		HigginsHouse.add("Grandma's House");
		WashburnShops.add("WB");	WashburnShops.add("Washburn Shops");	WashburnShops.add("Washburn");
		AldenHall.add("AH");		AldenHall.add("Alden Hall");		 	AldenHall.add("Alden");
		OlinHall.add("OH");		 	OlinHall.add("Olin Hall");		 		OlinHall.add("Olin");
		West157.add("SDCC");		West157.add("157 West");		 		West157.add("157");
		Campus.add("Fountain");	 	Campus.add("Campus");		 			Campus.add("Seal");
		AlumniGym.add("AG");		AlumniGym.add("Alumni Gym");		 	AlumniGym.add("Alumni");
		ProjectCenter.add("PC");	ProjectCenter.add("Project Center");	ProjectCenter.add("CDC");
		AtwaterKent.add("AK");		AtwaterKent.add("Atwater Kent");
	
		
		allNames.put("SH",		StrattonHall);
		allNames.put("BH",		BoyntonHall);
		allNames.put("SL",		SalisburyLabs);
		allNames.put("GL",		GordonLibrary);
		allNames.put("CC",		CampusCenter);
		allNames.put("HL",		HigginsLabs);
		allNames.put("FL",		FullerLabs);
		allNames.put("KH",		KavenHall);
		allNames.put("HH",		HigginsHouse);
		allNames.put("WB",		WashburnShops);
		allNames.put("AH",		AldenHall);
		allNames.put("OH",		OlinHall);
		allNames.put("AK",		AtwaterKent);
		allNames.put("PC",		ProjectCenter);
		allNames.put("AG",		AlumniGym);
		allNames.put("Fountain",Campus);
		allNames.put("Seal", 	Campus);
		allNames.put("SDCC",	West157);
	}
	
	public SearchLocation(ArrayList<Point> pointArray)	
	{
		prepData();
		
		
		for(Point point:pointArray)
		{	
			String pointName = point.getName();
			String lowerPointName = pointName.toLowerCase();
			if(!(pointName.equalsIgnoreCase("room") || lowerPointName.contains("stair") || lowerPointName.contains("hallway") 
					|| lowerPointName.contains("path") || lowerPointName.contains("elevator")) || lowerPointName.contains("entrance"))
			{
				for(String LN:getLongName(pointName))
				{
					//System.out.println("long point name: " + LN);
					pointNames.putFuzzy(LN, point);
				}
			}
		}		
	}
	
	//get's the list of aliases for a point on the map
	//these points are added to the hashmap on return
	//@param pointName name of the point, including number
	//@return returns arrayList of aliases generated from tables
	public static ArrayList<String> getLongName(String pointName)
	{
		ArrayList<String> aliasList = new ArrayList<String>();
		ArrayList<String> longNameReturn = new ArrayList<String>();

	String shortName;
	String roomNum;
		if(pointName.contains(" ")){
		String[] arr = pointName.split(" ",2);
		 shortName = arr[0];
		 roomNum = arr[1];
		}
		else{
			shortName = pointName;
			roomNum = "";
		}
		System.out.println("ShortName: "+shortName);
		
/*		int spaceIndex = pointName.indexOf(" ");
		String shortName = pointName.substring(0,spaceIndex);
		System.out.println("ShortName: "+shortName);*/
		
		//all of the aliases without roomnumattached
		aliasList = allNames.get(shortName);
		//longNames.add(e);
		//for every aliase obtained from the tables, add the room number to store in the
		//pointnames fuzzyhash
		if(aliasList != null)
		{
			for(int count = 0; count < aliasList.size(); count++)
			{
				longNameReturn.add(aliasList.get(count).concat(" ").concat(roomNum));
				System.out.println("full alias: "+longNameReturn.get(count));
			}
			return longNameReturn;

		}else{
			System.out.println("no mapping for: "+pointName);
			return new ArrayList<String>();

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//new SearchLocation();
		ArrayList<Map> maps = ServerDB.getMapsFromLocal();
		ArrayList<Point> allPoints = new ArrayList<Point>();
		for(int i = 0; i < maps.size(); i++){
			for(int j = 0; j < maps.get(i).getPointList().size(); j++){
				allPoints.add(maps.get(i).getPointList().get(j));
			}
		}
		
		new SearchLocation(allPoints);
		ArrayList<String> searchTerms = new ArrayList<String>();
		searchTerms.add(StringUtils.lowerCase("Atwater Kent"));
		//searchTerms.add(StringUtils.lowerCase("AK"));
		//searchTerms.add(StringUtils.lowerCase("AH"));

		searchTerms.add(StringUtils.lowerCase("a"));
		searchTerms.add(StringUtils.lowerCase("at"));
		searchTerms.add(StringUtils.lowerCase("atw"));
		searchTerms.add(StringUtils.lowerCase("atwa"));
		searchTerms.add(StringUtils.lowerCase("atwat"));
		searchTerms.add(StringUtils.lowerCase("atwate"));
		searchTerms.add(StringUtils.lowerCase("atwater"));
		searchTerms.add(StringUtils.lowerCase("atwater "));
		searchTerms.add(StringUtils.lowerCase("atwater K"));
		searchTerms.add(StringUtils.lowerCase("atwater Ke"));
		searchTerms.add(StringUtils.lowerCase("atwater Ken"));
		searchTerms.add(StringUtils.lowerCase("atwater Kent"));
		searchTerms.add(StringUtils.lowerCase("atwater Knt"));
		searchTerms.add(StringUtils.lowerCase("atwter Kent"));
		searchTerms.add(StringUtils.lowerCase("atwater Kent"));
		searchTerms.add(StringUtils.lowerCase("atwaterkent"));
		searchTerms.add(StringUtils.lowerCase("atwater 302"));

		
		
		for(String search:searchTerms)
		{
			//search for one of the items in the test array
			Point result = searchFor(search);
			if(result != null)
			{
				System.out.println("Searched for: "+ search+" Result: " +result.getName());
			}
	
		}

	}
	
	
	
	//return a sorted arraylist of 
	public static Point searchFor(String searchTerm)
	{
/*		ArrayList<String> results = new ArrayList<String>();
		ArrayList<ArrayList<String>> orderList = new ArrayList<ArrayList<String>>();
		
		

		for(ArrayList<String> nameArray:orderList)
		{
			//Integer levenshtein = compareStrings(,searchTerm);
			//nameArray.add(levenshtein.toString());		
			//System.out.println("Lev: "+nameArray.get(1)+" Searchterm: "+searchTerm+" compare: "+nameArray.get(0));
		}
		
		orderList.sort(new orderListComparator());
		//System.out.println("sortedList: ");
		for(ArrayList<String> sortedName:orderList)
		{
			if(Integer.parseInt(sortedName.get(1))!=0)
			{
				String buildingLetter = sortedName.get(0).substring(0, 1);
				String searchLetter = searchTerm.substring(0,1);
				System.out.println("build Letter: "+buildingLetter+" Search letter: "+searchLetter);
				if(buildingLetter.equalsIgnoreCase(searchLetter))
				{
					results.add(sortedName.get(0));
				}
			}
			//System.out.println("Build: "+sortedName.get(0)+" lev: "+ sortedName.get(1));
		}*/
		Point result = (Point) pointNames.getFuzzy(searchTerm,10);
		if(result == null){
			System.out.println("Result = null");
		}
		else{
			//System.out.println("In searched for: "+ result.getName());
		}
		return result;
	}

	
	
	//returns the levenshtein value for two Strings
	//@param String String 
	//@return int
	public static Integer compareStrings(String s, String c)
	{
		String compare = null;
		String search = null;
		if(s.length()<=c.length())
		{
			compare = c.substring(0,s.length());
			search = s;
		}else {
			search = s.substring(0,c.length());			
			compare = c;
		}
		return StringUtils.getFuzzyDistance(compare.substring(0,search.length()),search.substring(0,compare.length()), Locale.ENGLISH);
		
	}

    static class orderListComparator implements Comparator<ArrayList<String>> {

		@Override
		public int compare(ArrayList<String> o1, ArrayList<String> o2) {
			// TODO Auto-generated method stub
			Integer num1 = Integer.parseInt(o1.get(1));
			Integer num2 = Integer.parseInt(o2.get(1));
			if(num1 > num2)
				return -1;
			if(num1==num2)
				return 0;
			if(num1 < num2)
				return 1;
			
			return (Integer) null;
		}
    }

}
