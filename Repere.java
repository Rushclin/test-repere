import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.lang.SecurityException;
import java.lang.Exception;

public class Repere {

    private double xMax=10;
    private double yMax=10;
    private int largeurPix=600;
    private int hauteurPix=600;
    private BufferedImage buff;
    private Graphics2D gBuff;
    private boolean repere=true;    
    private boolean debug=false;
    private MonJPanel panel;

    public Repere(){
        JFrame frame = new JFrame("Repere orthonorme");
        frame.setSize(largeurPix,hauteurPix+20);
        frame.setResizable(false);
        
        panel = new MonJPanel();
        panel.setSize(largeurPix, hauteurPix);
        frame.add(panel);
        
        buff = new BufferedImage(largeurPix,hauteurPix,BufferedImage.TYPE_INT_ARGB);
        gBuff = buff.createGraphics();
        effacer();
        frame.setVisible(true);
    }

    
    private int repereVersEcranX(double x){
        return (int)((x/xMax)*largeurPix/2)+largeurPix/2;
    }
    

    private int repereVersEcranY(double y){
        return (int)((-y/yMax)*hauteurPix/2)+hauteurPix/2;
    }
    

    private void dessineRepere(Graphics g){
        int nbStep = 5;
        g.setColor(Color.GRAY);
        g.drawLine(0, hauteurPix/2,largeurPix,hauteurPix/2);
        g.drawLine(largeurPix/2,0,largeurPix/2,hauteurPix);
        for(int i = 0; i < nbStep; i++){
            int xx=repereVersEcranX(i*xMax/nbStep);
            g.drawLine(xx, hauteurPix/2,xx,hauteurPix/2+5);
            int xx2=repereVersEcranX(-i*xMax/nbStep);
            g.drawLine(xx2, hauteurPix/2,xx2,hauteurPix/2+5);
            
            int yy=repereVersEcranX(i*yMax/nbStep);
            g.drawLine(largeurPix/2,yy, largeurPix/2+5,yy);
            int yy2=repereVersEcranX(-i*yMax/nbStep);
            g.drawLine(largeurPix/2, yy2,largeurPix/2+5,yy2);
        }
        
        g.drawString("(0,0)",largeurPix/2+3,hauteurPix/2+15);
        g.drawString(""+(nbStep-1)*xMax/nbStep,repereVersEcranX((nbStep-1.0)*xMax/nbStep)-8,hauteurPix/2+18);
        g.drawString(""+ (-1*(nbStep-1)*xMax/nbStep),repereVersEcranX(-(nbStep-1.0)*xMax/nbStep)-8,hauteurPix/2+18);
        g.drawString(""+(nbStep-1)*yMax/nbStep,largeurPix/2+8,repereVersEcranY((nbStep-1.0)*yMax/nbStep)+5);
        g.drawString(""+(-(nbStep-1)*yMax/nbStep),largeurPix/2+8,repereVersEcranY(-(nbStep-1.0)*yMax/nbStep)+5);
    }
    

    public void effacer()
    {     
        for(int x = 0; x < buff.getWidth(); x++)
        {
            for(int y = 0; y < buff.getHeight(); y++)
            {
                buff.setRGB(x, y, (0xFF));
            }
        }
        panel.repaint();
    }
    
    
    private Double getValueFromObject(Object o, String method){
        Class<?> c=o.getClass();
        Method m;
        try{
            m=c.getMethod(method,new Class<?>[]{});
        } catch (NoSuchMethodException e) {
            System.err.println("Repere: dessinerVecteurEn2d: Impossible de trouver la mÃ©thode "+method+" dans la classe " +c );
            return null;
        } catch (SecurityException e){
            System.err.println("Repere: dessinerVecteurEn2d: La mÃ©thode " +method +" doit Ãªtre publique dans la classe " +c);
            return null;
        }
        Object or;
        try{
            or=m.invoke(o,new Object []{});
        }catch(Exception e) {
            System.err.println("Repere: dessinerVecteurEn2d: Erreur lors de l'appel Ã  " +method+ " : " + e);
            return null;
        }
        
        if(or==null){
            System.err.println("Repere: dessinerVecteurEn2d: Erreur la mÃ©thode " +method+ " n'a rien retournÃ©");
            return null;
        }
        
        if(!(or instanceof Double)){
            System.err.println("Repere: dessinerVecteurEn2d: Erreur la mÃ©thode " +method+ " doit retourner un double");
            return null;
        }
        
        return (Double)or;
    }

    private void dessinerFlecheEn2d(double px, double py, Color couleur){
        dessinerFlecheEn2d(0,0,px,py,couleur);
    }
    

    private void dessinerFlecheEn2d(double px1, double py1, double px2, double py2, Color couleur){
        dessinerSegmentEn2d(px1,py1,px2,py2,couleur);
        int x=repereVersEcranX(px2);
        int y=repereVersEcranY(py2);
        gBuff.translate(x, y);
        double angle = Math.atan2(py2-py1,px2-px1);
        gBuff.rotate(-angle);
        gBuff.drawLine(0, 0, -10, 5);
        gBuff.drawLine(0, 0, -10, -5);
        gBuff.rotate( angle);
        gBuff.translate( -x, -y);
        panel.repaint();
    }
   

    public void dessinerSegmentEn2d(Object o1, Object o2){
        Double ox1=getValueFromObject(o1,"getX");
        Double oy1=getValueFromObject(o1,"getY");
        Double ox2=getValueFromObject(o2,"getX");
        Double oy2=getValueFromObject(o2,"getY");
        if(ox1!=null && oy1!=null && ox2!=null && oy2!=null)
        {
            dessinerSegmentEn2d(ox1,oy1,ox2,oy2);
        }
    }
    
    private void dessinerSegmentEn2d(double px1, double py1, double px2, double py2){
        dessinerSegmentEn2d(px1,py1,px2,py2,Color.GREEN);
    }
    

    private void dessinerSegmentEn2d(double px1, double py1, double px2, double py2, Color couleur){
        int x1=repereVersEcranX(px1);
        int y1=repereVersEcranY(py1);
        int x2=repereVersEcranX(px2);
        int y2=repereVersEcranY(py2);
        gBuff.setColor(couleur);
        gBuff.drawLine(x1,y1,x2,y2);
        panel.repaint();
    }

    public void afficheRepere(boolean f){
        repere=f; 
        panel.repaint();
    }
    
    private class MonJPanel extends JPanel{
         @Override
         public void paint(Graphics g) 
         {
             g.setColor(Color.WHITE);
             g.clearRect(0, 0, largeurPix,hauteurPix);
             if(repere) dessineRepere(g);
             g.drawImage(buff,0,0,null);
            }
    }

    public static void main(String[] args){
        Repere repere = new Repere();
    }
}