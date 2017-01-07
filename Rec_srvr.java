import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;

class Header1{
    static byte ct;
    static byte majorv=3;
    static byte minorv=0;
    static short msgl;
}

public class Rec_srvr
{
    ServerSocket s;
    Socket rec;            
    BufferedReader br;
    PrintWriter pw;
    byte mg[];
    Rec_srvr(){
        try{
            s=new ServerSocket(8000,0);
            //ds.setSoTimeout(34000);
            rec=s.accept();            
            br=new BufferedReader
                (new InputStreamReader(rec.getInputStream()));
            pw=new PrintWriter(rec.getOutputStream(),true);
            int i,u1,u2,i2,n1,xyz1=0;
            boolean sr;
            if(Msgbffs.lg>0){
                sr=true;
                System.out.println("\nsending from rec servr");
                //verifying whether handshake completed or not
                if(Msgbffs.type==4)
                {
                    FileOutputStream fosc=new FileOutputStream("s_fl");
                    fosc.write(0);
                    fosc.close();
                    fosc=new FileOutputStream("ser_cip_s");
                    byte f=Msgbffs.cnt[Msgbffs.cnt.length-1];
                    int fl_len=0;
                    if(f==0){
                        String fn="";
                        for(i=0;i<Msgbffs.cnt.length-1;i++)
                            fn=fn+(char)(Msgbffs.cnt[i]);
                        System.out.println(fn);
                        FileInputStream f1=new FileInputStream(fn);
                        while(i!=-1){
                            i=f1.read();
                            if(i!=-1)
                                fl_len++;
                        }
                        f1.close();
                        f1=new FileInputStream(fn);
                        Msgbffs.cnt=new byte[fl_len];
                        for(i=0;i<fl_len;i++)
                            Msgbffs.cnt[i]=(byte)f1.read();
                        Msgbffs.lg=fl_len;
                    }
                    //fragment data to be sent
                    int nb=Msgbffs.lg/16384,k,j;
                    System.out.println("Number of blocks ="+(nb+1));
                    for(i=0;i<=nb;i++){
                        Serfrm.l_status[0].setText("Sending block "+(i+1)+"......");
                        if(i==nb)                    
                            n1=Msgbffs.lg-(nb*16384);
                        else
                            n1=16384;
                        mg=new byte[n1+20];
                        for(j=0;j<n1;j++){
                            mg[j]=Msgbffs.cnt[(i*16384)+j];
                        }
                        //first hash smac,pad1,seq num,type,length,fragment
                        //lenh=58+Msgbffs.lg+16384;
                        int lenh=60+mg.length-20;
                        byte fcont[]=new byte[lenh];

                        FileInputStream fin1=new FileInputStream("smac");
                        for(u1=0;u1<16;u1++)
                            fcont[u1]=(byte)fin1.read();   //write mac secret
                        fin1.close();
                        for(u2=0;u2<40;u2++)
                            fcont[u1++]=0x36;              //pad-1
                        fcont[u1++]=(byte)i;               //seq - no.
                        fcont[u1++]=Msgbffs.type;          //compressed type
                        //shifting length
                        fcont[u1++]=(byte)(n1>>>8);        /*compressed*/
                        fcont[u1++]=(byte)(n1);            /*length*/
                        for(u2=0;u2<mg.length-20;u2++,u1++)
                            fcont[u1]=mg[u2];             //compresed fragment
                        Sha1 sh11=new Sha1(fcont);        //inner hash

                        //second hash contains cmac,pad2,hash one
                        byte bl1[]=new byte[76];
                        fin1=new FileInputStream("smac");
                        for(u1=0;u1<16;u1++)
                            bl1[u1]=(byte)fin1.read();    //write mac secret
                        fin1.close();
                        for(u2=0;u2<40;u2++)
                            bl1[u1++]=0x5c;               //pad-2
                        for(u2=0;u2<20;u2++)
                            bl1[u1++]=sh11.msgd[u2];      //innner hash
                        Sha1 sh12=new Sha1(bl1);          //outer hash

                        for(j=0;j<20;j++)
                            mg[n1+j]=sh12.msgd[j];

                        //encryption
                        String str1="";
                        fin1=new FileInputStream("skey");
                        for(i2=0;i2<5;i2++)
                            str1=str1+(byte)fin1.read();
                        fin1.close();
                        Dese d1=new Dese(str1,mg);
                        int cl=d1.cip.length;
                        mg=new byte[cl];
                        for(u2=0;u2<cl;u2++){
                            mg[u2]=d1.cip[u2];
                            xyz1++;
                            int xyz=(int)mg[u2];
                            //System.out.print((char)xyz);
                            fosc.write(xyz);
                        }
                        pw.println((nb-i));//sending block no.
                        System.out.println("xyz1 - 1 = "+xyz1);
                        send_msg();
                    }//end for,i.e repeat for each fragment
                    fosc.close();
                    System.out.println("xyz1 - 2 = "+xyz1);
                    fosc=new FileOutputStream("s_fl");
                    fosc.write(1);
                    fosc.close();
                }
                else{                    
                    mg=new byte[Msgbffs.cnt.length];
                    for(int j=0;j<Msgbffs.cnt.length;j++)
                        mg[j]=Msgbffs.cnt[j];
                    pw.println(0);
                    send_msg();
                }

                //recieving ack
                String st=br.readLine();
                System.out.println("Recieved "+st+" in server");
                Msgbffs.lg=0;
            }//endif
            else{
                sr=false;
                System.out.println("\nrxing in rec servr");
                int i1,fl,count=-1;
                mg=new byte[0];
                FileOutputStream fosc=new FileOutputStream("sr_fl");
                fosc.write(0);
                fosc.close();
                fosc=new FileOutputStream("asflag");
                fosc.write(0);
                fosc.close();
                fosc=new FileOutputStream("ser_cip_r");
                do{                    
                    count++;
                    String st=br.readLine();
                    fl=Integer.parseInt(st); //no. of blocks remaining

                    //recieving header
                    st=br.readLine();
                    Header1.ct=(Byte.valueOf(st)).byteValue();
                    st=br.readLine();                                        
                    Header1.majorv=(Byte.valueOf(st)).byteValue();
                    st=br.readLine();
                    Header1.minorv=(Byte.valueOf(st)).byteValue();
                    st=br.readLine();
                    Header1.msgl=(Short.valueOf(st)).shortValue();
                    if(Header1.ct==1){
                        FileOutputStream fos=new FileOutputStream("srec");
                        st=br.readLine();//to rx handshake msg type
                        fos.write(Integer.parseInt(st));
                        System.out.println("\n type, length rxed in Rec_srvr "+st+" "+Header1.msgl);
                        //msg length, unpacking                    
                        for(int k=1;k<=4;k++)
                            fos.write(Header1.msgl>>>(32-(k*8)));
                        for(i=0;i<Header1.msgl-5;i++){
                            st=br.readLine();
                            fos.write(Integer.parseInt(st));
                        }//endfor
                        fos.close();
                    }
                    else{
                        byte temp1[]=new byte[Header1.msgl-5];
                        for(i1=0;i1<Header1.msgl-5;i1++)
                        {
                            st=br.readLine();                            
                            temp1[i1]=(Byte.valueOf(st)).byteValue();
                            //temp1 has each encrypted fragment
                            fosc.write((int)temp1[i1]);
                        }
                        //decrypting each fragment
                        String str="";
                        FileInputStream fin1=new FileInputStream("ckey");
                        for(i2=0;i2<5;i2++)
                            str=str+(byte)fin1.read();
                        fin1.close();
                        Desd d1=new Desd(str,temp1);
                        temp1=new byte[d1.plt.length-20];
                        for(i2=0;i2<d1.plt.length-20;i2++)
                            temp1[i2]=d1.plt[i2];
                        //temp1 has each decrypted fragment without hash

                        byte hashc[]=new byte[20];
                        for(i1=0;i1<20;i1++)
                            hashc[i1]=d1.plt[i2+i1];
                        //hashc has mac computed client

                        int lenh=60+temp1.length;
                        byte fcont[]=new byte[lenh];
                        fin1=new FileInputStream("cmac");
                        for(u1=0;u1<16;u1++)
                            fcont[u1]=(byte)fin1.read();   //write mac secret
                        fin1.close();
                        for(u2=0;u2<40;u2++)
                            fcont[u1++]=0x36;              //pad-1
                        fcont[u1++]=(byte)count;           //seq - no.
                        fcont[u1++]=Header1.ct;            //compressed type
                        //shifting length
                        fcont[u1++]=(byte)(temp1.length>>>8); /*compressed*/
                        fcont[u1++]=(byte)(temp1.length);     /*length*/                        
                        for(u2=0;u2<temp1.length;u2++)
                            fcont[u1++]=temp1[u2];        //compresed fragment
                        Sha1 sh11=new Sha1(fcont);        //inner hash

                        //second hash contains cmac,pad2,hash one
                        byte bl1[]=new byte[76];
                        fin1=new FileInputStream("cmac");
                        for(u1=0;u1<16;u1++)
                            bl1[u1]=(byte)fin1.read();    //write mac secret
                        fin1.close();
                        for(u2=0;u2<40;u2++)
                            bl1[u1++]=0x5c;               //pad-2
                        for(u2=0;u2<20;u2++)
                            bl1[u1++]=sh11.msgd[u2];      //innner hash
                        Sha1 sh12=new Sha1(bl1);          //outer hash

                        //compare both the hashes
                        int flag=0;
                        for(i1=0;i1<20;i1++)
                        {
                            if(hashc[i1]!=sh12.msgd[i1])
                            {
                                System.out.println("call alert b/c msg. digest mismatch");                                
                                flag=1;
                                break;
                            }
                        }
                        byte temp[]=new byte[mg.length];
                        for(i2=0;i2<mg.length;i2++)
                            temp[i2]=mg[i2];
                        mg=new byte[temp.length+temp1.length];
                        for(i2=0;i2<temp.length;i2++)
                            mg[i2]=temp[i2];
                        for(i1=0;i1<temp1.length;i1++)
                            mg[i2+i1]=temp1[i1];
                    }
                }while(fl!=0);
                //sending ack
                pw.println("OK");
                fosc.close();
            } //end else
            s.close();
            rec.close();
            if(Header1.ct==4&&sr==false){
                int i1;
                FileOutputStream fos=new FileOutputStream("sapp");
                for(i1=0;i1<mg.length;i1++)
                    fos.write((int)mg[i1]);
                fos.close();
                fos=new FileOutputStream("asflag");
                fos.write(1);
                fos.close();                
                fos=new FileOutputStream("sr_fl");
                fos.write(1);
                fos.close();
            }
            if(Header1.ct==1&&sr==false){
                FileOutputStream fos=new FileOutputStream("sflag");
                fos.write(1);
                fos.close();
            }
        }//try blk
        catch(Exception e){
            System.out.println("Exception in Rec_srvr  "+e);
        } 
    }
    void send_msg(){
        //constructing header
        Header1.ct=Msgbffs.type;
        Header1.msgl=(short)(mg.length+5);
        //sending
        pw.println(Header1.ct);                
        pw.println(Header1.majorv);
        pw.println(Header1.minorv);
        pw.println(Header1.msgl);
        if(Header1.ct==1)
            pw.println(Msgbffs.h_type);    
        for(int i=0;i<mg.length;i++){
            pw.println(mg[i]);
        }
    }
}
