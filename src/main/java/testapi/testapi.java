package testapi;

import com.alibaba.fastjson.JSON;
import com.connect.Connect;
import com.sun.deploy.security.ValidationState;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * 复旦接口类
 * **/
public class testapi {
	private static Map<String,String> ignoremaps = new HashMap<String, String>();
	private static Map<String,String> equalMaps = new HashMap<String, String>();
	/**
	 * 用于将等式不等式作为一个整体
	 * **/
	private static final List<String> characters = new ArrayList<String>(
            Arrays.asList("+","-","*","/","0","1","2","3","4","5","6","7","8","9",
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p",
            "q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F",
            "G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
            "W","X","Y","Z","_","∠","(",")","^","%",":",".","&","&","∞",
					"↑","θ",",","[","]","|","*","π","√"));
	/**
	 * 用于将等式不等式不作为一个整体
	 * **/
	private static final List<String> lone_characters = new ArrayList<String>(
			Arrays.asList("+","-","*","/","0","1","2","3","4","5","6","7","8","9",
					"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p",
					"q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F",
					"G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
					"W","X","Y","Z","_","∠","(",")","^","%",":",".","&","&","∞",
					"↑","θ",",","[","]","|","*","π","=",">","<","!","√")
	);
	/**
	 * 将表达是替换的字符串，其实可以改成一些汉字，不会造成冲突，交给你们了
	 * **/
    private static final List<String> keyWords = new ArrayList<String>(
            Arrays.asList("I","J","K","U","V","W","T","u","v","w","i","j","k"));
	public static void main(String[] args) throws Exception {
		int index = 1;
//		singleTest(index,"圆C的圆心为点(a,b)，半径为r，则圆C的标准方程为(x-a)^2+(y-b)^2=r^2");
		testXml("椭圆O的焦点F1, F2在x轴，则它的标准方程是$ \\frac{x^2}{a^2} + \\frac{y^2}{b^2}=1(a>b>0)$ ");
	}
	/**
	 * 将表达式变为一个整体
	 * **/
	private static String convertToVariable(String stem,List<String> tmpCharacters,Map<String,String> stringMap)
    {
		stringMap.clear();
        int start =0;
        int end = 0;
        int count = 0;
        boolean flag = false;
        for(int i = 0;i<stem.length();)
        {
            String temp = stem.substring(i,i+1);
            if(tmpCharacters.contains(temp))
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
					stringMap.put(keyWords.get(count),stem.substring(start,end+1));
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
			stringMap.put(keyWords.get(count),stem.substring(start,end+1));
            stem = lhs + keyWords.get(count)+rhs;
            count++;
        }
        return stem;
    }
	/**
	 * 没用到，不写了
	 * **/
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
	/**
	 * 本例子将xml的结果做处理得到Question类对象
	 * **/
	private static void getQuestion(Iterator it,Question questions,boolean isConlusion,Map<String,String> stringMap) {
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

							if(stringMap.containsKey(group[1]))
								e1 = stringMap.get(group[1]);
							else
								e1 = group[1];
							if(stringMap.containsKey(group[2]))
								e2 = stringMap.get(group[2]);
							else
								e2 = group[2];
							if(r.equals("Equal") || r.equals("LessThan") || r.equals("GreaterThan"))
							{
								pushEntity(questions,e1,"Expression",isConlusion,stringMap);
								pushEntity(questions,e2,"Expression",isConlusion,stringMap);
							}
							List<String> p = new ArrayList<String>();
							for (int j = 3; j < group.length; j++) {
								if(stringMap.containsKey(group[j]))
									p.add(stringMap.get(group[j]));
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
							pushEntity(questions,key,typename,isConlusion,stringMap);

//
						}
					}

				}

			}
		}
	}
	/**
	 * 将实体放入Question类对象中
	 * **/
	private static void pushEntity(Question questions,String key,String typename,boolean isConlusion,
								   Map<String,String> stringMap)
	{
		if (stringMap.containsKey(key))
			key = stringMap.get(key);

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
	/**
	 * 整体逻辑，先加载复旦结果，再处理结果得到Question类对象
	 * **/
	private static void doRight(String[] ss,Question questions,Map<String,String> entitys) throws Exception
	{

		if (ss.length == 2) {

			String condition = ss[0];
			String conlusion = ss[1];
			String question1 = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
					"format=\"latex\">" + condition +
					".</blank></question>";
			String question2 = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
					"format=\"latex\">" + conlusion +
					".</blank></question>";
			//先加载复旦结果
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
			//处理结果得到Question类对象
			getQuestion(it1, questions, false,entitys);
			getQuestion(it2, questions, true,entitys);

		} else
		{
			String condition = ss[0].replaceAll("\n","");
			if(condition.length()==0)
				return;
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

		}
	}

	public synchronized static String singleTest(int index,String stem) throws Exception {
		if (stem.equals(""))
			return "";
		String tmp = "";
		Map<String, String> entitys = new HashMap<String, String>();
		if (index == 1) {
			//将=,>,<,>=,<=作为一个整体做替换
			String ignoreEqualStem = convertToVariable(stem, lone_characters, ignoremaps);
			//不将=,>,<,>=,<=作为一个整体做替换
			String equalstem = convertToVariable(stem, characters, equalMaps);
			//按则区分结论,则之前的作为条件，则之后的作为结论
			String[] igeqstem = ignoreEqualStem.split("则");
			Question questions = new Question(stem, "test");
			doRight(igeqstem, questions, ignoremaps);
			StringBuilder sb = new StringBuilder();
			List<String> equalskeylist = new ArrayList<String>();
			List<String> equalsvaluelist = new ArrayList<String>();
			for(Map.Entry<String,String> e : equalMaps.entrySet())
			{
				equalskeylist.add(e.getKey());
				equalsvaluelist.add(e.getValue());
			}
			for(Map.Entry<String,String> e : ignoremaps.entrySet()) {
				String key = e.getKey();
				String value = e.getValue();
				if (equalsvaluelist.contains(value)) {
					continue;
				} else
				{
					int i = 0;
					//此处是对不等号两端的特殊替换
					for(i = 0;i<equalsvaluelist.size()-1;i++)
					{
						if((equalsvaluelist.get(i)+"="+equalsvaluelist.get(i+1)).equals(value))
						{

							sb.append(equalskeylist.get(i)).append("=").append(equalskeylist.get(i+1));
							i++;
						}
						else if((equalsvaluelist.get(i)+">"+equalsvaluelist.get(i+1)).equals(value))
						{

							sb.append(equalskeylist.get(i)).append(">").append(equalskeylist.get(i+1));
							i++;
						}
						else if((equalsvaluelist.get(i)+"<"+equalsvaluelist.get(i+1)).equals(value))
						{

							sb.append(equalskeylist.get(i)).append("<").append(equalskeylist.get(i+1));
							i++;
						}
						else if((equalsvaluelist.get(i)+">="+equalsvaluelist.get(i+1)).equals(value))
						{

							sb.append(equalskeylist.get(i)).append(">=").append(equalskeylist.get(i+1));
							i++;
						}
						else if((equalsvaluelist.get(i)+"<="+equalsvaluelist.get(i+1)).equals(value))
						{

							sb.append(equalskeylist.get(i)).append("<=").append(equalskeylist.get(i+1));
							i++;
						}
						else
						{
							sb.append(equalskeylist.get(i));
						}
						sb.append("．");
					}

				}
			}
			List<String> tmplist = new ArrayList<String>();
			tmplist.add(sb.toString());
			doRight((String[]) tmplist.toArray(new String[tmplist.size()]), questions, equalMaps);
			tmp = JSON.toJSONString(questions);
			System.out.println(tmp);
//			TextWriter writer = new TextWriter("result/" + questions.questionId + ".json");
//			TextWriter writer2 = new TextWriter("result2/" + questions.name + ".json");
//			writer.write(tmp);
//			writer2.write(tmp);
//			writer.close();
//			writer2.close();

		} else {
			//这个else是之前的一个想法，利用我们的标注服务去加入未标注的实体，但是符老师意思是让复旦那边改，
			// 就一直没用，如果要用应该还是需要改一些东西的
			nerClient clt = new nerClient("192.168.1.123", 59997);
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

				String ignoreEqualStem = convertToVariable(stem, characters, ignoremaps);
				//不将=,>,<,>=,<=作为一个整体做替换
				String equalstem = convertToVariable(stem, lone_characters, equalMaps);
				//按则区分结论
				String[] igeqstem = ignoreEqualStem.split("则");
				Question questions = new Question(stem, "test");
				doRight(igeqstem, questions, entitys);
				StringBuilder sb = new StringBuilder();
				for (Map.Entry e : equalMaps.entrySet()) {
					sb.append(e.getValue()).append("．");
				}
				List<String> tmplist = new ArrayList<String>();
				tmplist.add(sb.toString());
				doRight((String[]) tmplist.toArray(), questions, entitys);

				tmp = JSON.toJSONString(questions);
				System.out.println(tmp);
			}
		}
		return tmp;
	}
//	/
	public static void testXml(String ques)
	{
//		ques = ques.replaceAll(" ","");
		String question1 = "<question id=\"test01_65\" type=\"solution\"><blank num=\"1\" " +
				"format=\"latex\">" + ques +
				".</blank></question>";
		getXMLFromfudan(question1,"test.xml");
	}


	public static void getXMLFromfudan(String question,String path) {
		// TODO Auto-generated method stub
		String out=Connect.ConnectWeb("http://jkx.fudan.edu.cn", question, "UTF-8");
		TextWriter writer = new TextWriter(path,false);
		writer.write(out);
		writer.close();
	}

}
/**
 * 最后的结果类
 * **/
class Question
{
	//用于产生随机questionid
	private static final String chars="ABCDEFGHIJK";
	//题目的名字
	public String name;
	//questionid
	public String questionId;
	//题干
	public String commenText;
	//实体名：实体类型
	public Map<String,Entity> entitys;
	//所以关系
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
/**
 * 实体的类型与是否是结论
 * **/
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
//实体
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
//关系
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
//条件属性
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