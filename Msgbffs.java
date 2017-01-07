import java.io.*;
class Msgbffs{
    static boolean hs=false;
    static byte type;
    static byte h_type;
    static byte flag;
    static int lg;
    static byte cnt[];
    static byte f1,f2;
    static void update(boolean val,int t,int h,int l, byte[] c){
        try{
            int w=0;
            while(w!=1){
                FileInputStream fls=new FileInputStream("wait");
                w=fls.read();
                fls.close();
            }        
            hs=val;
            type=(byte)t;
            h_type=(byte)h;
            lg=l;
            cnt=new byte[c.length];
            for(int i=0;i<c.length;i++)
                cnt[i]=c[i];
            FileOutputStream fos=new FileOutputStream("wait");
            fos.write(0);
            fos.close();
            SA1 tc=new SA1();
            SB1 ts=new SB1();
            ts.start();
            /*Thread td = new Thread();
            td.start();
            td.sleep(14000);
            td.join();*/
            tc.start();
            ts.join();
            tc.join();
        }
        catch(Exception e){
            System.out.println("Exception in Msgbffs update "+e);
        }
    }
}

class SA1 extends Thread{
    public void run(){
        Rec_clnt r1=new Rec_clnt();
        //System.out.println("in SA1");
    }
}
class SB1 extends Thread{
    public void run(){
        Rec_srvr r2=new Rec_srvr();
        //System.out.println("in SB1");
    }
}
