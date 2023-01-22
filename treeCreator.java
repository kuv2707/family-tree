import java.util.*;
import java.io.*;
class treeCreator
{
    Node root;
    ArrayList<Node> allNodes=new ArrayList<Node>();
    public treeCreator(String srcFilePath)
    {
        String document="";
        try(FileReader fr=new FileReader(new File(srcFilePath)))
        {
            
            BufferedReader br=new BufferedReader(fr);
            String s="";
            //implement comments
            while((s=br.readLine())!=null)
            {
                document+=s+"\n";
            }
        }
        catch(Exception e){}
        document+="}";
        StringTokenizer st=new StringTokenizer(document," \t\n");
        root=new Node("$ROOT$",-1,this);
        root.scanChildren(st);
        //root=root.childList.get(0);
    }
    Node getNodeByName(String name)
    {
        for(Node n:allNodes)
        {
            if(n.name.equals(name))
            return n;
        }
        return null;
    }
}