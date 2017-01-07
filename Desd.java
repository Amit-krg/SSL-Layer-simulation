class Desd
{
    String pltx="";
    byte plt[];
    Desd(String key,byte[] ci){
        String str1="";
        int i;
        for(i=0;i<ci.length;i++){
            str1=new String(str1+(char)ci[i]);
        }
        Desd dd=new Desd(key,str1);
        plt=new byte[dd.pltx.length()];
        for(i=0;i<dd.pltx.length();i++)
            plt[i]=(byte)(dd.pltx.charAt(i));
    }        
    Desd(String key,String ciph)
    {
        String st="";
        char c;
        int k[][]=new int[17][65];
        int pc1[]={0,57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,60,52,44,36,63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,28,20,12,4};
        int pc2[]={0,14,17,11,24,1,5,3,28,15,6,21,10,23,19,12,4,26,8,16,7,27,20,13,2,41,52,31,37,47,55,30,40,51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32};
        int p1[]=new int[65];
        int cd[][]=new int[17][57];                
        int l[][] = new int[17][33];
        int r[][] = new int[17][33];
        int ip[]={0,58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,62,54,46,38,30,22,14,6,64,56,48,40,32,24,16,8,57,49,41,33,25,17,9,1,59,51,43,35,27,19,11,3,61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7};
        int e[]={0,32,1,2,3,4,5,4,5,6,7,8,9,8,9,10,11,12,13,12,13,14,15,16,17,16,17,18,19,20,21,20,21,22,23,24,25,24,25,26,27,28,29,28,29,30,31,32,1};
        int ip1[]={0,40,8,48,16,56,24,64,32,39,7,47,15,55,23,63,31,38,6,46,14,54,22,62,30,37,5,45,13,53,21,61,29,36,4,44,12,52,20,60,28,35,3,43,11,51,19,59,27,34,2,42,10,50,18,58,26,33,1,41,9,49,17,57,25};
        int e1[]=new int[49];
        int f[]=new int[49];
        int b[][]=new int[9][7];
        int sb[][]=new int[9][5];
        int sbb[]=new int[33];
        int rl[]=new int[65];   
        int s1[]={14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7,0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8,4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0,15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13};
        int s2[]={15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10,3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5,0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15,13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9};
        int s3[]={10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8,13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1,13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7,1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12};
        int s4[]={7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15,13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9,10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4,3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14};
        int s5[]={2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9,14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6,4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14,11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3};
        int s6[]={12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11,10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8,9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6,4,3,2,12,9,5,15,10,11,14,1,7,6,0,8 ,13};
        int s7[]={4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1,13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6,1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2,6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12};
        int s8[]={13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7,1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2,7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8,2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11};
        int pp[]={0,16,7,20,21,29,12,28,17,1,15,23,26,5,18,31,10,2,8,24,14,32,27,3,9,19,13,30,6,22,11,4,25};     
        int n,n1,nn,np,i,j,d,t,t1=0,t2,t3,k1,x,y=1,i1=0,q,q1=0,km,nz=0;
        int kl[]=new int[65];

        //KEY STORING
        n=key.length();
        if(n<8)
            while(key.length()!=8)
                key=new String(key+(char)0);
        else{
            String stemp="";
            for(j=0;j<8;j++)
                stemp=new String(stemp+key.charAt(j));
            key=new String(stemp);                    
        }
        int inc=0,temp;
        for(i=0;i<8;i++){
            temp=(int)key.charAt(i);
            for(j=0;j<8;j++){
                temp=temp<<j;
                t=temp&0x80;
                if(t==0)
                    kl[++inc]=0;
                else
                    kl[++inc]=1;
            }
        }
        for(i=1;i<=64;i++)
            k[0][i]=kl[i];
        //applying permutation-1 to key  
        for(i=1;i<=56;i++) 
            p1[i]=k[0][pc1[i]];
        //splitting the permuted key into l and r
        j=1;            
        for(i=1;i<=56;i++)
        {
            if(i<=28) 
                l[0][i]=p1[i];
            else
                r[0][j++]=p1[i];  
        }
        //circular leftshift to obtain l16 and r16
        for(i=1;i<=16;i++)
        {
            t=l[i-1][1];
            t1=r[i-1][1];
            t2=l[i-1][2];
            t3=r[i-1][2];
            if(i==1 || i==2 || i==9 || i==16)
            {
                for(j=1;j<28;j++)
                {
                    l[i][j]=l[i-1][j+1];
                    r[i][j]=r[i-1][j+1];
                }
                l[i][28]=t;
                r[i][28]=t1;   
            }                      
            else 
            {
                for(j=1;j<27;j++)
                {
                    l[i][j]=l[i-1][j+2];
                    r[i][j]=r[i-1][j+2];
                }
                l[i][27]=t;
                r[i][27]=t1;
                l[i][28]=t2;
                r[i][28]=t3;
            } 
        }
        //16 cndn for calculating 16 subkeys
        k1=1;
        for(i=1;i<=16;i++) 
        {
            for(j=1;j<=56;j++)
            {
                if(j<=28)  {
                    cd[i][j]=l[i][j];  k1=1;
                }
                else
                    cd[i][j]=r[i][k1++];
            }
        }
        // applying permutation-2 to obtain 16 subkeys
        for(i=1;i<=16;i++)
        {       
            for(j=1;j<=48;j++) 
                k[i][j]=cd[i][pc2[j]];
        }
        //MESSAGE STORING
        n=ciph.length();
        inc=0;
        int cp[]=new int[n*8+1];
        //System.out.println("\ncipher "+ciph);
        for(i=0;i<n;i++)
        {                                            
            k1=(int)ciph.charAt(i);
            for(j=0;j<8;j++)
            {
                t=(k1<<j) & 0x80;
                if(t == 0)
                    cp[++inc]=0;
                else
                    cp[++inc]=1;
                //System.out.print(" "+cp[inc]);
             }
        }
        if(inc%64==0)
            np=0;
        else
            np=64-(inc%64);
        for(i=1;i<=np;i++){
            System.out.println(" Trying to pad");
            cp[++inc]=0;
        }
        int mm[]=new int[65];
        //implementing for message
        n1=inc/64;y=1;
        int pt[][]=new int[n1][65];
        for (x=1;x<=n1;x++)
        {
            i=1;
            while(i<=64)
            {
                mm[i]=cp[y]; i++;y++;
            }
            //applying initial permutation for msg
            for(i=1;i<=64;i++) 
                p1[i]=mm[ip[i]];            
            //splitting into l0 r0
            j=1;
            for(i=1;i<=64;i++){
                if(i<=32)
                    l[0][i]=p1[i];
                else
                    r[0][j++]=p1[i];  
            }
            //calculating l16 and r16 using E-bit selection table
            for(i1=1,nn=16;i1<=16;i1++,nn--)
            {
                for(j=1;j<=32;j++)
                    l[i1][j]=r[i1-1][j];     
                for(k1=1;k1<=48;k1++)
                    e1[k1]=r[i1-1][e[k1]];
                for(k1=1;k1<=48;k1++)
                    f[k1]=k[nn][k1]^e1[k1];
                i=1;k1=1;
                for(j=1;j<=48;j++,k1++)
                {              
                    if(k1%6!=0) 
                        b[i][k1]=f[j]; 
                    else
                    {
                        b[i][k1]=f[j];
                        k1=0;
                        i++;
                     }
                }
                i=1;
                while(i<=8)
                {
                    t=0; q1=0;
                    k1=b[i][1]*2+b[i][6]; 
                    for(j=5;j>=2;j--)
                    {
                        q= b[i][j]*(int) Math.pow(2,t);
                        q1=q1+q;  t++;
                    }                    
                    t2=16*k1+q1;
                    // and using s-boxes which are from s1 to s8
                    switch(i) {
                        case 1:
                            t1=s1[t2];
                            break;
                        case 2:
                            t1=s2[t2];
                            break;
                        case 3:
                            t1=s3[t2];
                            break;
                        case 4:
                            t1=s4[t2];
                            break;
                        case 5:
                            t1=s5[t2];
                            break;
                        case 6:
                            t1=s6[t2];
                            break;
                        case 7:
                            t1=s7[t2];
                            break;
                        case 8:
                            t1=s8[t2];
                            break;
                    }
                    for(j=0;j<4;j++){
                        t1=t1<<j;
                        temp=t1&0x8;
                        if(temp==0)
                            sb[i][j]=0;
                        else
                            sb[i][j]=1;
                     }
                     i++;
                }//end while
                j=1;k1=1;
                while(j<=8)
                {
                    for(i=1;i<=4;i++,k1++)
                    sbb[k1]=sb[j++][i];
                }
                for(i=1;i<=32;i++)
                    f[i]=sbb[pp[i]];
                for(i=1;i<=32;i++)
                    r[i1][i]=l[i1-1][i]^f[i];                        
            }
        
            //calucalting rl
            j=1;
            for(i=1;i<=64;i++)
            {
                if(i<=32)
                    rl[i]=r[16][i];
                else{
                    rl[i]=l[16][j];
                    j++;
                }
            } 
            //ciphertext  is obtained using IP1 table
            for(i=1;i<=64;i++)                        
                pt[x-1][i]=rl[ip1[i]];
             //printing ciphertext
             int temp1;
             byte s;
             inc=0;
             for(i=0;i<8;i++){
                temp=1;s=0;                
                for(j=8;j>0;j--){                            
                    temp1=(temp*pt[x-1][inc+j]);
                    s+=(byte)temp1;
                    temp*=2;
                }
                inc+=8;
                if(s!=0)                   
                    pltx=new String(pltx+(char)s);
            }            
         }//for end
         //System.out.println("\nPlain text "+pltx);
         plt=new byte[pltx.length()];
         for(i=0;i<pltx.length();i++)
            plt[i]=(byte)(pltx.charAt(i));
    }
}
