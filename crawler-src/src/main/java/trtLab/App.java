package trtLab;

import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class App 
{
    public static void main( String[] args )
    {
		ArrayList<Asset> assets = new ArrayList<Asset>();

		assets.add(new Asset("Rocky Linux", {"https://1.com", "https://2.com"}));
		System.out.println(assets);
	}
};

class Asset
{
	public String assetName;
	public ArrayList<String> URLs;

	Asset(String assetName, String[] URLs)
	{
		this.assetName = assetName;
		this.URLs = new ArrayList<String>(Array.asList(URLs));
	}
};
