import java.io.*;
class Msgapp
{
    int len;
    byte content[];
    Msgapp(String st)
    {
        len=st.length();
        content=new byte[len];
        for(int i=0;i<len;i++)
            content[i]=(byte)st.charAt(i);
    }
}

class Sapsnd
{
    Sapsnd(String s)
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
            Msgapp m1=new Msgapp(s);            
            Msgbffs.update(true,4,1,m1.len,m1.content);
        }
        catch(Exception e){
            System.out.println("Exception in Sapsnd !!"+e);
        }
    }
}       
