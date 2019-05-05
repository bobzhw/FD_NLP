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
		String stem = "三角形ABC的三条边AB,AC,BC构成公差为d的等差数列a_n，三角形ABC的面积是t，求这个三角形的边长和角";
		stem = stem.replaceAll("&&[a-zA-Z]+ ","").replaceAll(" ","");
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
						System.out.println(line);
						if (line.contains(",")) {
							String[] group = line.split("\\(|\\)|\\,");
							String r = group[0];

							String e1 = group[1];
							String e2 = group[2];
							List<String> p = new ArrayList<String>();
							for(int i = 3;i<group.length;i++)
							{
								p.add(group[i]);
							}
							Condition cond = new Condition();
							Relation re = new Relation(e1,e2,r,p,cond);
							questions.relations.add(re);

						} else {
							String key = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
							if(!questions.entitys.containsKey(key))
								questions.entitys.put(key,
										new Entity(line.substring(0, line.indexOf("("))));
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
		TextWriter writer= new TextWriter("test.json");
		writer.write(tmp);
		writer.close();
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
	private static final String chars="ABCDEFGHIJK";
	public String questionId;
	public String commenText;
	public Map<String,Entity> entitys;
	public List<Relation> relations;
	public Question(String questionId,String questionstem)
	{
		this.questionId = questionId;
	    commenText = questionstem;
        entitys =new HashMap<String, Entity>();
        relations = new ArrayList<Relation>();
	}
	public Question(String questionstem)
	{
		long now = new Date().getTime();
		String tmpId = String.valueOf(now);
		StringBuilder id = new StringBuilder();
		for(int i = 0;i<tmpId.length();i++)
		{
			int index = Integer.valueOf(tmpId.substring(i,i+1));
			id.append(chars.charAt(index));
		}
		this.questionId = id.toString();
		commenText = questionstem;
		entitys =new HashMap<String, Entity>();
		relations = new ArrayList<Relation>();
	}
	public Question(){}
}

class Entity
{
	public String type;
	public Entity(String type)
	{
		this.type = type;
	}
	public Entity()
	{}
}

class Relation
{
    public String entity1;
    public String entity2;
    public List<String> propertity;
    public Condition condition;
    public String relationString;
    public Relation(String e1 ,String e2,String relationString,List<String> propertity,Condition condition)
    {
        this.relationString = relationString;
        entity1=e1;
        entity2=e2;
        this.propertity = propertity;
        this.condition = condition;
    }
    public Relation(){}
}

class Condition
{
	public List<Relation> conditionRelations;
	public List<String> entities;
	public Condition(List<Relation> relations,List<String> entities)
	{
		this.conditionRelations = relations;
		this.entities = entities;
	}
	public Condition(){
		conditionRelations = new ArrayList<Relation>();
		entities = new ArrayList<String>();
	}
}