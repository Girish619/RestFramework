package genericUtility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class FileUtility {

	public String readPropertyFile(String key) throws FileNotFoundException, IOException {
		Properties property = new Properties();
		property.load(new FileInputStream(IpathConstant.filePath));
		String value = property.getProperty(key);
		return value;
	}
}
