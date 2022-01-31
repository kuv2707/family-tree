import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class createDummyTree 
{
    static Random random=new Random();
    static PrintWriter pw;
    static final String TAB="   ";
    public static void main(String args[])throws Exception
    {
        FileWriter fw=new FileWriter("dummydata.kuv");
        BufferedWriter bw=new BufferedWriter(fw);
        pw=new PrintWriter(bw);
        createNode("",1);
        pw.close();
        bw.close();
        fw.close();
        System.out.println("Tree created successfully!");
    }
    public static void createNode(String spc,int depth)
    {
        if(depth>8)
        return;
        int children=random.nextInt(8);
        pw.println(spc+"."+getRandomName());
        pw.println(spc+"{");
        pw.println(spc+TAB+"@age:"+random.nextInt()+";");
        for(int i=1;i<children;i++)
        {
            createNode(spc+TAB,depth+1);
        }
        pw.println(spc+"}");
    }
    public static String getRandomName()
    {
        int len=random.nextInt(20);
        String str="";
        for(int i=0;i<len;i++)
        {
            str+=(char)(65+random.nextInt(50));
        }
        return str;
    }
}
