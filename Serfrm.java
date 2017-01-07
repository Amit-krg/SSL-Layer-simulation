import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
public class Serfrm extends Frame implements ItemListener,ActionListener{
    boolean hsk;
    int ver,keyex,calg,mac,str;
    Label l_ver,l_keyex,l_cip,l_calg,l_mac,l_app;
    static Label l_status[];
    Choice c_ver,c_keyex,c_calg,c_mac;
    Button hb,ab,vb,vcrb,vcsb;
    CheckboxGroup ch;
    Checkbox m,f;
    TextField txt;
    static String msg="";
    Serfrm(String title){        
        super(title);
        hsk=false;
        Wa ad=new Wa(this);
        addWindowListener(ad);
        l_ver=new Label("Version");
        l_keyex=new Label("Key Exchange");
        l_cip=new Label("Cipher Specification");
        l_calg=new Label("Cipher Algorithm");
        l_mac=new Label("Mac");
        l_app=new Label("Application data");
        l_status=new Label[14];
        c_ver=new Choice();
        c_keyex=new Choice();
        c_calg=new Choice();
        c_mac=new Choice();
        hb=new Button("Start Server");
        ab=new Button("Send data");
        vb=new Button("View Recieved data");
        vcrb=new Button("View Recieved Cipher");
        vcsb=new Button("View Sent Cipher");
        ch=new CheckboxGroup();
        m=new Checkbox("Messages",ch,true);
        f=new Checkbox("Files",ch,false);
        txt=new TextField();        
        c_ver.add("3.0");
        c_keyex.add("RSA");
        c_calg.add("DES");
        c_mac.add("MD5");
        c_mac.add("SHA-1");
        setLayout(null);
        add(l_ver);add(c_ver);
        l_ver.setBounds(50,38,50,20);
        c_ver.setBounds(190,38,60,20);
        add(l_keyex);add(c_keyex);
        l_keyex.setBounds(52,71,82,20);
        c_keyex.setBounds(190,71,60,20);
        add(l_cip);
        l_cip.setBounds(100,104,110,20);
        add(l_calg);add(c_calg);
        l_calg.setBounds(40,137,150,20);
        c_calg.setBounds(190,137,60,20);
        add(l_mac);add(c_mac);
        l_mac.setBounds(112,170,40,20);
        c_mac.setBounds(190,170,60,20);
        add(hb);
        hb.setBounds(100,203,110,20);
        l_app.setBounds(90,240,100,20);
        add(l_app);
        m.setBounds(120,263,70,20);
        f.setBounds(120,287,70,20);
        add(m);add(f);
        txt.setBounds(115,318,90,20);
        ab.setBounds(30,346,125,20);
        vb.setBounds(160,346,125,20);
        add(txt);add(ab);add(vb);
        vcrb.setBounds(30,376,125,20);
        vcsb.setBounds(160,376,125,20);
        add(vcrb);add(vcsb);
        int y=410,x=90;
        for(int i=0;i<14;i++){
            l_status[i]=new Label("");
            l_status[i].setBounds(x,y,250,12);
            y+=15;
        }
        for(int i=0;i<10;i++)
            add(l_status[i]);
        hb.addActionListener(this);
        ab.addActionListener(this);
        vb.addActionListener(this);
        vcrb.addActionListener(this);
        vcsb.addActionListener(this);
        c_ver.addItemListener(this);
        c_keyex.addItemListener(this);
        c_calg.addItemListener(this);
        c_mac.addItemListener(this);
        ver=c_ver.getSelectedIndex();
        keyex=c_keyex.getSelectedIndex();
        calg=c_calg.getSelectedIndex();
        mac=c_mac.getSelectedIndex();
    }
    public void actionPerformed(ActionEvent ae){
        for(int j=0;j<10;j++)
            l_status[j].setText(" ");
        msg=new String("");
        String bn=ae.getActionCommand();
        if(bn.equals("Start Server")){
            Hsr srvr=new Hsr();
            hsk=true;
        }
        if(bn.equals("Send data")){            
            if(txt.getText().length()==0){
                l_status[0].setText("Text box cannot be empty");
                return;
            }
            System.out.println(" hsk "+hsk);
            if(hsk==false){
                Ds d2=new Ds(this,"Warning",1);
                d2.setVisible(true);
                return;
            }
            String st1=txt.getText();
            if(f.getState()==true)
                st1=st1+(char)0;           
            Sapsnd ss=new Sapsnd(st1);
            l_status[1].setText("Data sent successfully");
        }
        if(bn.equals("View Recieved data")){
            l_status[1].setText("Please wait .......");
            Ds d1=new Ds(this,"Recieved Data",2);
            System.out.println("Called dialog");
            d1.setVisible(true);
            System.out.println("Made dialog visible");
            l_status[1].setText("");
        }
        if(bn.equals("View Recieved Cipher")){
            l_status[1].setText("Please wait .......");
            Ds d1=new Ds(this,"Recieved Cipher",3);
            d1.setVisible(true);
            l_status[1].setText("");
        }
        if(bn.equals("View Sent Cipher")){
            l_status[1].setText("Please wait .......");
            Ds d1=new Ds(this,"Sent Cipher",4);
            d1.setVisible(true);
            l_status[1].setText("");
        }
    }
    public void itemStateChanged(ItemEvent ie){
        ver=c_ver.getSelectedIndex();
        keyex=c_keyex.getSelectedIndex();
        calg=c_calg.getSelectedIndex();
        mac=c_mac.getSelectedIndex();
    }
    public void paint(Graphics g){
        g.drawString("ENTER SERVER CONFIGURATION",20,20);
        g.drawString(msg,20,450);
    }   
    public static void main(String args[]){
        Serfrm sr=new Serfrm("Server");
        sr.setSize(300,650);
        sr.setVisible(true);
    }
}
class Wa extends WindowAdapter{
    Serfrm srf;
    Wa(Serfrm srf){
        this.srf=srf;
    }
    public void windowClosing(WindowEvent we){
        srf.setVisible(false);
        System.exit(0);
    }
}
class Ds extends Dialog implements ActionListener{
    Ds(Frame par,String ttl,int c){
        super(par,ttl,false);
        try{            
            Button b;
            TextArea t;
            Label l=new Label();
            setLayout(new FlowLayout());
            setSize(400,400);
            b=new Button("Close");
            add(l);
            FileInputStream fs;
            switch(c){
                case 1:
                    l.setText("Handshake not done connection will not be secure");
                    break;
                case 2:
                    fs=new FileInputStream("asflag");
                    int a=fs.read();
                    fs.close();
                    System.out.println("Found "+a+" in asflag");
                    if(a==0){
                        l.setText("Data not yet recieved, please wait ... ");
                        System.out.println("Created wait lable");
                    }
                    else{
                        fs=new FileInputStream("sapp");
                        int r=0;
                        String st=new String("");
                        while(r!=-1){
                            r=fs.read();
                            if(r!=-1)
                                st=st+(char)r;
                        } 
                        t=new TextArea(st,15,50);
                        add(t);
                        fs.close();
                        System.out.println("Created text area");
                    }
                    break;
                case 3:
                    fs=new FileInputStream("sr_fl");
                    a=fs.read();
                    fs.close();
                    if(a==0){
                        l.setText("Cipher not yet recieved, please wait ... ");
                    }
                    else{
                        fs=new FileInputStream("ser_cip_r");
                        int r=0,xyz1=0;
                        String st=new String("");
                        while(r!=-1){                            
                            r=fs.read();
                            if(!(r==-1||r==0)){
                                st=st+(char)r;
                            }
                            xyz1++;
                        }
                        System.out.println("Scr "+st.length()+"xyz1 "+xyz1);
                        t=new TextArea(st,15,50);
                        add(t);
                        fs.close();
                    }                    
                    break;
                case 4:
                    fs=new FileInputStream("s_fl");
                    a=fs.read();
                    fs.close();
                    if(a==0){
                        l.setText("Cipher not yet ready, please wait ... ");
                    }
                    else{
                        fs=new FileInputStream("ser_cip_s");
                        int r=0,xyz1=0;
                        String st=new String("");
                        while(r!=-1){
                            r=fs.read();                            
                            if(!(r==-1||r==0)){
                                st=st+(char)r;
                            }
                            xyz1++;
                        }
                        System.out.println("Scs "+st.length()+"xyz1 "+xyz1);
                        t=new TextArea(st,15,50);
                        add(t);
                        fs.close();
                    }                    
                    break;
            }
            add(b);
            b.addActionListener(this);
        }
        catch(Exception e){
            System.out.println("Exception in Ds "+e);
        }
    }
    public void actionPerformed(ActionEvent ae){
        dispose();
    }
} 
