package main_package;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import javax.mail.search.SearchTerm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringUtils.*;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.CaseInsensitiveMap;

public class SearchLocation {

	private static ArrayList<String> longNames = new ArrayList<String>();
	private static ArrayList<String> shortNames = new ArrayList<String>();
	private static ArrayList<String> roomNums = new ArrayList<String>();
	
	public SearchLocation() {
		// TODO Auto-generated constructor stub
		longNames.add("Atwater Kent");
		longNames.add("Stratton Hall");
		longNames.add("Boynton Hall");
		longNames.add("Salisbury Labs");
		longNames.add("Gordon Library");
		longNames.add("Campus Center");
		longNames.add("Higgins Labs");
		longNames.add("Fuller Labs");
		longNames.add("Kaven Hall");
		longNames.add("Higgins House");
		longNames.add("Washburn Shops");
		longNames.add("Alden Hall");
		longNames.add("Olin Hall");
		
		shortNames.add("AK");
		shortNames.add("SH");
		shortNames.add("BH");
		shortNames.add("SL");
		shortNames.add("GL");
		shortNames.add("CC");
		shortNames.add("HL");
		shortNames.add("FL");
		shortNames.add("KH");
		shortNames.add("HH");
		shortNames.add("WS");
		shortNames.add("AH");
		shortNames.add("OH");
		
		roomNums.add("001");
		roomNums.add("201");
		roomNums.add("202");
		roomNums.add("203");
		roomNums.add("303");
		roomNums.add("508");
		roomNums.add("002");
		roomNums.add("004");
		roomNums.add("004");
		roomNums.add("332");
		roomNums.add("116");
		roomNums.add("113");
		roomNums.add("Chairman's Room");
		roomNums.add("Lower");
		roomNums.add("Upper");
		roomNums.add("334");
		roomNums.add("204");
		
	}
	
	public SearchLocation(ArrayList<Point> pointArray)	
	{
		for(Point point:pointArray)
		{	
			String pointName = point.getName();
			String lowerPointName = pointName.toLowerCase();
			if(pointName.equalsIgnoreCase("room") || lowerPointName.contains("stair") || lowerPointName.contains("hallway") 
					|| lowerPointName.contains("path") || lowerPointName.contains("elevator") )
				shortNames.add(pointName);
		}		

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SearchLocation();
		ArrayList<String> searchTerms = new ArrayList<String>();
		searchTerms.add(StringUtils.lowerCase("Atwater Kent"));
		searchTerms.add(StringUtils.lowerCase("AK"));
		searchTerms.add(StringUtils.lowerCase("AH"));

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
		
		
		
		for(String search:searchTerms)
		{
			//search for one of the items in the test array
			ArrayList<String> results = searchFor(search);
			System.out.println("Searched for: "+ search+" Result: " );
			for(int i = 0; i<results.size();i++)
			{
				System.out.println(i+" " +results.get(i));
			}
			System.out.println(" ");
	
		}

	}
	
	
	
	//return a sorted arraylist of 
	public static ArrayList<String> searchFor(String searchTerm)
	{
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<ArrayList<String>> orderList = new ArrayList<ArrayList<String>>();
		
		
		
		for(String shortName:shortNames)
		{ 
			ArrayList<String> addArray = new ArrayList<String>();
			addArray.add(shortName);
			orderList.add(addArray);		
		}		
		for(String longName:longNames)
		{ 
			ArrayList<String> addArray = new ArrayList<String>();
			addArray.add(longName);
			orderList.add(addArray);			
		}		
		for(ArrayList<String> nameArray:orderList)
		{
			Integer levenshtein = compareStrings(nameArray.get(0),searchTerm);
			nameArray.add(levenshtein.toString());		
			//System.out.println("Lev: "+nameArray.get(1)+" Searchterm: "+searchTerm+" compare: "+nameArray.get(0));
		}
		
		orderList.sort(new orderListComparator());
		//System.out.println("sortedList: ");
		for(ArrayList<String> sortedName:orderList)
		{
			if(Integer.parseInt(sortedName.get(1))!=0)
			{
				results.add(sortedName.get(0));
			}
			//System.out.println("Build: "+sortedName.get(0)+" lev: "+ sortedName.get(1));
		}
		return results;
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
