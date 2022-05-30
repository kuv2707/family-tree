import java.util.*;
class Node
{
    final String ONETAB="\t";
    ArrayList<Node> childList=new ArrayList<Node>();
    HashMap<String,String> instanceVariables=new HashMap<String,String>();
    String name;
    Node conjugate;
    int generation;
    Node immediateParent=null;
    Node(String name,int gen)
    {
        this.name=name;
        generation=gen;
    }
    @Override
    public String toString()
    {
        return "<node name="+name+">";
    }
    String getName()
    {
        return name;
    }
    void setChild(Node n)
    {
        //n has to be a child of this node
        this.childList.add(n);
        n.immediateParent=this;
    }
    public void deepPrint(String space)
    {
        System.out.println(space+"<"+getName()+">");
        for(Map.Entry<String,String> s:instanceVariables.entrySet())
        {
            System.out.println(space+"*)"+s.getKey()+":"+s.getValue());
        }
        for(Node n:childList)
        {
            n.deepPrint(space+ONETAB);
        }
        System.out.println(space+"</"+getName()+">");
    }
}