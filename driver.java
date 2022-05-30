public class driver {
    public static void main(String[] args)
    {
        treeCreator tc=new treeCreator("src.txt");
        tc.root.deepPrint("");
        //tc.log(tc.allNodes.toString());
    }
    public static void main()
    {
        main(null);
    }
}
