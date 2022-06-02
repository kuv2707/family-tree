import java.util.Scanner;

public class driver {
    public static void main(String[] args)
    {
        treeCreator tc=new treeCreator("src.txt");
        tc.root.deepPrint("");
        Scanner in=new Scanner(System.in);
        tc.root.menu("",in);
        
        //tc.makeJSON(tc.root,"jsonTree");
        //String x=tc.readJSON("jsonTree");
        //System.out.println(x);
    }
    public static void main()
    {
        main(null);
    }
}
