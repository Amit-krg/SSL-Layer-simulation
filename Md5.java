class Word{
    static long msk=0xffffffffl;
    static long tp32=0x100000000l;
    int w;
    Word(){
        w=0;
    }
    Word(int a)
	{
        int i,temp,temp1=0;
        int tmsk=0x000000ff;
        for(i=0;i<4;i++){
            temp=(byte) a;
            temp=temp&tmsk;
            temp1=temp1<<8;
            temp1=temp1|temp;
            a=a>>8;
        }
        w=temp1;
    }
    Word(long l){
        int a=(int) l;
        Word w1=new Word(a);
        w=w1.w;
    }
    void set(int a){
        w=a;
    }
    void set(long a){
        w=(int) a;
    }
    void add(Word w1){
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
public class Md5{
    String msg;
    byte msgd[]=new byte[16];
    String out="";
    String tempout="";	
    byte b1[];
    int m,f;
    Md5( byte b[]){
        b1=new byte[b.length];
        m=b.length*8+64;
        for(int i=0;i<b.length;i++){
            b1[i]=b[i];
        }
        f=1;
        calc_hash();
    }
    Md5(String st){        
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
        //System.out.println("Number of pad bits = "+np);
        long l=(long) (m-64);
        //System.out.println("length of original message = "+l);
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

        //appending length least significant byte first
        
        for(j=0;j<=7;j++)            
            msg1[++i]=(byte) (l>>>(8*j));       
        /*for(j=0;j<=i;j++)
            System.out.print(msg1[j]);
        System.out.println("\nlength after padding length = "+msg1.length*8);
        System.out.println("\nlength after padding length(in bytes) = "+msg1.length);*/

        //intialising md buffer


        Word mdbfr[]=new Word[4];
        /*mdbfr[0]=new Word(0x67452301);*/ mdbfr[0]=new Word(0x01234567);
        /*mdbfr[1]=new Word(0xefcdab89);*/ mdbfr[1]=new Word(0x89abcdef);
        /*mdbfr[2]=new Word(0x98badcfe);*/ mdbfr[2]=new Word(0xfedcba98);
        /*mdbfr[3]=new Word(0x10325476);*/ mdbfr[3]=new Word(0x76543210);
        //compression

        n=msg1.length;n=n/64;
        int temp=0,p=0,s=0;        
        Word f=new Word(),x=new Word(),t=new Word(),t1=new Word();
        long l1,lx,la,templ;        
        Word intlv[]=new Word[4];
        for(i=0;i<n;i++){
            for(j=0;j<4;j++){
                intlv[j]=new Word();
                intlv[j].set(mdbfr[j].w);
            }
            for(j=1;j<=4;j++){
                for(k=(j-1)*16;k<j*16;k++){
                    switch(j){
                        case 1: temp=((mdbfr[1].w&mdbfr[2].w)|((~mdbfr[1].w)&mdbfr[3].w));
                                p=k;
                                s=7+((k%4)*5);
                                break;
                        case 2: temp=((mdbfr[1].w&mdbfr[3].w)|(mdbfr[2].w&~mdbfr[3].w));
                                p=(1+(5*(k)))%16;
                                s=5+(3*(k%4))+((k%4)*(k%4+1)/2);
                                break;
                        case 3: temp=(mdbfr[1].w^mdbfr[2].w^mdbfr[3].w);
                                p=(5+(3*(k)))%16;
                                switch(k%4){
                                    case 0: s=4;
                                            break;
                                    case 1: s=11;
                                            break;
                                    case 2: s=16;
                                            break;
                                    case 3: s=23;
                                            break;
                                }
                                break;
                        case 4: temp=(mdbfr[2].w^(mdbfr[1].w|~mdbfr[3].w));
                                p=(7*k)%16;
                                s=6+(3*(k%4))+((k%4)*(k%4+1)/2);
                    }
                    f.set(temp); //function value
                    double d1=Math.abs(Math.sin(k+1));
                    templ=(long) (d1*Word.tp32);
                    t.set(templ);// value of t[i]
                    p*=4;
                    templ=0;
                    int tmsk=0x000000ff;
                    for(m=0;m<4;m++){
                        temp=(int) msg1[p+m+(i*64)];
                        temp=temp&tmsk;
                        templ=templ<<8;
                        templ=templ|(long)temp;
                    }
                    x=new Word(templ);
                    t1.set(0);
                    t1.add(mdbfr[0]);t1.add(f);t1.add(x);t1.add(t);
                    t1.rol(s);
                    t1.add(mdbfr[1]);
                    mdbfr[0].w=t1.w;
                    temp=mdbfr[3].w;
                    for(int a=3;a>=1;a--)
                        mdbfr[a].set(mdbfr[a-1].w);
                    mdbfr[0].w=temp;                    
                }//end of k loop
            }//end of j loop
            for(j=0;j<4;j++)
                mdbfr[j].add(intlv[j]);
        }//end of i loop
        for(i=0;i<4;i++)
            mdbfr[i]=new Word(mdbfr[i].w);
        for(i=0;i<4;i++)
            out+=mdbfr[i].w; 
        for(i=0;i<4;i++)
            tempout+=mdbfr[i].w; 

        //converting msg digest to byte,unpacking
        k=0;
        for(i=0;i<4;i++)
            for(j=1;j<=4;j++)
                msgd[k++]=(byte) (mdbfr[i].w>>(32-(j*8)));

        /*System.out.print("Msg. dgst = ");
        for(i=0;i<4;i++)
            System.out.print(Long.toHexString(mdbfr[i].rtrn())); */
    }
}
