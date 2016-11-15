import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;


public class FilterWords {

public static void main(String[] str) {
	  try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resource/tamiltest.txt"), StandardCharsets.UTF_8));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resource/tamiltest1.txt"), StandardCharsets.UTF_8));
		    //BufferedReader reader = new BufferedReader(new FileReader("tamiltest.txt"));
			//fw = new FileWriter("tamiltest1.txt");
			String line = null;
			int n = 0;
			while ((line = reader.readLine()) != null) {
				if(line.contains("Translations of"))
				{  
					writer.newLine();
					line = line.replace("Translations of ","");					
					writer.write(line);
					writer.write(" # ");
				}
				else
				{
					char c = line.toLowerCase().charAt(0);
					if(!((c >= 'a' &&  c <= 'z') || line.matches("^\\s*$")))
						writer.write(line+"; ");
				}
				
			}
			reader.close();
			writer.close();

		} catch (Exception e) {
		System.out.println("Exception: " + e.getMessage());
		}
	}
}