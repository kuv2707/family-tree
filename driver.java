import java.util.Scanner;

public class driver 
{
    static String SRC_PATH;
    public static void main(String[] args)
    {
        if(args!=null && args.length>0)
        SRC_PATH=args[0];
        else
        SRC_PATH="src.txt";
        treeCreator tc=new treeCreator(SRC_PATH);
        tc.root.deepPrint("");
        Scanner in=new Scanner(System.in);
        tc.root.menu("",in);
        System.out.println("Program terminated!");
        
    }
    public static void main()
    {
        main(new String[]{"src.txt"});
    }
}
