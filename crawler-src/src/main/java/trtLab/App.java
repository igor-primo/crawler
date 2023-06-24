package trtLab;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class App 
{
    public static void main( String[] args )
    {
		if(args.length != 1) {
			System.out.println("Usage: programName fileName");
			return;
		}
		
		ArrayList<Asset> assets = new ArrayList<Asset>();
		String fileName = args[0];

		loadURLs(assets, fileName); // Alimenta o arraylist assets a partir de um csv
	
		for(Asset asset : assets)
			asset.getVersions().print();
	}
	private static void loadURLs(ArrayList<Asset> assets, String fileName)
	{
		try {
			CSVParser parser = new CSVParserBuilder()
				.withSeparator(';')
				.withIgnoreQuotations(true)
				.build();
			Reader reader = Files.newBufferedReader(Paths.get(fileName));
			CSVReader csvReader = new CSVReaderBuilder(reader)
				.withCSVParser(parser)
				.build();
			String[] line;

			while((line = csvReader.readNext()) != null)
				assets.add(new Asset(line[0], line[1].split(",")));
		} catch(Exception e) {
			System.out.println("Error opening or reading file");
		}
	}
};

class Asset
{
	public String assetName;
	public ArrayList<String> URLs;
	public ArrayList<String> versions;

	Asset(String assetName, String[] URLs) // Construtor
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
				else
					this.log("Status code not 200. statusCode == " + con.response().statusCode() + " for URL = " + URL);

			} catch(IOException e) {
				this.log("Error connecting to " + URL);
				continue;
			}
		}
		this.versions.sort(null);
		this.versions.set(this.versions.size() - 1, this.versions.get(this.versions.size() - 1) + " ### Versão atual");
		return this;
	}
	
	void parseElements(Document doc)
	{
		if(doc == null)
			return;
		
		switch(this.assetName) {
		case "Rocky Linux":

			for(Element element : doc.select("a[href]")) {

				String[] URLSplitted;

				URLSplitted = element.absUrl("href").split("/");
				// Exemplo: https://download.rockylinux.org/pub/rocky/9/isos/x86_64/Rocky-9.2-x86_64-boot.iso
				// Pega o último elemento do split("/")
				// Pega os 2 primeiros elementos do split("-"), i.e, nome e versão.
				if(URLSplitted.length < 6)
					continue;
				if(this.versions.contains(this.assetName + " - " + URLSplitted[5])) // Previne duplicação. Ineficiente.
					continue;
				if(!URLSplitted[5].matches("^\\d+(\\.\\d+)*$")) // Às vezes, o campo de array que deveria ser uma versão é algo como "x86_64". Filtragem.
					continue;
				this.versions.add(this.assetName + " - " + URLSplitted[5]);
			}
			break;
		case "Ubuntu Server":

			for(Element element : doc.select("a[href]")) {

				String URL;
				String[] URLSplitted;

				URL = element.absUrl("href");
				if(!URL.startsWith("https://releases.ubuntu.com/"))
					continue;
				URLSplitted = URL.split("/");
				if(URLSplitted.length < 4)
					continue;
				if(this.versions.contains(this.assetName + " - " + URLSplitted[3])) // Previne duplicação. Ineficiente.
					continue;
				if(!URLSplitted[3].matches("^\\d+(\\.\\d+)*$"))
					continue;
				this.versions.add(this.assetName + " - " + URLSplitted[3]);
			}
			break;
		case "Oracle Linux":

			for(Element element : doc.select("a[href]")) {

				String URL;
				String[] URLSplitted;
				String versionString;

				URL = element.absUrl("href");
				if(!URL.startsWith("https://yum.oracle.com/ISOS/OracleLinux/"))
					continue;
				URLSplitted = URL.split("/");
				if(URLSplitted.length < 7)
					continue;
				versionString = URLSplitted[5].charAt(2) + "." + URLSplitted[6].charAt(1);
				if(this.versions.contains(this.assetName + " - " + versionString)) // Previne duplicação. Ineficiente.
					continue;
				if(!versionString.matches("^\\d+(\\.\\d+)*$"))
					continue;
				this.versions.add(this.assetName + " - " + versionString);
			}
			break;
		case "Windows Server":

			for(Element element : doc.select("td")) {

				if(!element.ownText().contains("Windows Server"))
					continue;
				this.versions.add(this.assetName + " - " + element.ownText());
			}
			break;
		default:
			this.log("Invalid asset name.");
			break;
		}
	}
		
	void log(String str) // Wrapper para System.out.println()
	{
		System.out.println(str);
	}
};
