import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


public class Strip extends UDF {
	
private Text result = new Text();

public Text evalute(Text str){
	if(str ==null){
		return null;
	}
	//leading and trailing white spaces can be removed
	result.set(StringUtils.strip(str.toString()));
	return result;
}
	
public Text evalute(Text str,String strChar){
	if(str==null){
		return null;
	}
	result.set(StringUtils.strip(str.toString(), strChar));
	return result;
	
}
}
