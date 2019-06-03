package testapi;

import com.alibaba.fastjson.JSON;
import com.connect.Connect;
import com.sun.deploy.security.ValidationState;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class testapi {
	private static Map<String,String> maps = new HashMap<String, String>();
	private static final List<String> characters = new ArrayList<String>(
            Arrays.asList("+","-","*","/","0","1","2","3","4","5","6","7","8","9",
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p",
            "q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F",
            "G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
            "W","X","Y","Z","_","∠","(",")","^","%",":",".","&","&","∞",
					"↑","$","θ",","));
    private static final List<String> keyWords = new ArrayList<String>(
            Arrays.asList("I","J","K","U","V","W","T","u","v","w","i","j","k"));
	public static void main(String[] args) throws Exception {
		int index = 1;
//		singleTest(index,"三角形ABC经过平移、旋转变换后与三角形EFG重合");
		testXml("三角形ABC经过平移、旋转变换后与三角形EFG重合");
	}
	private static String convertToVariable(String stem)
    {
        maps.clear();
        int start =0;
        int end = 0;
        int count = 0;
        boolean flag = false;
        for(int i = 0;i<stem.length();)
        {
            String temp = stem.substring(i,i+1);
            if(characters.contains(temp))
            {
                if(!flag)
                {
                    flag = true;
                    start = i;
                    end = i;
                }
                else
                {
                    end = i;
                }
                i++;
            }
            else
            {
                if(start!=end)
                {
                    flag = false;
                    String lhs = stem.substring(0,start);
                    String rhs = stem.substring(end+1);
                    maps.put(keyWords.get(count),stem.substring(start,end+1));
                    stem = lhs + keyWords.get(count)+rhs;
                    count++;
                    i=i-end+start;
                    start = 0;
                    end = 0;

                }
                else
                {
                    flag = false;
                    i++;
                }


            }
        }
        if(start!=end)
        {
            String lhs = stem.substring(0,start);
            String rhs = stem.substring(end+1);
            maps.put(keyWords.get(count),stem.substring(start,end+1));
            stem = lhs + keyWords.get(count)+rhs;
            count++;
        }
        return stem;
    }
    private static void GenerateEntityMap(String result,Map<String,String> entitys)
	{
		String[] results = result.split(" ");
		for(String r :results)
		{
			if(r.contains("&&"))
			{
				String e = r.substring(0,r.indexOf("&&"));
				String n = r.substring(r.indexOf("&&")+2);
				entitys.put(e,n);
			}
		}
	}
	private static void getQuestion(Iterator it,Question questions,boolean isConlusion,Map<String,String> entitys) {
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

							String e1 = "";
							String e2 = "";

							if(maps.containsKey(group[1]))
								e1 = maps.get(group[1]);
							else
								e1 = group[1];
							if(maps.containsKey(group[2]))
								e2 = maps.get(group[2]);
							else
								e2 = group[2];
							if(r.equals("equal") || r.equals("LessThan") || r.equals("BigThan"))
							{
								pushEntity(questions,e1,"Expression",isConlusion);
								pushEntity(questions,e2,"Expression",isConlusion);
							}
							List<String> p = new ArrayList<String>();
							for (int j = 3; j < group.length; j++) {
								if(maps.containsKey(group[j]))
									p.add(maps.get(group[j]));
								else
									p.add(group[j]);
							}
							Condition cond = new Condition();
							if(isConlusion)
							{
								Relation re = new Relation(e1, e2, r, p, cond,"T");
								questions.relations.add(re);

							}
							else
							{
								Relation re = new Relation(e1, e2, r, p, cond,"F");
								questions.relations.add(re);
							}
						} else {
							String key = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
							String typename = line.substring(0, line.indexOf("("));
							pushEntity(questions,key,typename,isConlusion);

//
						}
					}

				}

			}
		}
	}
	private static void pushEntity(Question questions,String key,String typename,boolean isConlusion)
	{
		if (maps.containsKey(key))
			key = maps.get(key);

//								questions.entitys.put(key,
//										new Entity(entitys.get(key)));

		if (!questions.entitys.containsKey(key)) {
			List<Type> temp = new ArrayList<Type>();
			temp.add(new Type(typename,isConlusion?"T":"F"));
			questions.entitys.put(key, new Entity(temp));

		} else
		{
			List<Type> temp = questions.entitys.get(key).types;
			boolean flag = false;
			for(Type type : temp)
			{
				if(type.typename.equals(typename))
				{
					flag = true;
					break;
				}
			}
			if(!flag)
			{
				temp.add(new Type(typename,isConlusion?"T":"F"));
				questions.entitys.remove(key);
				questions.entitys.put(key,new Entity(temp));
			}

		}
	}
	private static String doRight(String[] ss,Question questions,Map<String,String> entitys) throws Exception
	{
		String tmp = "";
		if (ss.length == 2) {

			String condition = ss[0];
			String conlusion = ss[1];
			String question1 = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
					"format=\"latex\">" + condition +
					".</blank></question>";
			String question2 = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
					"format=\"latex\">" + conlusion +
					".</blank></question>";
			getXMLFromfudan(question1,"test1.xml");
			getXMLFromfudan(question2,"test2.xml");
			SAXReader reader = new SAXReader();
			File file1 = new File("test1.xml");
			File file2 = new File("test2.xml");
			Document document1 = reader.read(file1);
			Document document2 = reader.read(file2);
			Element root1 = document1.getRootElement();
			Element root2 = document2.getRootElement();
			Iterator it1 = root1.elementIterator();
			Iterator it2 = root2.elementIterator();

			getQuestion(it1, questions, false,entitys);
			getQuestion(it2, questions, true,entitys);
			tmp = JSON.toJSONString(questions);
			System.out.println(tmp);
			TextWriter writer = new TextWriter("result/" + questions.questionId + ".json");
			TextWriter writer2 = new TextWriter("result2/" + questions.name + ".json");
			writer.write(tmp);
			writer2.write(tmp);
			writer.close();
			writer2.close();
		} else
		{
			String condition = ss[0];
			String question1 = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
					"format=\"latex\">" + condition +
					".</blank></question>";

			getXMLFromfudan(question1,"test1.xml");
			SAXReader reader = new SAXReader();
			File file1 = new File("test1.xml");
			Document document1 = reader.read(file1);
			Element root1 = document1.getRootElement();
			Iterator it1 = root1.elementIterator();

			getQuestion(it1, questions, false,entitys);
			tmp = JSON.toJSONString(questions);
			System.out.println(tmp);
			TextWriter writer = new TextWriter("result/" + questions.questionId + ".json");
			TextWriter writer2 = new TextWriter("result2/" + questions.name + ".json");
			writer.write(tmp);
			writer2.write(tmp);
			writer.close();
			writer2.close();
		}
		return tmp;
	}

	public synchronized static String singleTest(int index,String stem) throws Exception {
		String singletest="";
		if(stem.equals(""))
			return "";
		Map<String,String> entitys = new HashMap<String, String>();
		if (index == 1) {
//			nerClient clt = new nerClient("192.168.1.123",59997);
//			delUtil.delAllFile("result3/");
//			String stem = "平面ABC与平面DEF相交于直线L，平面GHI与平面DEF相交于直线l，则直线L与直线l平行。";
//			stem.replaceAll(" ","");
//			clt.sendMsg(stem);
//			String result = clt.receive();

//			GenerateEntityMap(result,entitys);

			String stem2 = convertToVariable(stem);
			String[] ss = stem2.split("则");
			Question questions = new Question(stem, "test");
			singletest=doRight(ss,questions,entitys);

		} else {
			nerClient clt = new nerClient("192.168.1.123",59997);
			delUtil.delAllFile("result/");
			delUtil.delAllFile("result2/");
			TextReader reader1 = new TextReader("res.txt");

			for (String data : reader1.ReadLines()) {
				String[] g = data.split("\t");
				if (g.length < 2)
					throw new Exception();
				String name = g[0];
				String stem2 = g[1];
//			String stem = "已知减函数y=f(x-1)是定义在R上的奇函数，则不等式f(1-x)>0的解集为()";
				stem = stem2.replaceAll("&&[a-zA-Z]+ ", "").replaceAll(" ", "");
				clt.sendMsg(stem);
				String result = clt.receive();

//				GenerateEntityMap(result,entitys);

				stem = convertToVariable(stem);
				String[] ss = stem.split("则");
				Question questions = new Question(stem2, name);
				singletest=doRight(ss,questions,entitys);
			}
		}
		return singletest;
	}
