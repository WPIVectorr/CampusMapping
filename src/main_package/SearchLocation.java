package main_package;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringUtils.*;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.CaseInsensitiveMap;

public class SearchLocation {

	private ArrayList<String> longNames = new ArrayList<String>();
	private ArrayList<String> shortNames = new ArrayList<String>();

	
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
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
			String compare = "atwater kent              ";
			int distanceAk = compareStrings("AK",search);
			int distanceAtwater= compareStrings(search,  compare.substring(0,search.length()));
			System.out.println("searchFor: "+search+" distanceAk: "+ distanceAk +" distanceAtwater: "+distanceAtwater );
			
		}

	}
	
	
	
	//return a sorted arraylist of 
	public static ArrayList<String> searchFor(String searchTerm)
	{
		ArrayList<String> results = null;
		
		
		return results;
		
	}
	
	public static ArrayList<String> orderList(ArrayList<String> unsorted)
	{
		ArrayList<String> sorted = new ArrayList<String>();
		
		
		
		
		return sorted;
	}
	
	
	//returns the levenshtein value for two Strings
	//@param String String 
	//@return int
	public static int compareStrings(String s, String c)
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
		return StringUtils.getLevenshteinDistance(compare.substring(0,search.length()),search.substring(0,compare.length()));
		
	}

}
