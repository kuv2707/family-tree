import java.io.*;
import java.util.*;
class objectModelCreator
{
    static String document="";
    static int index=0;
    static int  maxDepth=0;
    //static ArrayList<Node> nodes=new ArrayList<Node>();
    static String tabs="   ";
    public static void parse() throws Exception
    {
        float init=System.nanoTime();
        FileReader fr=new FileReader(new File("sourceData.txt"));
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
        fr.close();
        Node root=new Node("$root",-1);
        System.out.println("Creating document object model...");
        
        root.scanChildren(0);
        
        root.print("");
        float f=System.nanoTime();
        System.out.println((f-init)/Math.pow(10,9)+" time took");
        Scanner in=new Scanner(System.in);
        while(true)
        {
            String n=in.nextLine();
            if(n.equals("X"))
            {
                System.out.println("Program terminated");
                break;
            }
            if(n.length()==0)
            {
                System.out.println("Empty command");
                continue;
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
                case '>':
                {
                    //find generation gap between two nodes (difference in depth)
                }
                default:
                {
                    System.out.println("Unknown method: "+func);
                    break;
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
        int depth=0;
        ArrayList<Node> parentlist=new ArrayList<Node>();//no need for a list of parents though
        ArrayList<Node> childlist=new ArrayList<Node>();
        HashMap<String,String> properties=new HashMap<String,String>();
        public Node(String name,int dept)
        {
            depth=dept;
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
            
            return parentlist.size()==0?null:parentlist.get(0);//for now 0th index
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
        public void scanChildren(int depth)
        {
            while(index<document.length())
            {
                switch (document.charAt(index)) 
                {
                    case '.':
                    {
                        Node n=new Node(readName(),depth);
                        childlist.add(n);

                        n.parentlist.add(this);
                        //nodes.add(n);
                        if(document.charAt(index)=='{')
                        {
                            index++;
                            n.scanChildren(depth+1);
                        }
                        if(document.charAt(index)==';')
                        {
                            index++;
                            continue;
                        }
                        break;
                    }    
                    case '@':
                    {
                        index++;
                        String key="",value="";
                        while(document.charAt((index))!='=')
                        {
                            key+=document.charAt(index);
                            index++;
                        }
                        index++;
                        while(document.charAt(index)!=';')
                        {
                            value+=document.charAt(index);
                            index++;
                        }
                        properties.put(key,value);
                        break;
                    }
                    case '}':
                    {
                        return;
                    }  
                    default:
                    {
                        System.out.println("unexpected token "+document.charAt(index));
                        break;
                    }
                }
                index++;
            }

        }
        public void print(String tab)//also prints grandchildren nodes
        {
            System.out.print(tab+"<"+getNameWithSpouse());
            System.out.println(" "+properties.entrySet().toString().replace("=",":")+">");
            if(childlist.size()>0)
            {
                
                for(Node n:childlist)
                {
                    n.print(tab+tabs);
                }
                //System.out.println(tab+"</"+getName()+">");
            }
            
            System.out.println(tab+"</"+depth+" "+getName()+">");
        }
        public void print()//does not print grandchildren nodes
        {
            if(childlist.size()>0)
            {
                System.out.print("<"+getNameWithSpouse());
                System.out.println(" "+properties.entrySet().toString().replace("=",":")+">");
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