import java.util.Scanner;

public class driver 
{
    static final String SRC_PATH="src.txt";
    public static void main(String[] args)
    {
        treeCreator tc=new treeCreator(SRC_PATH);
        tc.root.deepPrint("");
        Scanner in=new Scanner(System.in);
        tc.root.menu("",in);
        System.out.println("Terminated!");
        //tc.makeJSON(tc.root,"jsonTree");
        //String x=tc.readJSON("jsonTree");
        //System.out.println(x);
    }
    public static void main()
    {
        main(null);
    }
}
