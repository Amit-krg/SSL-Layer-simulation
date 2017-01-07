import java.util.*;
import java.io.*;
public class Alert
{
    static byte level;
    static byte alert;
    Alert(byte[] hashc,byte[] sh12)
    {
                        for(int i1=0;i1<20;i1++)
                        {
                            if(hashc[i1]!=sh12[i1])
                            {
                                System.out.println("call alert b/c msg. digest mismatch");
                                Cdata.msgdig=1;
                                break;
                            }
                        }
      }
    
    Alert(int type,int len,byte[] content,int exp_type) 
    {    //unexpected_msg
        try
        {                        
            if(type!=exp_type)
                Cdata.fatal=1;
            //illegal par
            if(len!=content.length || type > 11)
            {
                Cdata.fatal=3; 
            }       
            if(Cdata.fatal==1 || Cdata.fatal==3)
            {
                level=1;//fatal
                alert=(byte)Cdata.fatal;
            }
            else
                Cdata.fatal=0;
        }
        catch(Exception e)
        {
            System.out.println("alert exception"+e);
        }
    }

    Alert(int msgno)
    {
        try{
            FileInputStream f1=new FileInputStream("clnt_secpar");
            FileInputStream f2=new FileInputStream("srvr_secpar");
            int t1,t2;
            t1=t2=1;
            while(t1>=t2 && t1!=-1 &&t2!=-1) //handshake failure
            {
                t1=f1.read();
                t2=f2.read();
                if(t1<t2){
                    Cdata.hand_al=1;
                    Srvrdata.hand_al=1;
                }
            }
            f1.close();
            f2.close();
            if(Cdata.hand_al!=1)
            {
                 Cdata.hand_al=0;
                 Srvrdata.hand_al=0;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }
    Alert(String certi_authority) 
    {
        //unsupported_certi
        if(certi_authority.equals(" "))
            Cdata.certi_alert=1;
        //certi_unknown
        if(certi_authority.equals("RSA_data_security") || certi_authority.equals("INF_data_security")|| certi_authority.equals("ROUTING_data_security") ||certi_authority.equals("NETWORKING_data_security"))
            System.out.println("valis certificate authority");
        else
            Cdata.certi_alert=2;
        //certi_expired
        int day,month,year;
        byte y[]=new byte[2];
        Calendar c=Calendar.getInstance();
        day=c.get(Calendar.DATE);
        month=c.get(Calendar.MONTH);
        year=c.get(Calendar.YEAR);
        y[1]=(byte)year;
        y[0]=(byte)(year>>8);
        if(day>Ser_certi1.nb[0] && month>Ser_certi1.nb[1] && y[0]>Ser_certi1.nb[2] && y[1]>Ser_certi1.nb[3]){
            if(day<Ser_certi1.na[0] && month<Ser_certi1.na[1] && y[0]<Ser_certi1.na[2] && y[1]<Ser_certi1.na[3]){
                Cdata.certi_alert=0;
            }
        }
        else
            Cdata.certi_alert=3;
        if(Cdata.certi_alert==1  || Cdata.certi_alert==2 || Cdata.certi_alert==3)
        {
             level=0;//warning 
             alert=(byte)Cdata.certi_alert;
        }
    }         
}          

