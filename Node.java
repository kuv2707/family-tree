import java.util.*;
import java.io.*;

class Node
{
    static final String ONETAB="    ";
    ArrayList<Node> childList=new ArrayList<Node>();
    HashMap<String,String> instanceVariables=new HashMap<String,String>();
    String name;
    Node conjugate;//to implement
    int generation;
    Node immediateParent=null;
    treeCreator god;
    Node(String name,int gen,treeCreator g)
    {
        this.name=name;
        generation=gen;
        god=g;
    }
    @Override
    public String toString()
    {
        return "<node "+name+">";
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
        n.generation=generation+1;
    }
    void removeChild(Node n)
    {
        this.childList.remove(n);
        god.allNodes.remove(n);
    }
    /**
     * 
     * @param name name of wanted node
     * @return starting the search from itself, it returns the node of name passed in arg, (if exists) else null
     */
    Node searchInParents(String name)
    {
        if(getName().equals(name))
        return this;
        else
        {
            if(this.immediateParent!=null)
            return this.immediateParent.searchInParents(name);
            else
            return null;
        }
    }
    Node searchChild(String name)
    {
        for(Node n:childList)
        {
            if(n.getName().equals(name))
            {
                return n;
            }
            else
            {
                Node m=n.searchChild(name);
                if(m!=null)
                return m;
                //else continue this nodes childlist iteration
            }
        }
        return null;
    }
    public void deepPrint(String space)
    {
        System.out.print(space+"<"+getName());
        for(Map.Entry<String,String> s:instanceVariables.entrySet())
        {
            System.out.print(" "+" "+s.getKey()+"=\""+s.getValue()+"\"");
        }
        System.out.println(">");
        for(Node n:childList)
        {
            n.deepPrint(space+ONETAB);
        }
        System.out.println(space+"</"+getName()+">");
    }
    public String getVariableFromAddress(String key)//doesnt include the prefix &
    {
        if(!key.contains("."))
        return instanceVariables.get(key);
        
        ArrayList<String> treePath=new ArrayList<String>();
        String noden="";
        key+=".";
        for(int i=0;i<key.length();i++)
        {
            if(key.charAt(i)=='.')
            {
                treePath.add(noden);
                noden="";
            }
            else
            noden+=key.charAt(i);
        }
        Node fin=god.getNodeByName(treePath.get(0));
        for(int i=1;i<treePath.size()-1;i++)
        {
            Node ch=fin.searchChild(treePath.get(i));
            if(ch==null)
            return null;
            fin=ch;
        }
        

        return fin.instanceVariables.get(treePath.get(treePath.size()-1));
    }
    public boolean menu(String space,Scanner in)
    {
        System.out.println(space+"Entering node "+getName());
        while(true)
        {
            System.out.print(space+">");
            String command=in.nextLine().trim();
            if(command.equals("return"))
            break;
            StringTokenizer toks=new StringTokenizer(command," ");
            while(toks.hasMoreTokens())
            {
                String t=toks.nextToken();
                
                switch(t)
                {
                    case "define":
                    {
                        String[] assig=god.readVariable(this,toks,"as");
                        System.out.println(space+"defined "+assig[0]+" as "+assig[1]);
                        break;
                    }
                    case "undefine":
                    {
                        String varname=toks.nextToken();
                        instanceVariables.remove(varname);
                        System.out.println(space+varname+" is no longer defined in "+getName());
                        break;
                    }
                    case "insert":
                    {
                        //add a node of specified name to childList of current node
                        String name=toks.nextToken();
                        Node n=new Node(name,generation+1,god);
                        god.allNodes.add(n);
                        setChild(n);
                        System.out.println(space+" inserted node "+name+" as a child of "+getName());
                        break;
                    }
                    case "fuck":
                    {
                        //add a node of specified name to childList of current node
                        String name=toks.nextToken();
                        
                        Node n=searchChild(name);
                        if(n==null)
                        System.out.println(space+name+" is already absent");
                        else
                        {
                            removeChild(n);
                            System.out.println(space+name+" is removed from "+getName());
                        }
                        
                        break;
                    }
                
                    case "enter":
                    {
                        String name=toks.nextToken();
                        Node n=searchChild(name);
                        if(n!=null)
                        {
                            int gendiff=n.generation-generation;
                            String tabb="";
                            while(gendiff-->0)
                            tabb+=ONETAB;
                            boolean res=n.menu(space+tabb,in);
                            if(!res)
                            return false;
                        }
                        else
                        System.out.println(space+"No such node found");
                        break;
                    }
                    case "fuckoff":
                    {
                        return false;
                    }
                    //implement delete
                    case "printself":
                    {
                        deepPrint(space);
                        break;
                    }
                    case "save":
                    {
                        try(PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(driver.SRC_PATH))))
                        {
                            if(god.root.childList.size()>0)
                            god.root.childList.get(0).save(pw,"");
                        }
                        catch(IOException e)
                        {
                            System.err.println("Couldn't save");
                        }
                        break;
                    }
                    default:
                    {
                        //functions
                        if(t.charAt(0)=='&')
                        {
                            //System.out.println("Searching variable "+t+" in "+instanceVariables.toString());
                            String s=getVariableFromAddress(t.substring(1));
                            System.out.println(space+s);
                        }
                        else
                        {
                            //function call
                            StringTokenizer f=new StringTokenizer(t,"() ,");
                            //String methodName=f.nextToken();
                            while(f.hasMoreTokens())
                            System.out.println(f.nextToken());
                        }
                    }
                }
            }
        }
        System.out.println(space+"Leaving "+getName());
        return true;
    }
    public void save(PrintWriter pw,String space)
    {
        pw.println(space+"node "+name);
        pw.println(space+"{");
        instanceVariables.entrySet().forEach(entry->
        {
            pw.println(space+ONETAB+"def "+entry.getKey()+"="+entry.getValue()+";");
        });
        for(Node n:childList)
        {
            n.save(pw,space+ONETAB);
        }
        pw.println(space+"}");
    }
}