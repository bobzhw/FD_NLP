package testapi;

import com.alibaba.fastjson.JSON;
import com.connect.Connect;
import com.google.gson.JsonObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.*;

public class testapi {

	public static void main (String[] args) throws Exception {
		String stem = "DE是三角形ABC的中位线，点D在AB上，点E在AC上，则DE//BC且DE=0.5*BC";
		String question = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
				"format=\"latex\">" + stem +
				".</blank></question>";
		getXMLFromfudan(question);
		SAXReader reader = new SAXReader();
		File file = new File("test.xml");
		Document document = reader.read(file);
		Element root = document.getRootElement();
		Iterator it = root.elementIterator();
		JsonObject jsonObject = new JsonObject();
		Question questions = new Question(stem);
		while (it.hasNext()) {
			Element element = (Element) it.next();
			List<Element> l = element.elements();
			for (Element e : l) {
				List<Element> ll = e.elements();

				for (Element ee : ll) {
					String relation = ee.getText();
					String[] relations = relation.split(" ");

					for (String line : relations) {

						if (line.contains(",")) {
							String r = line.substring(0, line.indexOf("("));
							String e1 = line.substring(line.indexOf("(") + 1, line.indexOf(","));
							String e2 = line.substring(line.indexOf(",") + 1, line.indexOf(")"));
							Relation re = new Relation(e1, e2, r);
							questions.relations.add(re);

						} else {
							String key = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
							if(!questions.entitys.containsKey(key))
								questions.entitys.put(key,
										line.substring(0, line.indexOf("(")));
						}
					}

//					System.out.println(entitys.trim()+r.trim());

				}

			}
		}
		String tmp = JSON.toJSONString(questions);
		System.out.println(tmp);
		Socket client = new Socket("192.168.1.10", 9999);
		DataOutputStream dos = new DataOutputStream(client.getOutputStream());

		dos.write(tmp.getBytes());
		dos.flush();
	}



	public static void getXMLFromfudan(String question) {
		// TODO Auto-generated method stub


		String out=Connect.ConnectWeb("http://jkx.fudan.edu.cn", question, "UTF-8");
		TextWriter writer = new TextWriter("test.xml");
		writer.write(out);
		writer.close();

		
	}

}

class Question
{

	public String commenText;
	public Map<String,String> entitys;
	public List<Relation> relations;
	public Question(String questionstem)
	{
	    commenText = questionstem;
        entitys =new HashMap<String, String>();
        relations = new ArrayList<Relation>();
	}
}
class Relation
{
    public String entity1;
    public String entity2;
    public String relationString;
    public Relation(String e1 ,String e2,String relationString)
    {
        this.relationString = relationString;
        entity1=e1;
        entity2=e2;
    }
}