//	}
	public static void testXml(String ques)
	{
		String question1 = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
				"format=\"latex\">" + ques +
				".</blank></question>";
		getXMLFromfudan(question1,"test.xml");
	}


	public static void getXMLFromfudan(String question,String path) {
		// TODO Auto-generated method stub
		String out=Connect.ConnectWeb("http://jkx.fudan.edu.cn", question, "UTF-8");
		TextWriter writer = new TextWriter(path);
		writer.write(out);
		writer.close();
	}

}

class Question
{
	private static final String chars="ABCDEFGHIJK";
	public String name;
	public String questionId;
	public String commenText;
	public Map<String,Entity> entitys;
	public List<Relation> relations;
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
		this.name = "";
		this.questionId = id.toString();
		commenText = questionstem;
		entitys =new HashMap<String, Entity>();
		relations = new ArrayList<Relation>();
	}
	public Question(String questionstem,String name)
	{
		long now = new Date().getTime();
		String tmpId = String.valueOf(now);
		StringBuilder id = new StringBuilder();
		for(int i = 0;i<tmpId.length();i++)
		{
			int index = Integer.valueOf(tmpId.substring(i,i+1));
			id.append(chars.charAt(index));
		}
		this.name = name;
		this.questionId = id.toString();
		commenText = questionstem;
		entitys =new HashMap<String, Entity>();
		relations = new ArrayList<Relation>();
	}
	public Question(){}
}

class Type
{
	public String typename;
	public String isConlusion;
	public Type(String typename,String isConlusion)
	{
		this.typename = typename;
		this.isConlusion=isConlusion;
	}
	public Type(){}
}
class Entity
{
	public List<Type> types;
	public Entity(List<Type> types)
	{
		this.types = types;
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
    public String isConlusion;
    public Relation(String e1 ,String e2,String relationString,List<String> propertity,Condition condition,String isConlusion)
    {
        this.relationString = relationString;
        entity1=e1;
        entity2=e2;
        this.propertity = propertity;
        this.condition = condition;
        this.isConlusion = isConlusion;
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