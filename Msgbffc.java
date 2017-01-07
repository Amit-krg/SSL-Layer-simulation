simport java.io.*;
class Msgbffc{
    static boolean hs;
    static byte type;
    static byte h_type;
    static int lg;
    static byte cnt[];
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
            CA1 tc=new CA1();
            CB1 ts=new CB1();
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
            System.out.println("Exception in Msgbffc update"+e);
        }
    }
}

class CA1 extends Thread{
    public void run(){
        Rec_clnt rc=new Rec_clnt();
        //System.out.println("in CA1");
    }
}
class CB1 extends Thread{
    public void run(){
        Rec_srvr rs=new Rec_srvr();
        //System.out.println("in CB1");
    }
}
 
