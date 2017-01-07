import java.io.*;
class Msgap
{
    int len;
    byte content[];
    Msgap(String st)
    {
        len=st.length();
        content=new byte[len];
        for(int i=0;i<len;i++)
            content[i]=(byte)st.charAt(i);
    }
}

class Capsnd
{
    Capsnd(String s)
    {
        try{
            FileOutputStream fls=new FileOutputStream("wait");
            fls.write(1);
            fls.close();
            int n=s.length(),i;
            if(n==0){
                System.out.println("Insuffcient Arguments");
                return;
            }
            Msgap m1=new Msgap(s);            
            Msgbffc.update(true,4,1,m1.len,m1.content);
        }
        catch(Exception e){
            System.out.println("Exception in Capsnd !!"+e);
        }
    }
}       
