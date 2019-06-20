package RelationTag;



import testapi.TextReader;
import testapi.TextWriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Tag {
    public static void singleQuestionTag(AimData data, TextWriter writer) throws  Exception{
//        AimData data = zwCreateAimData();
        List<EntityPair> questionTagData = LoadData(data);
        BufferedReader clientData = new BufferedReader(new InputStreamReader(System.in));
        int entityCount = 0;
        Map<String,Entity> entityMaps = new HashMap<String, Entity>();
        Map<String,String> relationMaps = new HashMap<String, String>();
        List<String> writeData = new ArrayList<String>();
        writeData.add("<QuestionBegin>\n"+data.getLine()+"\n");
        boolean flag = true;
        boolean haveBeenLoad = false;
        while(flag) {
            for(Entity line : data.getEntities())
            {
                entityCount++;
                entityMaps.put(String.valueOf(entityCount),line);
            }
            boolean flag1 = true;
            boolean flag2 = true;
            while (flag1) {
                entityCount = 0;
                System.out.println("您所标注的题目是:");
                System.out.println(data.getLine());
                System.out.println("本道题目所包含的实体有：");
                System.out.println("0.退出本题");
                for(Entity line : data.getEntities())
                {
                    entityCount++;
                    System.out.print(entityCount+"."+line.getIndividual()+"  ");
                }
                System.out.println();
                System.out.println("请输入您所要标注的实体对,格式为：num1,num2，如果您认为所有实体都已经标注完毕" +
                        "，请输入0，如果您所输入的格" +
                        "式不正确或者所输入的实体多于两个系统将会爆炸,请慎重");
                String cld = clientData.readLine();
                String cldGroup[] = cld.split(",|，| ");
                //输入为0就退出本题
                if (cldGroup.length == 1 && cldGroup[0].equals("0")) {
                    //开写
                    flag1 = false;
                    flag = false;
                    writeData.add("\n");
                    continue;
                }

                if (cldGroup.length != 2) {
                    System.out.println("boom....");
                    System.out.println("重新输入吧....");
                    continue;
                }
                if (!entityMaps.containsKey(cldGroup[0]) || !entityMaps.containsKey(cldGroup[1])) {
                    System.out.println("输入错误，请重新输入");
                    continue;
                }
                Entity e1 = entityMaps.get(cldGroup[0]);
                Entity e2 = entityMaps.get(cldGroup[1]);
                writeData.add("$"+e1.getIndividual()+"\t"+e2.getIndividual()+"$\n");
                int relationCount = 0;
                EntityPair ep = new EntityPair(e1.getIndividual(),e2.getIndividual());
                List<String> lines = getData(questionTagData,ep);
                if(lines == null)
                {
                    System.out.println("wrong,重新输入");
                    continue;
                }
                System.out.println("0.刚刚按错了，我不想选这俩个实体");
                for (String line : lines) {
                    relationCount++;
                    System.out.println(relationCount + "." + line);
                    relationMaps.put(String.valueOf(relationCount), line);
                }
                boolean haveRelation = false;
                while (flag2) {
                    System.out.println("请选择实体" + ep.getE1() + "和" + ep.getE2() + "的关系：（请用1,2,3标注，" +
                            "如果包含多项，请输入num1,num2,num3回车即可,如果不存在关系请直接选择0");
                    cld = clientData.readLine();
                    cldGroup = cld.split(",|，| ");
                    if (cldGroup.length == 0) {
                        System.out.println("重新输入");
                        continue;
                    }
                    for (String line : cldGroup) {
                        if(line.equals("0"))
                        {
                            flag2 = false;
                            break;
                        }
                        if (!relationMaps.containsKey(line)) {
                            System.out.println("输入序号出现错误，请重新输入");
                            continue;
                        }

                    }
                    if(flag2)
                        //写入当前选择实体的的选择的关系
                        for (String line : cldGroup) {
                            if(relationMaps.get(line)!=null)
                            {
                                System.out.println(ep.getE1()+"和"+ep.getE2()+"的关系是:"+relationMaps.get(line)+"已写入");
                                if(!writeData.contains(relationMaps.get(line)+"\n"))
                                {
                                    writeData.add(relationMaps.get(line));
                                    haveRelation = true;
                                    haveBeenLoad = true;
                                    writeData.add("\n");
                                }
                            }

                        }
                    flag1 = false;
                }
                if(!haveRelation)
                {
                    writeData.remove(writeData.size()-1);
                }
            }
        }
        writeData.remove(writeData.size()-1);
        writeData.add("<QuestionEnd>\n");
//        TextWriter writer = new TextWriter("re.txt");
        //TOTO 如果该题目最后没有进行标注，就把写了的删掉
        if(!haveBeenLoad)
        {
            int tobeDelete = writeData.size()-1;
            for(int i = writeData.size()-1;i>=0;i--)
            {
                if(writeData.get(i).equals("<QuestionBegin>\n"))
                {
                    writeData.remove(i);
                    break;
                }
                writeData.remove(i);
            }
        }

        for(String line : writeData)
        {
            writer.write(line);
        }
    }


    private static List<String> getData(List<EntityPair> questionTagData,EntityPair e)
    {
        for(EntityPair o : questionTagData)
        {
            if(o.equals(e))
            {
                return o.getOriginalData();
            }
        }
        return null;
    }
    private static List<EntityPair> LoadData(AimData data)
    {
        List<String> result = data.getResults();
        EntityPair pair = null;


        //写入文件的顺序有一点问题，采取先存入list再书写的办法可以很好的解决
        List<EntityPair> questionTagData = new ArrayList<EntityPair>();

        for(int i = 0;i<result.size();i++)
        {
            String group[] = result.get(i).split("  ");
            if(group.length==3)
            {
                String e1 = group[0].substring(0,group[0].indexOf("【"));
                String e2 = group[1].substring(0,group[1].indexOf("【"));
                String r = group[2];
                if(pair == null)
                {
                    pair = new EntityPair(e1,e2);
                }
                List<String> tmplist = new ArrayList<String>();
                if(pair.equals(new EntityPair(e1,e2)))
                {
                    pair.relations.add(r);
                    pair.originalData.add(result.get(i));
                }
                else
                {
                    EntityPair pairclone = new EntityPair(pair.getE1(),pair.getE2());
                    pairclone.relations.addAll(pair.relations);
                    pairclone.originalData.addAll(pair.originalData);
                    questionTagData.add(pairclone);
                    pair.setE1(e1);
                    pair.setE2(e2);
                    pair.relations.clear();
                    pair.originalData.clear();
                    pair.relations.add(r);
                    pair.originalData.add(result.get(i));
                }
            }
        }
        return questionTagData;
    }
    private static void WriteSomeThing(List<String> list,TextWriter writer)
    {
        for(String line : list)
        {
            writer.write(line);
        }
    }

    private static AimData zwCreateAimData()
    {
        TextReader reader = new TextReader("result.txt");
        List<String> context = reader.ReadLines();
        String line = context.get(0);
        List<String> result = new ArrayList<String>();
        List<Entity> entities = new ArrayList<Entity>();

        for(int i = 1;i<12;i++)
        {
            Entity entity = new Entity(context.get(i).split(":")[1],context.get(i).split(":")[0]);
            entities.add(entity);
        }
        for(int i = 12;i<context.size();i++)
        {
            result.add(context.get(i));
        }
        AimData data = new AimData(line,result,entities);
        reader.Close();
        return data;
    }

    public static void main(String[] args) {
        try
        {
            AimData data = zwCreateAimData();
            TextWriter writer = new TextWriter("re.txt",true);
            singleQuestionTag(data,writer);
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
