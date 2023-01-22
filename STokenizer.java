import java.util.ArrayList;

class STokenizer
{
    final char[] delimiters;
    ArrayList<String> tokens=new ArrayList<String>();
    int pointer;
    STokenizer(String s,String delims)
    {
        this.delimiters=delims.toCharArray();
        extractTokens(s);
        
    }
    STokenizer(String s)
    {
        this.delimiters=new char[]{' '};
        extractTokens(s);
    }
    private void extractTokens(String s)
    {
        s+=delimiters[0];
        String bucket="";
        for(int i=0;i<s.length();i++)
        {
            char k=s.charAt(i);
            boolean delim=false;
            for(char c:delimiters)
            {
                if(k==c)
                delim=true;
            }
            if(delim)
            {
                if(!bucket.equals(""))
                {
                    tokens.add(bucket);
                    bucket="";
                }
            }
            else
            bucket+=k;
        }
    }
    String nextToken()
    {
        return tokens.get(pointer++);
    }
    boolean hasMoreTokens()
    {
        return (pointer<tokens.size());
    }
    public static void main(String[] args)
    {
        STokenizer st=new STokenizer("this is a() sentence()"," ()");
        while(st.hasMoreTokens())
        System.out.println("token:"+st.nextToken());
    }
}