import java.util.*;
import java.io.*;
import java.net.*;
class Cdata
{
    static byte version;
    static byte keyex,calg,mac;
    static byte flag,tflag,dok; //flags
    static byte mast_sec1[]=new byte[48];
    static byte finish[]=new byte[36];
    static byte hand_al;
    static byte certi_alert;
    static byte fatal;
    static byte msgdig;
    static byte nb[]=new byte[10];
    static byte na[]=new byte[10];
    static int dummy;
}
class Key
{
    static int n;
    static int e;
}
class Current1
{
    static byte cur_cipalg;
    static byte cur_macalg;
    static byte cur_ctype;
    static byte cur_isexport;
    static byte cur_hashsize;
    static byte cur_keymat;
    static byte cur_iv;
}
class Mssg{
    byte type;
    int len;
    byte content[];
    static byte chello[]=new byte[32];
    static byte shello[]=new byte[32];
    static byte cw_ms[]=new byte[16];
    byte mast_sec[]=new byte[48];
    Mssg(byte t,int l){
        type=t;
        len=l;
        content=new byte[len-5];
        System.out.println("\n Mssg con"+t);
    }
    Mssg(int i){
        type=(byte) i;
        switch(type){
            case 1:
                len=48;                
                content=new byte[43];
                content[0]=Cdata.version;     //version
                Date d=new Date();
                int tm=(int) d.getTime();
                for(i=1;i<=4;i++){
                    content[i]=(byte) (tm>>>(32-(i*8)));//32-bit time stamp
                    chello[i-1]=content[i];
                }
                for(i=5;i<33;i++){
                    content[i]=(byte) (Math.random()*256);//28-byte random no.
                    chello[i-1]=content[i];
                }
                content[34]=0; //session id.
                content[35]=Cdata.keyex; //rsa,key exchange
                content[36]=Cdata.calg; //cipher algos
                content[37]=Cdata.mac; //macalgorithm...md5/sha-1
                content[38]=1; //cipher type---stream/block
                content[39]=0; //isExportable--t or f
                content[40]=20; //hashsize..for sha-1
                content[41]=0;  //key-material.
                content[42]=64; //IV block size.
                break;

            case 6:
                Alert a1=new Alert(Ser_certi1.name);
                String st="";
                byte ym=Cdata.certi_alert;
                System.out.println("certi alert"+Cdata.certi_alert);
                if(ym>0)
                {
                    switch(ym)
                    {
                        case 1:
                            st="unsupported certificate";
                            break;
                        case 2:
                            st="certificate unknown";
                            break;
                        case 3:
                            st="certificate expired";
                            break;
                    }
                    content =new byte[st.length()];
                    len=st.length()+5;
                    for(i=0;i<st.length();i++)
                        content[i]=(byte)st.charAt(i);
                }
                else
                {
                    st="no certificate alert";
                    content =new byte[st.length()];
                    len=st.length()+5;
                    for(i=0;i<st.length();i++)
                        content[i]=(byte)st.charAt(i);
                 }
                 System.out.println("st"+st);
                 break;

            case 7:         //client key exchange

                byte b1[]=new byte[48];
                long cipher = 1,temp;
                String str="";int j=0;
                byte dm[]=new byte[400];
                for(i=0;i<48;i++){
                    b1[i]=(byte)(Math.random()*128);// generates 48 byte master secret
                    for(int i1=1;i1<=Key.e;i1++){
                        temp=b1[i]%Key.n;     //master secret encrypted with RSA key
                        cipher=(cipher*temp)%Key.n;
                    }
                    while(cipher>0){
                        dm[j++]=(byte)(cipher%10);
                        cipher=cipher/10;
                    }
                    dm[j++]=(byte)'!';
                    cipher=1;
                }
                len=j+5;
                content =new byte[len-5];
                for(i=0;i<len-5;i++) 
                    content[i]=dm[i];
                //creating master secret key using pre master secret
                byte inter1_key[] =new byte[105];
                inter1_key[0]=(byte)'A';
                for(i=1;i<=48;i++)
                    inter1_key[i]=b1[i-1];
                for(i=49;i<77;i++)
                    inter1_key[i]=chello[i-49];
                for(i=77;i<105;i++)
                    inter1_key[i]=shello[i-77];
                Sha1 s1=new Sha1(inter1_key);

                inter1_key=new byte[106];
                inter1_key[0]=(byte)'B';
                inter1_key[1]=(byte)'B';
                for(i=2;i<50;i++)
                    inter1_key[i]=b1[i-2];
                for(i=50;i<78;i++)
                    inter1_key[i]=chello[i-50];
                for(i=78;i<106;i++)
                    inter1_key[i]=shello[i-78];
                Sha1 s2=new Sha1(inter1_key);

                inter1_key=new byte[107];
                inter1_key[0]=(byte)'C';
                inter1_key[1]=(byte)'C';
                inter1_key[2]=(byte)'C';
                for(i=3;i<51;i++)
                    inter1_key[i]=b1[i-3];
                for(i=51;i<79;i++)
                    inter1_key[i]=chello[i-51];
                for(i=79;i<107;i++)
                    inter1_key[i]=shello[i-79];
                Sha1 s3=new Sha1(inter1_key);

                byte md_hash[]=new byte[68];
                for(i=0;i<48;i++)
                    md_hash[i]=b1[i];//getting the pre-mast secret

                for(i=48;i<68;i++)
                    md_hash[i]=s1.msgd[i-48];
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
                   Cdata.mast_sec1[i]=mast_sec[i];

          case 8:
                    //CONSTRUCTING CIPHER SPEC    
                    len=6;
                    content=new byte[1];
                    content[0]=1;
                    break;

          case 9:   //CONSTRUCTING FINISHED MSG.
                    len=41;
                    content=new byte[36];
                    for(i=0;i<36;i++)
                    content[i]=Cdata.finish[i];
                    break;
                    
        }        
    }
    public void compare()
    {
        int temp=0;
        switch(type){
            case 2: //comparing contents of server hello with Client data
                int i;
                for(i=1;i<=4;i++){
                     temp=temp<<8;
                     temp=temp|content[i];
                }
                for(i=1;i<33;i++)
                    shello[i-1]=content[i];
                Date d=new Date();
                int tm=(int) d.getTime();
                temp=temp+60000; 
                if(temp>=tm)
                    Cdata.tflag=1;
                else
                    Cdata.tflag=0;
                //comparing cipher suite
                if(content[0]<=Cdata.version && content[35]<=Cdata.keyex &&
                   content[36]<=Cdata.calg && content[37]<=Cdata.mac )
                    Cdata.flag=1;
                else
                    Cdata.flag=0;
                break;

            case  3:
                int sym=0,j=0,k=0;
                String st1="",st2="",st3="";
                String st="";
                k=-1;char t1=0,c1=0;
                for(i=0;sym<17;i++){
                    c1=0;k=k+2;
                    for(j=1;j>=0;j--){
                       t1=(char)content[k--];
                       t1=(char)(t1 & (char)0xff);
                       c1=(char)(c1 | t1);
                       c1=(char) (c1<<j*8); 
                    }k=k+2;
                    st=st+c1;
                    System.out.print(c1+"sym"+sym);
                    if(c1=='!'){
                        sym++;
                    }
                }
                String hj="";
                hj=hj+st;
                sym=0;
                j=0;;int p=0;
                for(p=0;sym<17;p++){
                    if(st.charAt(p)=='!'){
                        sym++;
                    }
                    else st1=st1+st.charAt(p);
                    if(sym==3 && st.charAt(p)!='!')
                        Key.n=(int) st.charAt(p);
                    if(sym==4 && st.charAt(p)!='!')
                        Key.e=(int) st.charAt(p);
                }

                //CALCULATING THE HASH
                Md5 jm=new Md5(st1);
                String tmp="";
                tmp=tmp+jm.tempout;
                Sha1 sh=new Sha1(st1); 
                tmp+=sh.out2;             
                byte rxhash[]=new byte[tmp.length()*2];
                k=k+1;
                if(sym==17){
                   for(int lp=0;lp<tmp.length()*2;lp++){
                        rxhash[lp]=content[k++];
                    }
                }
                int i1=0,k1,a=0;
                st=tmp;st1="";
                for(int i4=0;i4<=((rxhash.length)/2) -1;i4++)
                {
                    i1=0;a=0;
                    for(int k4=1;k4>=0;k4--)
                    {
                        i1=i1<<8;
                        a=rxhash[(i4*2)+k4];
                        a=a & 0xff;
                        i1=i1|a;
                    }
                    k1=i1;
                    for(int i2=0;i2<Key.e-1;i2++)
                        i1=(i1*k1)%Key.n;
                    st1=st1+i1;
                }
                String tr="";
                for(int u=0;u<tmp.length();u++)
                    tr=tr+(int)tmp.charAt(u);
                if(!tr.equals(st1))
                {
                    System.out.print("hash mismatch"+j);
                    System.exit(0);
                }
                else
                    System.out.println("hash match");
                sym=0;
                int day,month,year;
                int temp1=0;temp=0;
                for(p=0;sym<17;p++){
                    if(hj.charAt(p)=='!'){
                        sym++;
                    }
                    else
                    { switch(sym)
                      {
                            case 6:
                                Cdata.nb[0]=(byte) hj.charAt(p);
                                break;
                            case 7:
                                Cdata.nb[1]=(byte) hj.charAt(p);
                                break;
                            case 8:
                                Cdata.nb[2]=(byte) hj.charAt(p);
                                break;
                            case 9:
                                Cdata.nb[3]=(byte) hj.charAt(p);
                                break;
                            case 10:
                                Cdata.na[0]=(byte) hj.charAt(p);
                                break;
                            case 11:
                                Cdata.na[1]=(byte) hj.charAt(p);
                                break;
                            case 12:
                                Cdata.na[2]=(byte) hj.charAt(p);
                                break;
                            case 13:
                                Cdata.na[3]=(byte) hj.charAt(p);
                                break;
                        }
                    }
                }
            
                Calendar c=Calendar.getInstance();
                day=c.get(Calendar.DATE);
                month=c.get(Calendar.MONTH);
                year=c.get(Calendar.YEAR);
                byte y[]=new byte[2];
                y[0]=04;y[1]=20;
                /*for(int l=0;l<=1;l++){
                    y[l]=(byte) (year >>> (l*8));
                }*/
                /*
                for(int l=2;l<=3;l++){
                    temp1=temp1<<8;
                    temp1=temp1 & 0xffff;
                    temp1=temp1|Cdata.na[l];
                }*/
                if(day>Cdata.nb[0] && month>Cdata.nb[1] && y[0]>=Cdata.nb[3] && y[1]>=Cdata.nb[2]){
                    if(day<Cdata.na[0] && month<Cdata.na[1] && Cdata.na[3]> y[0] && Cdata.na[2]>=y[1]){
                        Cdata.dok=1;
                    }
                }
                else
                    Cdata.dok=0;
                Cdata.na[4]=(byte) day;Cdata.na[5]=(byte) month;Cdata.na[6]= y[0];Cdata.na[7]= y[1];
                break;

            case 4: //action after recieving certificate request
                break;
            case 5: //action after recieving server hello done
                break;            
        }
    }
}

