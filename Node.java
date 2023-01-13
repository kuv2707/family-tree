import java.util.*;
import java.io.*;

class Node
{
    static final String ONETAB="    ";
    ArrayList<Node> childList=new ArrayList<Node>();
    HashMap<String,Object> instanceVariables=new HashMap<String,Object>();
    String name;
    Node conjugate;
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
        for(Map.Entry<String,Object> s:instanceVariables.entrySet())
        {
            System.out.print(" "+" "+s.getKey()+"="+s.getValue());
        }
        System.out.println(">");
        for(Node n:childList)
        {
            n.deepPrint(space+ONETAB);
        }
        System.out.println(space+"</"+getName()+">");
    }
    public String getVariable(String key)
    {
        String varName=key.substring(key.lastIndexOf(".")+1);
        
        StringTokenizer dott=new StringTokenizer(key,".");
        Node p=null;
        String wrd=dott.nextToken();
        if(wrd.equals(varName))
        {
            p=this;
        }
        else
        {
            p=searchInParents(wrd);
            //System.out.println(p);
            if(p==null)
            return null;
            while(dott.hasMoreTokens())
            {
                String obj=dott.nextToken();
                if(obj.equals(varName))
                break;
                if(p==null)
                return null;
                else
                p=p.searchChild(obj);
            }
        }
        //System.out.println(p.toString());
        return p==null?null:(String)p.instanceVariables.getOrDefault(varName,null);
    }
    public boolean menu(String space,Scanner in)
    {
        System.out.println(space+"Entering "+getName());
        OUT:while(true)
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
                        defineVariable(toks);
                        break;
                    }
                    case "declare":
                    {
                        //add a node of specified name to childList of current node
                        Node n=new Node(toks.nextToken(),generation+1,god);
                        god.allNodes.add(n);
                        setChild(n);
                        break;
                    }
                    case "enter":
                    {
                        String name=toks.nextToken();
                        Node n=god.getNodeByName(name);
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
                    case "exit":
                    {
                        
                        return true;
                    }
                    case "XX":
                    {
                        return false;
                    }
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
                            String s=getVariable(t.substring(1));
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
    public void defineVariable(StringTokenizer toks)
    {
        god.readVariable(this,toks,"as");
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