class Words{
    static long msk=0xffffffffl;
    static long tp32=0x100000000l;
    int w;
    Words(){
        w=0;
    }
    void set(int a){
        w=a;
    }
    void set(long a){
        w=(int) a;
    }
    void add(Words w1){
        long l1,l2;
        l1=(long) w;
        l2=(long) w1.w;
        l1=l1&msk;
        l2=l2&msk;
        l1=(l1+l2)%tp32;
        w=(int) l1;
    }
    void rol(int val){
        int temp1,temp2;
        temp1=w>>>(32-val);
        temp2=w<<val;
        temp1=temp1|temp2;
        w=temp1;
    }
    long rtrn(){
        long temp;
        temp=(long) w;
        temp=temp&msk;
        return temp;
    }
}
public class Sha1{
    String msg;
    String out1="";
    String out2="";
    byte b1[];
    int m,f;
    byte msgd[]=new byte[20];
    Sha1(byte b[]){
        b1=new byte[b.length];
        m=b.length*8+64;
        for(int i=0;i<b.length;i++){
            b1[i]=b[i];
        }
        f=1;
        calc_hash();
    }
    Sha1(String st){        
        if(st.length()==0)
            msg=new String("");
        else
            msg=new String(st);
        m=msg.length()*8+64;
        f=0;
        calc_hash();
    }
    void calc_hash(){
        int i,np,n,j,k;        
        n=m/512;
        np=(n+1)*512-m;
        long l=(long) (m-64);
        byte msg1[]=new byte[(n+1)*64];
        if(f==0){
            for(i=0;i<msg.length();i++)
                msg1[i]=(byte) msg.charAt(i);
        }
        else{
            for(i=0;i<b1.length;i++)
                msg1[i]=b1[i];
        }

        //appending pad bits

        msg1[i]=(byte) 0x80;
        np-=8;
        while(np>=8){
            msg1[++i]=0;
            np-=8;
        }

        //appending length most significant byte first
        
        for(j=7;j>=0;j--)            
            msg1[++i]=(byte) (l>>>(8*j));       

        //intialising md buffer

        Words shbfr[]=new Words[5];
        shbfr[0]=new Words();shbfr[0].set(0x67452301);
        shbfr[1]=new Words();shbfr[1].set(0xefcdab89);
        shbfr[2]=new Words();shbfr[2].set(0x98badcfe);
        shbfr[3]=new Words();shbfr[3].set(0x10325476);
        shbfr[4]=new Words();shbfr[4].set(0xc3d2e1f0);

        //compression
        n=msg1.length;n=n/64;
        int temp=0,p=0,s=0,temp1;        
        Words f=new Words(),x[]=new Words[4],K=new Words(),t1=new Words();       
        Words t2=new Words(),W[]=new Words[80];
        long l1,lx,la,templ;        
        Words intlv[]=new Words[5],tempv[]=new Words[5];
        for(j=0;j<4;j++)
            x[j]=new Words();
        for(i=0;i<n;i++){
            for(j=0;j<5;j++){
                intlv[j]=new Words();
                tempv[j]=new Words();
                tempv[j].set(shbfr[j].w);                
                intlv[j].set(shbfr[j].w);                
            }
            for(j=1;j<=4;j++){
                for(k=(j-1)*20;k<j*20;k++){
                    switch(j){
                        case 1: temp=((shbfr[1].w&shbfr[2].w)|((~shbfr[1].w)&shbfr[3].w));
                                K.set(0x5a827999);
                                break;
                        case 2: temp=(shbfr[1].w^shbfr[2].w^shbfr[3].w);
                                K.set(0x6ed9eba1);
                                break;
                        case 3: temp=(shbfr[1].w&shbfr[2].w)|(shbfr[1].w&shbfr[3].w)|(shbfr[2].w&shbfr[3].w);
                                 K.set(0x8f1bbcdc);
                                break;
                        case 4: temp=(shbfr[1].w^shbfr[2].w^shbfr[3].w);
                                K.set(0xca62c1d6);
                    }
                    f.set(temp); //function value                                        
                    temp1=0;
                    int tmsk=0x000000ff;                    
                    if(k>15){
                        W[k]=new Words();
                        W[k].w=W[k-16].w ^ W[k-14].w ^ W[k-8].w ^ W[k-3].w;
                        W[k].rol(1);
                    }
                    else{
                        for(m=0;m<4;m++){
                            temp=(int) msg1[(k*4)+m+(i*64)];                                                      
                            temp=temp&tmsk;
                            temp1=temp1<<8;
                            temp1=temp1|temp;
                        }
                        W[k]=new Words();
                        W[k].set(temp1);
                    }
                    t1.set(0);
                    t2.set(tempv[0].w);t2.rol(5);
                    t1.add(tempv[4]);t1.add(f);t1.add(t2);t1.add(W[k]);t1.add(K);
                    shbfr[0].set(t1.w);
                    shbfr[1].set(tempv[0].w);
                    t2.set(tempv[1].w);t2.rol(30);
                    shbfr[2].set(t2.w);
                    shbfr[3].set(tempv[2].w);
                    shbfr[4].set(tempv[3].w);
                    for(int i2=0;i2<5;i2++)
                        tempv[i2].set(shbfr[i2].w);
                }//end of k loop
            }//end of j loop
            for(j=0;j<5;j++)
                shbfr[j].add(intlv[j]);
        }//end of i loop
        for(i=0;i<5;i++)
              out1+=shbfr[i].w;
        for(i=0;i<5;i++)
              out2+=shbfr[i].w;

        //converting msg digest to byte,unpacking
        k=0;
        for(i=0;i<5;i++)
            for(j=1;j<=4;j++){                
                msgd[k++]=(byte) (shbfr[i].w>>(32-(j*8)));
                //System.out.print(" "+msgd[k-1]);
            }
        /*System.out.print("\nMsg. dgst = ");
        for(i=0;i<5;i++)
            System.out.print(Long.toHexString(shbfr[i].rtrn()));*/
    }
}
