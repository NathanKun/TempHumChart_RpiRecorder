import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class UpgradeDataFile {

	public static void main(String[] args) {
		ArrayList<String> file = new ArrayList<String>();
		ArrayList<String> newFile = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get(System.getProperty("user.dir") + "/src/dataOld.txt"))) {

			stream.forEach(file::add);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String date = "";
		String data = "";
		for(int i = 0; i < file.size(); i = i + 2) {
			date = file.get(i);
			date = date.substring(0, 10).replace(" ", "-") + date.substring(10, 13) + ":" + date.substring(13, 15) + ":" + date.substring(15, 17);
			data = file.get(i + 1).replace("    ", ";");
			newFile.add(date + ";" + data + ";");
		}
		
		//newFile.forEach(System.out::println);
		try {
			Files.write(Paths.get(System.getProperty("user.dir") + "/src/data.txt"), newFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
