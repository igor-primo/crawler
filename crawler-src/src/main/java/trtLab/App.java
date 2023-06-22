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

		loadURLs(assets, fileName); // Alimenta o arraylist assets a partir de um csv
	
		for(Asset asset : assets)
			asset.getVersions().print();
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
	public ArrayList<String> versions;

	Asset(String assetName, String URLs) // Construtor
	{
		this.assetName = assetName;
		this.URLs = new ArrayList<String>(Arrays.asList(URLs));
		this.versions = new ArrayList<String>();
	}
	
	Asset print()				// Imprime elementos da classe
	{
		System.out.println(this.assetName + " - " + this.URLs.toString());
		if(this.versions != null)
			for(String version : this.versions)
				this.log(version);
		return this;
	}
	
	Asset getVersions()
	{
		for(String URL : this.URLs) {
			try {
				Connection con = Jsoup.connect(URL);
				Document doc = con.get();

				if(con.response().statusCode() == 200)
					this.parseElements(doc);
			} catch(IOException e) {
				// TODO: tratar caso
			}
		}
		return this;
	}
	
	void parseElements(Document doc)
	{
		if(doc != null) {
			switch(this.assetName) {
			case "Rocky Linux":
				for(Element element : doc.select("a[href]")) {
					String[] target = element.absUrl("href").split("/");
					String[] result = target[target.length-1].split("-");

					// Exemplo: https://download.rockylinux.org/pub/rocky/9/isos/x86_64/Rocky-9.2-x86_64-boot.iso
					// Pega o último elemento do split("/")
					// Pega os 2 primeiros elementos do split("-")

					if(result.length < 2)
						continue;

					if(this.versions.contains(result[0] + " - " + result[1])) // TODO: ineficiente, melhorar
						continue;

					// TODO: Adiciona elementos que não são versões propriamente, como x86_64. Filtrar
					
					this.versions.add(result[0] + " - " + result[1]);
				}
				break;
			default:
				break;
			}
		}
	}
	
	void log(String str) // Wrapper para System.out.println()
	{
		System.out.println(str);
	}
};
