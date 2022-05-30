import java.io.*;
import java.util.*;
/**
 * method to print current node
 */
class objectModelCreator
{
    static String document="";
    static int index=0;
    static int  maxDepth=0;
    static String tabs="    ";
    static HashMap<String,String> globalVariables=new HashMap<String,String>();
    static Scanner in=new Scanner(System.in);
    public static void parse() throws Exception
    {
        FileReader fr=new FileReader(new File("sourceData.kuv"));
        BufferedReader br=new BufferedReader(fr);
        String s="";
        float init=System.nanoTime();
        boolean ignore=false;
        while((s=br.readLine())!=null)
        {
            for(int i=0;i<s.length();i++)
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
        fr.close();
        Node root=new Node("root",0);
        System.out.println("Creating document object model...");
        root.scanChildren(0);
        float f=System.nanoTime();
        System.out.println((f-init)/Math.pow(10,9)+" time took");
        root.menu("");//user is already directed inside root node
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
        String spouse="none";
        int depth=0;
        ArrayList<Node> parentlist=new ArrayList<Node>();//no need for a list of parents though
        ArrayList<Node> childlist=new ArrayList<Node>();
        HashMap<String,String> properties=new HashMap<String,String>();
        public Node(String name,int dept)
        {
            depth=dept;
            properties.put("depth",""+depth);
            name=name.trim();
            if(name.contains("+"))
            {
                int k=name.indexOf("+");
                this.name=name.substring(0,k);
                this.spouse=name.substring(k+2);
                properties.put("spouse",this.spouse);
            }
            else
            this.name=name;
        }
        public String getNameWithSpouse()
        {
            if(spouse.equals("none"))
                return name;
            else
                return name+","+spouse;
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
            while(document.charAt(index)!='{'  && document.charAt(index)!=';' && document.charAt(index)!='\n')
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
                            while(document.charAt((index))!=':')
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
                    case ' ':
                        {
                            index++;
                            continue;
                        }
                    case '\n':
                        {
                            index++;
                            continue;
                        }
                    case '\u0009':
                        {
                            index++;
                            continue;
                        }
                    default:
                        {
                            System.out.println("unexpected token "+(int)document.charAt(index));
                            break;
                        }
                }
                index++;
            }

        }
        public void print(boolean grandchildren,boolean printproperties,String space)
        {
            System.out.println(space+"<"+getNameWithSpouse()+">");
            if(grandchildren)
            {
                
                if(printproperties)
                {
                    for(Map.Entry<String,String> s:properties.entrySet())
                    {
                        System.out.println(space+"*)"+s.getKey()+":"+s.getValue());
                    }
                }
                for(Node n:childlist)
                {
                    n.print(true,true,space+tabs);
                }
            }
            else
            {
                
                for(Map.Entry<String,String> s:properties.entrySet())
                {
                    System.out.println(space+"*)"+s.getKey()+":"+s.getValue());
                }
                for(Node n:childlist)
                {
                    System.out.print(space+n.getNameWithSpouse());
                    if(n.childlist.size()>0)
                    System.out.println("...");
                    else
                    System.out.println();
                }
            }
            System.out.println(space+"</"+getNameWithSpouse()+">");
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
             * *(node):   print the entire tree from the node passed as argument 
             * $(node):   print the properties and immediate children of the node passed as argument
             * #(node):   enters the menu for the node passed as argument
             * @      :   prints the current node's properties
             * &      :   prints the current node's children
             * X      :   Return from current node. If no parent, exit the program
             * 
             * any other input will be treated as an individual property of the current node
             * passing no arguments to a function accepting arguments will result in the current node being automatically passed to it
             * 
             * to make:
             * method to get only children
             * print only immediate children rather than entire tree (as $(node) does)
             */
            while(true)
            {
                System.out.print(spc+">");
                String n=in.nextLine();
                if(n.length()==0)
                {
                    continue;
                }
                char func=n.charAt(0);
                String nombre=this.getName();
                try
                {
                    nombre=n.substring(2,n.length()-1);
                        if(nombre.equals(""))
                        nombre=this.getName();
                }
                catch(Exception e)
                {
                    //the function called does not take arguments
                }
                switch(func)
                {
                    case '$'://global print all nodes method
                    {
                        //only print that named node's immediate children
                        
                        Node result=search(nombre);
                        if(result==null)
                        System.out.println(spc+"No such node found");
                        else
                        result.print(false,true,spc);
                        break;
                    }
                    case '*':
                    {
                        //only print that named node's immediate children
                        Node result=search(nombre);
                        if(result==null)
                        System.out.println(spc+"No such node found");
                        else
                        result.print(true,true,spc);
                        break;
                    }
                    case '#'://select a node
                    {
                        Node select=search(nombre);
                        if(select==null)
                        {
                            if(nombre.equals(this.getName()))
                            {
                                System.out.println(spc+"You are already in "+nombre);
                            }
                            else
                            {
                                System.out.println(spc+"No such node found");
                            }
                            break;
                        }
                        else
                        {
                            System.out.println(spc+select.getName()+" is now the selected node");
                            
                        }
                        String y="";
                        for(int i=0;i<select.depth-depth;i++)
                        {
                            y+=tabs;
                        }
                        select.menu(y);//instead of just one spc, there should be that number of spcs as is the depth of this node from current node
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
                    case '!':
                    {
                        main(null);
                        System.exit(0);
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