public class Hcl{
    static Mssg m1[]=new Mssg[14];
    int mn=0;
    static  void pend()
    {
        Current1.cur_cipalg=m1[2].content[36];
        Current1.cur_macalg=m1[2].content[37];
        Current1.cur_ctype=m1[2].content[38];
        Current1.cur_isexport=m1[2].content[39];
        Current1.cur_hashsize=m1[2].content[40];
        Current1.cur_keymat=m1[2].content[41];
        Current1.cur_iv=m1[2].content[42];
    }
    static int f_alert(int t,int len,byte[] content,int i1) throws Exception
    {
       int rt=0;
       Alert a2=new Alert(t,len,content,i1);
       if(Cdata.fatal==1)
       {
            //sending alert
             content=new byte[2];
              m1[t].content[0]=a2.level;
              m1[t].content[1]=a2.alert;
              m1[t].len=7;
              m1[t].type=(byte)t;
              rt=1;
        }
        return rt;
    }
    static void faction()
    {
        try{
            FileOutputStream fs1=new FileOutputStream("fatalaction");
            fs1.write(1);
            fs1.close();
            System.exit(0);
        }
        catch(Exception e){
            System.out.println("\n Exception in faction"+e);
        }
    }
    Hcl(){        
        Cdata.version=3;
        Cdata.keyex=1;
        Cdata.calg=1;
        Cdata.mac=2;
        try{
            //sending client _hello to server
            FileOutputStream fos=new FileOutputStream("cflag");
            fos.write(0);
            fos.close();
            m1[1] = new Mssg(1);
            Msgbffc.update(false,1,1,m1[1].len,m1[1].content);
            Clifrm.l_status[mn++].setText("Sent client hello ");

            //recieving msgs from server
            int i;
            int i1=2,f=0;
            while(i1<=5){                
                while(f==0){
                    FileInputStream fis=new FileInputStream("cflag");
                    f=fis.read();
                    fis.close();
                }
                f_read();f=0;
                m1[i1].compare();

                if(i1==2)
                {  Alert a1=new Alert(i1);
                    if(Cdata.hand_al==1);
                       System.exit(0);                   
                }
                if(f_alert(m1[i1].type,m1[i1].len-5,m1[i1].content,i1)>0)
                {          
                    //printing alert
                    faction();
                }                 
                i1++;
                Clifrm.l_status[mn++].setText("Recieved type "+(i1-1)+" message"); 
            }

            //sending no certificate alert to server
            m1[6]=new Mssg(6);
            Msgbffc.update(false,1,6,m1[6].len,m1[6].content);
            Clifrm.l_status[mn++].setText("Sent no certificate alert");

            //sending pre master
            m1[7]=new Mssg(7);
            Msgbffc.update(false,1,7,m1[7].len,m1[7].content);
            Clifrm.l_status[mn++].setText("Sent pre master secret");

            int mlen=0,j,j1,j2,k;
            //finished msg.
            //taking sender code as 12
            //calculating msg. length
            for(i=1;i<=7;i++){
                mlen=mlen+m1[i].len-5;
            }
            int m12=mlen;
            //input byte length for md5
            mlen=mlen+1+48+48;
            byte b2[]=new byte[mlen];
            for(i=1,j=0;i<=7;i++)
            {
                for(k=0;k<m1[i].len-5;k++)
                b2[j++]=m1[i].content[k];
            }
            //inserting sender id
            b2[j++]=12;
            for(i=0;i<48;i++)
                b2[j++]=Cdata.mast_sec1[i];
            for(i=0;i<48;i++)
                b2[j++]=0x36;

            Md5 md1=new Md5(b2);
            //48+48+16
            byte b1[]=new byte[112];
            j=0;
            for(i=0;i<48;i++)
                b1[j++]=Cdata.mast_sec1[i];
            for(i=0;i<48;i++)
                b1[j++]=0x5c;
            for(i=0;i<16;i++)
                b1[j++]=md1.msgd[i];
            Md5 md2=new Md5(b1);

            //calculating sha
            //size of array=hand shake  msg.s+1+48+40
            m12=m12+1+48+40;
            byte h2[]=new byte[m12];
            for(i=1,j=0;i<=7;i++)
            {
                for(k=0;k < (m1[i].len-5) ;k++)
                    h2[j++]=m1[i].content[k];
            }
            //inserting sender id
            h2[j++]=12;
            for(i=0;i<48;i++)
                h2[j++]=Cdata.mast_sec1[i];
            for(i=0;i<40;i++)
                h2[j++]=0x36;
            Sha1 sa1=new Sha1(h2);
            //48+40+20
            byte h1[]=new byte[108];
            j=0;
            for(i=0;i<48;i++)
                h1[j++]=Cdata.mast_sec1[i];
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
            for(i=0;i<36;i++)
            {
                System.out.print(" "+b3[i]);
                Cdata.finish[i]=b3[i];
            }

             //8 SENDING CIPHER SPEC. MSG.
            m1[8]=new Mssg(8);
            Msgbffc.update(true,1,8,m1[8].len,m1[8].content);
            Clifrm.l_status[mn++].setText("Sent cipher spec. message");

            //FINISHED MSG.
            m1[9]=new Mssg(9);
            Msgbffc.update(true,1,9,m1[9].len,m1[9].content);
            Clifrm.l_status[mn++].setText("Sent finished message");

            //10 RXING CIPHER SPEC.
            System.out.println("Reached here");
            while(f==0){
                FileInputStream fis=new FileInputStream("cflag");
                f=fis.read();                
                fis.close();
            }
            pend();
            f_read();f=0;
            Clifrm.l_status[mn++].setText("\nRxed cipher spec");
            if(f_alert(m1[10].type,m1[10].len-5,m1[10].content,10)>0){
                    //printing alert
                    faction();
                    System.out.println("in the state to receive finished msg.");
            }

            //rxing finished msg from server
            while(f==0){
                FileInputStream fis=new FileInputStream("cflag");
                f=fis.read();                
                fis.close();
            }
            f_read();f=0;
            Clifrm.l_status[mn++].setText("Received finished message");
            if(f_alert(m1[11].type,m1[11].len-5,m1[11].content,11)>0){
                    faction();
            }

            //writing client master secret into file named clnt_secpar
            FileOutputStream fout12=new FileOutputStream("clnt_secpar");
            for(int y=0;y<48;y++)
                fout12.write(Cdata.mast_sec1[y]);
            fout12.close();

            int flag=0;
            for(i=0;i<36;i++)  {
                if(m1[8].content[i]!=m1[9].content[i]) {
                    flag=1;
                    break;
                 }
            }

            byte keybl[]=new byte[48];
            h1=new byte[113];
            h1[0]=(byte)'A';
            for(i=1;i<49;i++)
                h1[i]=Cdata.mast_sec1[i-1];
            for(i=49;i<81;i++)
                h1[i]=Mssg.shello[i-49];
            for(i=81;i<113;i++)
                h1[i]=Mssg.chello[i-81];

            sa1=new Sha1(h1);
            h1=new byte[68];
            for(i=0;i<48;i++)
                h1[i]=Cdata.mast_sec1[i];
            for(i=48;i<68;i++)
                h1[i]=sa1.msgd[i-48];
            md1=new Md5(h1);

            h1=new byte[114];
            h1[0]=(byte)'B';
            h1[1]=(byte)'B';
            for(i=2;i<50;i++)
                    h1[i]=Cdata.mast_sec1[i-2];
            for(i=50;i<82;i++)
                h1[i]=Mssg.shello[i-50];
            for(i=82;i<114;i++)
                h1[i]=Mssg.chello[i-82];

            System.out.println("Client before verification");
            Alert a12=new Alert(11);
            if(Cdata.hand_al==1)
            {
                System.out.println("handshake failure because of different client and server master keys obtained");
                System.exit(0);
            }
            sa1=new Sha1(h1);
            h1=new byte[68];
            for(i=0;i<48;i++)
                h1[i]=Cdata.mast_sec1[i];
            for(i=48;i<68;i++)
                h1[i]=sa1.msgd[i-48];
            md2=new Md5(h1);

            h1=new byte[115];
            h1[0]=(byte)'C';
            h1[1]=(byte)'C';
            h1[2]=(byte)'C';
            for(i=3;i<51;i++)
                h1[i]=Cdata.mast_sec1[i-3];
            for(i=51;i<83;i++)
                h1[i]=Mssg.shello[i-51];
            for(i=83;i<115;i++)
                h1[i]=Mssg.chello[i-83];
            sa1=new Sha1(h1);
            h1=new byte[68];
            for(i=0;i<48;i++)
                h1[i]=Cdata.mast_sec1[i];
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
                Mssg.cw_ms[i]=md1.msgd[i];
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
                fc_wk[i]=Mssg.chello[i-5];
            for(i=37;i<69;i++)
                fc_wk[i]=Mssg.shello[i-37];
            md1=new Md5(fc_wk);
            for(i=0;i<5;i++)
                fs_wk[i]=sw_key[i];
            for(i=5;i<37;i++)
                fs_wk[i]=Mssg.shello[i-5];
            for(i=37;i<69;i++)
                fs_wk[i]=Mssg.chello[i-37];
            md2=new Md5(fs_wk);

            for(i=0;i<32;i++)
                h1[i]=Mssg.chello[i];
            for(i=32;i<64;i++)
                h1[i]=Mssg.shello[i-32];
            md1=new Md5(h1);
            for(i=0;i<7;i++)
                cw_iv[i]=md1.msgd[i];


            //writing client mac secret into a file
            FileOutputStream fout1=new FileOutputStream("cmac");
            for(int u=0;u<16;u++)
                fout1.write(cw_ms[u]);
            fout1.close();

            //writing client key into a file
            fout1=new FileOutputStream("ckey");
            for(int u=0;u<5;u++)
                fout1.write(cw_key[u]);
            fout1.close();
                           
            for(i=0;i<32;i++)
                h1[i]=Mssg.shello[i];
            for(i=32;i<64;i++)
                h1[i]=Mssg.chello[i-32];
            md1=new Md5(h1);
            for(i=0;i<7;i++)
                sw_iv[i]=md1.msgd[i];
            Clifrm.l_status[1].setText("Finished handshake successfully ");
            System.out.println("Finished handshake successfully ");
        }                                      
        catch(Exception e){
            String abcd=new String("Exc Hsr m : "+e);
            abcd=Clifrm.l_status[mn++].getText()+abcd;
            Clifrm.l_status[mn++].setText(abcd);
        }
    }
    static void f_read(){
        try{
            FileInputStream fis=new FileInputStream("crec");
            int t=fis.read();
            int l=0,b,j;
            for(int w=0;w<4;w++){
                b=fis.read();
                l=l<<8;
                l=l|b;
            }
            System.out.println("In Hcl f_read  l="+l+" t="+t);
            m1[t]=new Mssg((byte) t,l);
            for(j=0;j<l-5;j++){ //while(b!=-1){
                b=(byte) fis.read();
                if(b!=-1)
                    m1[t].content[j]=(byte)b;
            }

            fis.close();
            FileOutputStream fos=new FileOutputStream("wait");
            fos.write(1);
            fos.close();
            fos=new FileOutputStream("cflag");
            fos.write(0);
            fos.close();

        }
        catch(Exception e){
            Clifrm.l_status[9].setText("Exception in Hcl f_read() "+e);
        }        
    }
}
