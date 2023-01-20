import java.util.*;
import java.io.*;

class Node
{
    static final String ONETAB="    ";
    ArrayList<Node> childList=new ArrayList<Node>();
    HashMap<String,String> instanceVariables=new HashMap<String,String>();
    String name;
    String content="";
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
    /**
     * 
     * @param n Node to read variable into
     * @param stt StringTokenizer object from which to read variable
     * @param assigner keyword used to indicate that value is assigned to key  (like in int a=6, = is assigner)
     * @return String array containing the added key and value
     */
    public String[] readVariable(StringTokenizer stt,String assigner)
    {
        String line="";
        while(stt.hasMoreTokens())
        {
            line+=stt.nextToken();
            if(line.charAt(line.length()-1)==';')
            {
                line=line.substring(0, line.length()-1);
                break;
            }
        }
        
        int eq=line.indexOf(assigner);
        String key=line.substring(0,eq);
        String value=line.substring(eq+assigner.length(),line.length());
        if(value.charAt(0)=='&')
        {
            //variable is not a value, but a reference to it
            value=getVariableFromAddress(value.substring(1));
        }
        //log("Adding to "+n.name+" key="+key+" value="+value);
        instanceVariables.put(key,value);
        return new String[]{key,value};
    }
    public void log(String s)
    {
        System.err.println(s);
    }
    public void scanChildren(StringTokenizer st)
    {
        while(true)
        {
            String token=st.nextToken();
            switch(token)
            {
                case "node":
                {
                    String name=st.nextToken();
                    Node child=new Node(name,generation+1,god);
                    god.allNodes.add(child);
                    setChild(child);
                    if(st.nextToken().equals("{"))
                    child.scanChildren(st);
                    break;
                }
                case "{"://should never be found like this
                {
                    log("ERROR in parsing");
                    break;
                }
                case "}":
                {
                    return;
                }
                case "def":
                {
                    readVariable(st,"=");
                    break;
                }
                case "content:":
                {
                    String line="";
                    while(st.hasMoreTokens())
                    {
                        line+=st.nextToken();
                        if(line.charAt(line.length()-1)==';')
                        {
                            line=line.substring(0, line.length()-1);
                            break;
                        }
                    }
                    content=line;
                    break;
                }
                default:
                    System.out.println("Unknown: "+token);
                    break;
            }
        }
    }
    public void deepPrint(String space)
    {
        System.out.print(space+"<"+getName());
        for(Map.Entry<String,String> s:instanceVariables.entrySet())
        {
            System.out.print(" "+" "+s.getKey()+"=\""+s.getValue()+"\"");
        }
        System.out.println(">");
        if(content.length()!=0)
        System.out.println(space+ONETAB+content);
        for(Node n:childList)
        {
            n.deepPrint(space+ONETAB);
        }
        System.out.println(space+"</"+getName()+">");
    }
    /**
     * 
     * @param key address of variable, doesnt include the prefix &
     * @return variable value or null
     */
    public String getVariableFromAddress(String key)
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
                        String[] assig=readVariable(toks,"as");
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
                    case "content":
                    {
                        String cont="";
                        while(toks.hasMoreTokens())
                        {
                            cont+=toks.nextToken();
                        }
                        cont=cont.replace("$$",content);
                        content=cont;
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
                    case "remove":
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
                    case "X":
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
                            String s=getVariableFromAddress(t.substring(1));
                            System.out.println(space+s);
                        }
                        else
                        {
                            //function call
                            StringTokenizer f=new StringTokenizer(t,"() ,");
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
        if(content.length()>0)
        pw.println(space+ONETAB+"content: "+content+";");
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