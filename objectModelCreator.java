import java.io.*;
import java.util.*;
class objectModelCreator
{
    static String document="";
    static int index=0;
    static ArrayList<Node> nodes=new ArrayList<Node>();
    static String tabs="    ";
    public static void parse() throws Exception
    {
        FileReader fr=new FileReader(new File("data.txt"));
        BufferedReader br=new BufferedReader(fr);
        String s="";
        boolean ignore=false;
        while((s=br.readLine())!=null)
        {
            for(int i=0;i<s.length();i++)
            {
                if(s.charAt(i)!=' '  &&  s.charAt(i)!='\n')
                {
                    if(s.charAt(i)=='*')
                    {
                        ignore=!ignore;
                        continue;
                    }
                    if(!ignore)
                    document+=s.charAt(i);
                }
            }
        }
        //System.out.println("input data="+document);
        Node root=new Node("__root__");
        System.out.println("Creating document object model...");
        root.scanChildren();
        root.print("");
        //for(Node n:nodes)
        //n.print();
        Scanner in=new Scanner(System.in);
        while(true)
        {
            String n=in.nextLine();
            if(n.equals("X"))
            {
                System.out.println("Program terminated");
                break;
            }
            char func=n.charAt(0);
            String name=n.substring(2,n.length()-1);
            switch(func)
            {
                case '$':
                {
                    //only print that named node's immediate children
                    Node result=root.search(name);
                    if(result==null)
                    System.out.println("No such node found");
                    else
                    result.print();
                    break;
                }
                case '*':
                {
                    //only print that named node's immediate children
                    Node result=root.search(name);
                    if(result==null)
                    System.out.println("No such node found");
                    else
                    result.print("");
                    break;
                }
                default:
                {
                    System.out.println("Unknown method: "+func);
                }
            }
        }
        in.close();
    }
    public static void main(String[] args) {
        try
        {
            parse();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void log(String s)
    {
        System.out.println(s);
    }
    static class Node
    {
        String name="";
        String spouse="";
        ArrayList<Node> parentlist=new ArrayList<Node>();//no need for a list of parents though
        ArrayList<Node> childlist=new ArrayList<Node>();
        public Node(String name)
        {
            if(name.contains("+"))
            {
                int k=name.indexOf("+");
                this.name=name.substring(0,k);
                this.spouse=name.substring(k+2);
            }
            else
            this.name=name;
        }
        public String getNameWithSpouse()
        {
            if(spouse==null)
            {
                return name;
            }
            else
            {
                if(spouse.equals(""))
                return name;
                else
                return name+" spouse: "+spouse;
            }
        }
        public String getName()
        {
            return name;
        }
        public Node getParent()
        {
            return parentlist.get(0);//for now 0th index
        }
        public String readName()
        {
            index++;
            String nam="";
            while(document.charAt(index)!='{'  && document.charAt(index)!=';')
            {
                nam+=document.charAt(index);
                index++;
            }
            return nam;
        }
        public void scanChildren()
        {
            while(index<document.length())
            {
                //log(""+document.charAt(index));
                switch (document.charAt(index)) 
                {
                    case '.':
                    {
                        //log("found a .");
                        Node n=new Node(readName());
                        childlist.add(n);
                        n.parentlist.add(this);
                        nodes.add(n);
                        if(document.charAt(index)=='{')
                        {
                            n.scanChildren();
                        }
                        if(document.charAt(index)==';')
                        {
                            continue;
                        }
                        break;
                    }    
                    case '}':
                    {
                        return;
                    }  
                    default:
                    {
                        //System.out.println("unexpected token "+document.charAt(index));
                        break;
                    }
                }
                index++;
            }

        }
        public void print(String tab)//also prints grandchildren nodes
        {
            if(childlist.size()>0)
            {
                System.out.println(tab+"<"+getNameWithSpouse()+">");
                for(Node n:childlist)
                {
                    n.print(tab+tabs);
                }
                System.out.println(tab+"</"+getName()+">");
            }
            else
            {
                System.out.println(tab+"<"+getNameWithSpouse()+">");
            }
        }
        public void print()//does not print grandchildren nodes
        {
            if(childlist.size()>0)
            {
                System.out.println("<"+getNameWithSpouse()+">");
                for(Node n:childlist)
                {
                    System.out.print(tabs+n.getNameWithSpouse());
                    if(n.childlist.size()>0)
                    System.out.println("...");
                    else
                    System.out.println();
                }
                System.out.println("</"+getName()+">");
            }
            else
            {
                System.out.println("<"+getNameWithSpouse()+">");
            }
        }
        public Node search(String name)
        {
            if(this.name.equals(name)  || this.spouse.equals(name))
            return this;
            Node b;
            for(Node n:childlist)
            {
                b=n.search(name);
                if(b!=null)
                return b;
            }
            return null;
        }
    }
}