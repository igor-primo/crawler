package trtLab;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class App 
{
    public static void main( String[] args )
    {
		if(args.length != 1) {
			System.out.println("Usage: programName fileName");
			return;				// how to exit properly?
		}
		
		ArrayList<Asset> assets = new ArrayList<Asset>();
		String fileName = args[0];

		loadURLs(assets, fileName);
	
		for(Asset asset : assets) {
			System.out.print("Working on ");
			asset.print();

			for(String URL : asset.URLs) {
				try {
					Connection con = Jsoup.connect(URL);
					if(con.response().statusCode() == 200) {
						Document doc = con.get();
						if(doc != null)
							for(Element element : doc.select("a[href]"))
								System.out.println(element.absUrl("href"));
					}
				} catch(IOException e) {
				}
			}
		}
	}
	private static void loadURLs(ArrayList<Asset> assets, String fileName)
	{
		assets.add(new Asset("Rocky Linux", "https://download.rockylinux.org/pub/rocky/9/isos/x86_64/"));
		assets.add(new Asset("Rocky Linux", "https://download.rockylinux.org/pub/rocky/9/isos/x86_64/"));
	}
};

class Asset
{
	public String assetName;
	public ArrayList<String> URLs;

	Asset(String assetName, String URLs)
	{
		this.assetName = assetName;
		this.URLs = new ArrayList<String>(Arrays.asList(URLs));
	}
	void print()
	{
		System.out.println(this.assetName + " - " + this.URLs.toString());
	}
};
