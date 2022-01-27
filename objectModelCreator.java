import java.io.*;
import java.util.*;
class objectModelCreator
{
    static String document="";
    static int index=0;
    static int  maxDepth=0;
    static String tabs="    ";
    static Scanner in=new Scanner(System.in);
    public static void parse() throws Exception
    {
        
        FileReader fr=new FileReader(new File("sourceData.txt"));
        BufferedReader br=new BufferedReader(fr);
        String s="";
        float init=System.nanoTime();
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
        //root.print("");
        float f=System.nanoTime();
        System.out.println((f-init)/Math.pow(10,9)+" time took");
        root.menu("");
        System.out.println("Program terminated");
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
                return name+","+spouse;
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
                System.out.println("<"+getNameWithSpouse()+">");
                for(Map.Entry<String,String> s:properties.entrySet())
                {
                    System.out.println(tabs+"*)"+s.getKey()+":"+s.getValue());
                }
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
        public void menu(String spc)
        {
            /**
             * All available functions:
             * $(node):   print the entire tree from the node passed as argument 
             * *(node):   print the properties and immediate children of the node passed as argument
             * #(node):   enteres the menu for the node passed as argument
             * @:         prints the current node's properties
             * X:         Return from current node. If no parent, exit the program
             * 
             * any other input will be treated as an individual properties of the current node
             * 
             * to make:
             * method to get only children
             * make depth a property
             * print only immediate children rather than entire tree (as $(node) does)
             */
            while(true)
            {
                System.out.print(spc+">");
                String n=in.nextLine();
                
                if(n.length()==0)
                {
                    System.out.println(spc+"Current node:");
                    print(spc);
                    continue;
                }
                char func=n.charAt(0);
                
                switch(func)
                {
                    case '$'://global print all nodes method
                    {
                        //only print that named node's immediate children
                        String name=n.substring(2,n.length()-1);
                        Node result=search(name);
                        if(result==null)
                        System.out.println(spc+"No such node found");
                        else
                        result.print();
                        break;
                    }
                    case '*':
                    {
                        //only print that named node's immediate children
                        String name=n.substring(2,n.length()-1);
                        Node result=search(name);
                        if(result==null)
                        System.out.println(spc+"No such node found");
                        else
                        result.print(spc+"");
                        break;
                    }
                    case '#'://select a node
                    {
                        String name=n.substring(2,n.length()-1);
                        Node select=search(name);
                        if(select==null)
                        {
                            System.out.println(spc+"No such node found");
                            break;
                        }
                        else
                        {
                            System.out.println(spc+select.getName()+" is now the selected node");
                            
                        }
                        select.menu(spc+tabs);
                        break;
                    }
                    case '@':
                    {
                        if(properties.size()==0)
                        {
                            System.out.println(spc+"This node has no property");
                            break;
                        }
                        for(Map.Entry<String,String> s:properties.entrySet())
                        {
                            System.out.println(spc+"@)"+s.getKey()+":"+s.getValue());
                        }
                        break;
                    }
                    case 'X':
                    {
                        System.out.println(spc+"Unselecting "+getName());
                        return;
                    }
                    default:
                    {
                        System.out.println(spc+properties.get(n));
                        break;
                    }
                }
            }
        }
    }
}