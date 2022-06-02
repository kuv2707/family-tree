import java.util.Scanner;

public class driver {
    public static void main(String[] args)
    {
        treeCreator tc=new treeCreator("src.txt");
        tc.root.deepPrint("");
        //tc.log(tc.allNodes.toString());
        Scanner in=new Scanner(System.in);
        tc.root.menu("",in);
        //Node k=tc.root.searchChild("cousin");
        //System.out.println(k.getVariable("grandparent.parent.color"));
        //Node p=k.searchInParents("grandparent");
        //System.out.println(p);
    }
    public static void main()
    {
        main(null);
    }
}
