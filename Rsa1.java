public class Rsa1{
    int nr,er,dr;
    Rsa1(){
        int p=0,q=0,range=1000,i,e=0,k=1,d=0,y,n1,temp,n;
        double cipher=1,rslt=1;
        while(k!=0){
            p=(int) (Math.random()*range);
            k=0;
            if(p<10)
                k++;
            for(i=2;i<=p/2;i++)
                if(p%i==0)
                    k++;
        }
        k=1;
        while(k!=0){
            q=(int) (Math.random()*range);
            k=0;
            if(p==q|q<10)
                k++;
            for(i=2;i<=q/2;i++)
                if(q%i==0)
                    k++;
        }
        k=1;
        n=p*q;
        n1=(p-1)*(q-1);
        while (k!=0){
            e=(int) (Math.random()*100);
            k=0;
            for(i=2;i<=e/2;i++)
                if(n1%i==0&&e%i==0)
                    k++;
            if(e>=p*q||e==0)
            {e++;
                k++;
                }

            if(n1%e==0)
                k++;
        }
        n1=(p-1)*(q-1);
        i=1;
        while(true){
            if(((n1*i)+1)%e==0){
                y=((n1*i)+1)/e;
                if(y!=e&&y!=0)
                    break;
            }
            i++;
        }
        d=y%n1;
        nr=n;er=e;dr=d;
    }
}
