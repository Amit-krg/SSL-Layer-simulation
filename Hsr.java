import java.io.*;
import java.net.*;
import java.util.*;
class Srvrdata{
    static final byte version=3;
    static final byte key_ex=1;
    static final byte ciph_alg=1;
    static final byte mac_alg=3;
    static int d;
    static int n;
    static byte mast_sec1[]=new byte[48];
    static byte finish[]=new byte[36];
    static byte msgdig;
    static byte hand_al;
    static String temp1;
}
class Current
{
    static byte cur_cipalg;
    static byte cur_macalg;
    static byte cur_ctype;
    static byte cur_isexport;
    static byte cur_hashsize;
    static byte cur_keymat;
    static byte cur_iv;
}
class Ser_certi
{        
    static byte version=1;         //default value=1
    static int Serial_no=2;        //unique,issued by the CA
    static String sig_algo_name="MD5 SHA-1";
    static int n1=229193,e1=85;//certificate authority's public key
    static String  name="CA";       //x.500 name of the CA.
    static byte nb[]={1,1,20,4};   //new byte[4]; //period of validity
    static byte na[]={1,1,20,5};
    static String sub_name="SSL";//name of the user 2 whom this certificate refers
    static int n,e;
    byte sub_alg;  //public key of subject plus alg.id for which this key is used
    static String sig_alg="MD5 SHA-1";
    static byte hash[];
    static String stg="";
    static int jk[]=new int[200];
    Ser_certi(int a,int b,int c){
        //calculate HASH from MD5 SHA-1
        String t1="";
        n=a;
        e=b;
        t1=t1+(char) version;
        String s1=t1+(char)Serial_no+sig_algo_name+(char)n+(char)e+name+(char)nb[0]
                +(char)nb[1]+(char)nb[2]+(char)nb[3]+(char)na[0]+(char)na[1]
                +(char)na[2]+(char)na[3]+sub_name+(char)sub_alg+sig_alg;
        stg=s1;
        Md5 jm=new Md5(s1);
        String tmp="";
        tmp=tmp+jm.tempout;
        System.out.println("out of md5  "+jm.tempout.length());
        Sha1 sh=new Sha1(s1); 
        tmp+=sh.out2;
        stg=tmp;
        // SIGNATURE OF THE CERTIFICATE IS tmp which has to be encrypted by ca's private key
        int d=c;
        Srvrdata.n=a;
        Srvrdata.d=c;
        s1=tmp;
        String st=tmp,st1="";
        hash=new byte[st.length()*2];
        int k1;
        jk=new int[st.length()];
        for(int i4=0,i=0;i4<st.length();i4++)
        {
            int i1=(st.charAt(i4));
            i1=i1 & 0xffff;
            k1=i1;
            Ser_certi.jk[i4]=i1;
            for(int i2=0;i2<d-1;i2++)
                i1=(i1*k1)%n;

             for(int k2=0;k2<=1;k2++)
                    hash[i++]=(byte)(i1>>>k2*8);
            st1=st1+(char)i1;
            i1=0;
        }
    }
}
class Msg{
    byte type;
    int len;
    byte content[];
    static byte chello[]=new byte[32];
    static byte shello[]=new byte[32];
    byte mast_sec[]=new byte[48];     
    long rev(long a,int zf){
        long r=0;
        while(a>0){
            r=(r*10)+(a%10);
            a=a/10;
        }
        if(zf<10)
            while(zf>0){
                r*=10;
                zf--;
            }
        return r;
    }
    Msg(byte t, int l){
        type=t;
        len=l;
        content=new byte[l-5];
    }
    Msg(int i,Msg m){
        type=(byte)i;
        switch(type){
            case 2://rxing client hello n sending server hello
                len=48;
                for(i=1;i<33;i++)
                    chello[i-1]=m.content[i];
                content=new byte[43];
                if(m.content[0]<=Srvrdata.version)
                    content[0]=m.content[0];        //version
                else
                    content[0]=Srvrdata.version;
                Date d=new Date();
                int tm=(int) d.getTime();
                for(i=1;i<=4;i++){
                    content[i]=(byte) (tm>>>(32-(i*8)));//32-bit time stamp
                    shello[i-1]=content[i];
                }
                for(i=5;i<33;i++)
                {
                    content[i]=(byte) (Math.random()*256);//28-byte random no.
                    shello[i-1]=content[i];
                }
                if(m.content[34]==0)    //session id verification
                    content[34]=(byte) (Math.random()*256);
                else
                    content[34]=m.content[34];                
                content[35]=m.content[35]; //rsa,key exchange
                content[36]=m.content[36]; //cipher algos
                content[37]=3;  //macalgorithm...md5/sha-1
                content[38]=m.content[38]; //cipher type---stream/block
                content[39]=m.content[39]; //isexportable--t or f
                content[40]=m.content[40]; //hashsize..for sha-1
                content[41]=m.content[41]; //key-material.
                content[42]=m.content[42]; //IV block size.
                break;
            case 3:
               //sending server's certi to client
                Rsa1 rs=new Rsa1();
                Ser_certi sc=new Ser_certi(rs.nr,rs.er,rs.dr);
                String s1=(char)sc.version+"!"+(char)sc.Serial_no+"!"+sc.sig_algo_name+"!"+(char)sc.n+"!"+(char)sc.e+"!"+sc.name+ "!"+(char)sc.nb[0]
                    +"!"+(char)sc.nb[1]+"!"+(char)sc.nb[2]+"!"+(char)sc.nb[3]+"!"+(char)sc.na[0]+"!"+(char)sc.na[1]
                    +"!"+(char)sc.na[2]+"!"+(char)sc.na[3]+"!"+sc.sub_name+"!"+(char)sc.sub_alg+"!"+sc.sig_alg+"!";
                Srvrdata.temp1=s1;
                len=s1.length()*2+5+sc.stg.length()*2;//72 for the packed hash
                content=new byte[len-5];
                int k=0;
                for(i=0;i<s1.length();i++)
                {
                    for(int j=0;j<=1;j++)
                    {
                        content[k++]=(byte)(s1.charAt(i)>>>j*8);
                    }
                }
                for(int j=0;j<sc.hash.length;j++)
                    content[k++]=sc.hash[j];
                    System.out.print("hash aray len "+sc.hash.length);
                for(i=0;i<sc.hash.length;i++)
                    System.out.print(" "+sc.hash[i]);
                break;
            case 4:
                s1="RSA!CA";
                len=s1.length()+5;
                content=new byte[len-5];
                for(i=0;i<len-5;i++)
                    content[i]=(byte)s1.charAt(i);
                break;

            case 5:
                s1="sever hello done";
                len=s1.length()+5;
                content=new byte[len-5];
                for(i=0;i<len-5;i++)
                    content[i]=(byte)s1.charAt(i);
                break;

           case 6:
                  break;

           case 7:
                int j;
                long k1=0,temp,rslt=1;
                String st="";

                //decrypting the pre master secret
                int i1=0,zc=0;
                byte br[]=new byte[48];
                for(j=0;j<48;j++)  {
                    while(m.content[i1]!='!'){
                        if(zc==0)
                            while(m.content[i1]==0){
                                zc++;i1++;
                            }
                        if(zc==0)
                            zc =10;
                        k1=(k1*10)+(m.content[i1]);
                        i1++;
                    }
                    k1=rev(k1,zc);
                    zc=0;
                    for(i=1;i<=Srvrdata.d;i++){
                        temp=k1%Srvrdata.n;
                        rslt=(rslt*temp)%Srvrdata.n;
                    }
                    br[j]=(byte)(rslt);
                    i1++;k1=0;rslt=1;
                }
                //creating master secret key using pre master secret
                byte inter1_key[] =new byte[113];
                inter1_key[0]=(byte)'A';
                for(i=1;i<=48;i++)
                    inter1_key[i]=br[i-1];
                for(i=49;i<81;i++)
                    inter1_key[i]=chello[i-49];
                for(i=81;i<113;i++)
                    inter1_key[i]=shello[i-81];
                Sha1 sh1=new Sha1(inter1_key);

                inter1_key=new byte[114];
                inter1_key[0]=(byte)'B';
                inter1_key[1]=(byte)'B';
                for(i=2;i<50;i++)
                    inter1_key[i]=br[i-2];
                for(i=50;i<82;i++)
                    inter1_key[i]=chello[i-50];
                for(i=82;i<114;i++)
                    inter1_key[i]=shello[i-82];
                Sha1 s2=new Sha1(inter1_key);

                inter1_key=new byte[115];
                inter1_key[0]=(byte)'C';
                inter1_key[1]=(byte)'C';
                inter1_key[2]=(byte)'C';
                for(i=3;i<51;i++)
                    inter1_key[i]=br[i-3];
                for(i=51;i<83;i++)
                    inter1_key[i]=chello[i-51];
                for(i=83;i<115;i++)
                    inter1_key[i]=shello[i-83];
                Sha1 s3=new Sha1(inter1_key);

                byte md_hash[]=new byte[68];
                for(i=0;i<48;i++)
                    md_hash[i]=br[i];//getting the pre-mast secret
                for(i=48;i<68;i++)
                    md_hash[i]=sh1.msgd[i-48];
                Md5 md1=new Md5(md_hash);

                for(i=48;i<68;i++)
                    md_hash[i]=s2.msgd[i-48];
                Md5 md2=new Md5(md_hash);

                for(i=48;i<68;i++)
                    md_hash[i]=s3.msgd[i-48];
                Md5 md3=new Md5(md_hash);

                for(i=0;i<16;i++)
                    mast_sec[i]=md1.msgd[i];
                for(i=16;i<32;i++)
                    mast_sec[i]=md2.msgd[i-16];
                for(i=32;i<48;i++)
                    mast_sec[i]=md3.msgd[i-32];
                for(i=0;i<48;i++)
                    Srvrdata.mast_sec1[i]=mast_sec[i];
                    break;

            case 9:
                len=41;
                content=new byte[36];
                for(i=0;i<36;i++)
                    content[i]=Srvrdata.finish[i];
                break;

            case 10:
                 len=6;
                 content=new byte[1];
                 content[0]=1;
                 System.out.println("the end");
                 break;

            case 11:   //CONSTRUCTING FINISHED MSG.
                len=41;
                content=new byte[36];
                for(i=0;i<36;i++)
                    content[i]=Srvrdata.finish[i];
                break;
        }
    }
}
public class Hsr{
    static Msg m1[]=new Msg[14];
    static public void pend()
    {
        Current.cur_cipalg=m1[2].content[36];
        Current.cur_macalg=m1[2].content[37];
        Current.cur_ctype=m1[2].content[38];
        Current.cur_isexport=m1[2].content[39];
        Current.cur_hashsize=m1[2].content[40];
        Current.cur_keymat=m1[2].content[41];
        Current.cur_iv=m1[2].content[42];
    }
    static int f_alert1(int t,int len,byte[] content,int i1) throws Exception
    {
       int rt=0;
       Alert a1=new Alert(t,len,content,i1);
       if(Cdata.fatal==1)
       {
            //sending alert
             content=new byte[2];
              m1[t].content[0]=a1.level;
              m1[t].content[1]=a1.alert;
              m1[t].len=7;
              m1[t].type=(byte)t;
              rt=1;
        }
        return rt;
    }
    static void faction1()
    {
        try{
            FileOutputStream fs1=new FileOutputStream("fatalaction");
            fs1.write(1);
            fs1.close();
            System.out.println("\nFATAL ERROR HAS OCCURED");
            System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("\n exception  in faction");
        }
    }
    int mn=0;
    Hsr() {
        try{            
            Msg msg,msg1;
            FileOutputStream fos1=new FileOutputStream("sflag");
            fos1.write(0);
            fos1.close();
            FileOutputStream fos=new FileOutputStream("wait");
            fos.write(1);
            fos.close();
            int k,i,f=0;
            Serfrm.l_status[mn++].setText("Entered server ");
            //receiving client hello
            while(f==0){
                FileInputStream fis=new FileInputStream("sflag");
                f=fis.read(); 
                fis.close();
            }
            f_read();f=0;
            Serfrm.l_status[mn++].setText("Received client hello");            

            //sending server hello by calling update
            m1[2]=new Msg(2,m1[1]);
            Msgbffs.update(false,1,2,m1[2].len,m1[2].content);
            Serfrm.l_status[mn++].setText("Sent server hello");

            //sending certificate
            m1[3]=new Msg(3,m1[1]);
            Msgbffs.update(false,1,3,m1[3].len,m1[3].content);
            Serfrm.l_status[mn++].setText("Sent server certificate");            

            //requesting client certificate
            m1[4]=new Msg(4,m1[0]);
            Msgbffs.update(false,1,4,m1[4].len,m1[4].content);
            Serfrm.l_status[mn++].setText("Sent certificate request");

            //5-server done message
            m1[5]=new Msg(5,m1[0]);
            Msgbffs.update(false,1,5,m1[5].len,m1[5].content);
            Serfrm.l_status[mn++].setText("Sent server done message");

            //6-rxing no-certi-alert
            while(f==0){
                FileInputStream fis=new FileInputStream("sflag");
                f=fis.read();                
                fis.close();
            }
            f_read();f=0;
            if(f_alert1(m1[6].type,m1[6].len-5,m1[6].content,6)>0)
            {
                    //printing alert
                    faction1();
            }
            Serfrm.l_status[mn++].setText("Recieved no certificate alert ");

            //7-rxingthe 48-byte pre master secret frm the client
            while(f==0){
                FileInputStream fis=new FileInputStream("sflag");
                f=fis.read();                
                fis.close();
            }
            f_read();f=0;
            if(f_alert1(m1[7].type,m1[7].len-5,m1[7].content,7)>0){
                    //printing alert
                    faction1();
            }
            Serfrm.l_status[mn++].setText("Recieved pre master");

            //msg. 8  rxing cipher spec.
            while(f==0){
                FileInputStream fis1=new FileInputStream("sflag");
                f=fis1.read();                
                fis1.close();
            }
            f_read();f=0;
            System.out.println("\n RXED CIPHER SPEC");
            pend();
            if(f_alert1(m1[8].type,m1[8].len-5,m1[8].content,8)>0){
                    //printing alert
                    faction1();
            }

            //9-rxing finished mesg.
            while(f==0){
                FileInputStream fis=new FileInputStream("sflag");
                f=fis.read();                
                fis.close();
            }
            f_read();f=0;
            if(f_alert1(m1[9].type,m1[9].len-5,m1[9].content,9)>0){
                    //printing alert
                    faction1();
            }
            Serfrm.l_status[mn++].setText("Recieved finished message");

            int mlen=0,j,j1,j2;
            //finished msg. being constructed by server
            //taking sender code as 12
            //calculating msg. length
            for(i=1;i<=7;i++)
            {
                mlen=mlen+m1[i].len-5;
            }
            int m12=mlen;
            //input byte length for md5
            mlen=mlen+1+48+48;
            byte b2[]=new byte[mlen];
            for(i=1,j=0;i<=7;i++){
                for(k=0;k<m1[i].len-5;k++)
                    b2[j++]=m1[i].content[k];
            }
            //inserting sender id
            b2[j++]=12;
            for(i=0;i<48;i++)
                b2[j++]=Srvrdata.mast_sec1[i];
            for(i=0;i<48;i++)
                b2[j++]=0x36;
            Md5 md1=new Md5(b2);
            //48+48+16
            byte b1[]=new byte[112];
            j=0;
            for(i=0;i<48;i++)
                b1[j++]=Srvrdata.mast_sec1[i];
            for(i=0;i<48;i++)
                b1[j++]=0x5c;
            for(i=0;i<16;i++)
                b1[j++]=md1.msgd[i];
            Md5 md2=new Md5(b1);

            //calculating sha
            //size of array=hand shake  msg.s+1+48+40
            m12=m12+1+48+40;
            byte h2[]=new byte[m12];
            for(i=1,j=0;i<=7;i++){
                for(k=0;k < (m1[i].len-5) ;k++)
                    h2[j++]=m1[i].content[k];
            }
            //inserting sender id
            h2[j++]=12;
            for(i=0;i<48;i++)
                h2[j++]=Srvrdata.mast_sec1[i];
            for(i=0;i<40;i++)
                h2[j++]=0x36;
            Sha1 sa1=new Sha1(h2);
            //48+40+20
            byte h1[]=new byte[108];
            j=0;
            for(i=0;i<48;i++)
                h1[j++]=Srvrdata.mast_sec1[i];
            for(i=0;i<20;i++)
                h1[j++]=0x5c;
            for(i=0;i<20;i++)
                h1[j++]=sa1.msgd[i];
            Sha1 sa2=new Sha1(h1);
            //concatenating md5 o/p n sha o/p
            byte b3[]=new byte[36];
            j=0;
            for(i=0;i<16;i++)
                b3[j++]=md2.msgd[i];
            for(i=0;i<20;i++)
                b3[j++]=sa2.msgd[i];
            for(i=0;i<36;i++){
                Srvrdata.finish[i]=b3[i];
                System.out.print(" "+Srvrdata.finish[i]);
            }

            //10  SENDING  CIPHER SPEC
            m1[10]=new Msg(10,m1[1]);
            System.out.println(" length!!!!"+m1[10].len);
            Msgbffs.update(false,1,10,m1[10].len,m1[10].content);
            Serfrm.l_status[mn++].setText("Sent cipher spec message");

            //11 SENDING FINISHED MSG.
            m1[11]=new Msg(11,m1[1]);
            System.out.println("\n"+"m1[11].type"+m1[11].type+m1[11].len+"m1[11].content"+m1[11].content);
            Msgbffs.update(false,1,11,m1[11].len,m1[11].content);
            Serfrm.l_status[mn++].setText("Sent finished message");
            System.out.println("\nSent finfished msg. server");

            int flag=0,u;
            for(i=0;i<36;i++){
                if(m1[9].content[i]!=m1[11].content[i]){
                    flag=1;
                    break;
                }
                //writing server computed master secret into file named srvr_secpar
                FileOutputStream fout12=new FileOutputStream("srvr_secpar");
                for(int y=0;y<48;y++)
                    fout12.write(Srvrdata.mast_sec1[y]);
                fout12.close();
            }

            //CONSTRUCTING KEY MATERIAL 
            byte keybl[]=new byte[48];
            h1=new byte[113];
            h1[0]=(byte)'A';
             for(i=1;i<49;i++)
                h1[i]=Srvrdata.mast_sec1[i-1];
            for(i=49;i<81;i++)
                h1[i]=Msg.shello[i-49];
            for(i=81;i<113;i++)
                h1[i]=Msg.chello[i-81];
            sa1=new Sha1(h1);
            h1=new byte[68];
            for(i=0;i<48;i++)
                h1[i]=Srvrdata.mast_sec1[i];
            for(i=48;i<68;i++)
                h1[i]=sa1.msgd[i-48];
            md1=new Md5(h1);
            Alert a12=new Alert(11);
            System.out.println("Reached here also");
            if(Srvrdata.hand_al==1)
            {
                System.out.println("handshake failure because of different client and server master keys obtained");
                System.exit(0);
            }

            h1=new byte[114];
            h1[0]=(byte)'B';
            h1[1]=(byte)'B';
            for(i=2;i<50;i++)
                h1[i]=Srvrdata.mast_sec1[i-2];
            for(i=50;i<82;i++)
                h1[i]=Msg.shello[i-50];
            for(i=82;i<114;i++)
                h1[i]=Msg.chello[i-82];
            sa1=new Sha1(h1);
            h1=new byte[68];
            for(i=0;i<48;i++)
                h1[i]=Srvrdata.mast_sec1[i];
            for(i=48;i<68;i++)
                h1[i]=sa1.msgd[i-48];
            md2=new Md5(h1);

            h1=new byte[115];
            h1[0]=(byte)'C';
            h1[1]=(byte)'C';
            h1[2]=(byte)'C';
            for(i=3;i<51;i++)
                h1[i]=Srvrdata.mast_sec1[i-3];
            for(i=51;i<83;i++)
                h1[i]=Msg.shello[i-51];
            for(i=83;i<115;i++)
                h1[i]=Msg.chello[i-83];
            sa1=new Sha1(h1);
            h1=new byte[68];
            for(i=0;i<48;i++)
                h1[i]=Srvrdata.mast_sec1[i];
            for(i=48;i<68;i++)
                h1[i]=sa1.msgd[i-48];
            Md5 md3=new Md5(h1);

            byte cw_ms[]=new byte[16];
            byte sw_ms[]=new byte[16];
            byte cw_key[]=new byte[5];
            byte sw_key[]=new byte[5];
            byte fc_wk[]=new byte[69];
            byte fs_wk[]=new byte[69];
            byte cw_iv[]=new byte[7];
            byte sw_iv[]=new byte[7];

            for(i=0;i<16;i++){
                keybl[i]=md1.msgd[i];
                cw_ms[i]=md1.msgd[i];
            }
            for(i=16;i<32;i++){
                keybl[i]=md2.msgd[i-16];
                sw_ms[i-16]=md2.msgd[i-16];
            }
            for(i=32;i<48;i++)
                keybl[i]=md3.msgd[i-32];

            for(i=32;i<37;i++)
                cw_key[i-32]=keybl[i-32];
            for(i=37;i<41;i++)
                sw_key[i-37]=keybl[i-37];
            for(i=0;i<5;i++)
                fc_wk[i]=cw_key[i];
            for(i=5;i<37;i++)
                fc_wk[i]=Msg.chello[i-5];
            for(i=37;i<69;i++)
                fc_wk[i]=Msg.shello[i-37];
            md1=new Md5(fc_wk);

            for(i=0;i<5;i++)
                fs_wk[i]=sw_key[i];
            for(i=5;i<37;i++)
                fs_wk[i]=Msg.shello[i-5];
            for(i=37;i<69;i++)
                fs_wk[i]=Msg.chello[i-37];
            md2=new Md5(fs_wk);

            for(i=0;i<32;i++)
                h1[i]=Msg.chello[i];
            for(i=32;i<64;i++)
                h1[i]=Msg.shello[i-32];
            md1=new Md5(h1);
            for(i=0;i<7;i++)
                cw_iv[i]=md1.msgd[i];

            //writing server mac secret into a file
            FileOutputStream fout1=new FileOutputStream("smac");
            for(u=0;u<16;u++)
                fout1.write(sw_ms[u]);
            fout1.close();

            //writing server key into a file
            fout1=new FileOutputStream("skey");
            for(u=0;u<5;u++)
                fout1.write(sw_key[u]);
            fout1.close();

            for(i=0;i<32;i++)
                h1[i]=Msg.shello[i];
            for(i=32;i<64;i++)
                h1[i]=Msg.chello[i-32];
            md1=new Md5(h1);
            for(i=0;i<7;i++)
                sw_iv[i]=md1.msgd[i];
            Serfrm.l_status[1].setText(" Handshake completed successfully ");
            System.out.println(" Handshake completed successfully ");
        }
        catch(Exception e){
            String abcd=new String("Exc Hsr m : "+e);
            abcd=Serfrm.l_status[mn++].getText()+abcd;
            Serfrm.l_status[mn++].setText(abcd);
        }
    }
    static void f_read(){
        try{
            FileInputStream fis=new FileInputStream("sflag");
            fis.close();
            fis=new FileInputStream("srec");
            int t=fis.read();           
            int l=0,b,j;
            for(int w=0;w<4;w++){
                b=fis.read();
                l=l<<8;
                l=l|b;
            }
            System.out.println("In Hsr fread  l="+l+" t="+t);
            m1[t]=new Msg((byte) t,l);
            for(j=0;j<l-5;j++){//while(b!=-1)
               b=(byte) fis.read();
                if(b!=-1)
                    m1[t].content[j]=(byte)b;
            }            
            fis.close();
            FileOutputStream fos=new FileOutputStream("wait");
            fos.write(1);
            fos.close();
            fos=new FileOutputStream("sflag");
            fos.write(0);
            fos.close();
        }
        catch(Exception e){
            Serfrm.l_status[9].setText("Exception in Hsr f_read()"+e);
        }
    }
}
