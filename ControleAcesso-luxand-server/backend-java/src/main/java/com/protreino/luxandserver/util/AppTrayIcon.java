package com.protreino.luxandserver.util;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;

import com.protreino.luxandserver.BackendJavaApplication;

public class AppTrayIcon extends TrayIcon {

    private static final String IMAGE_PATH = "/static/facial.png";
    private static final String TOOLTIP = "Servidor facial em execução";
    private JPopupMenu jPopup;
    final SystemTray tray;

    public AppTrayIcon(){
        super(createImage(IMAGE_PATH, TOOLTIP), TOOLTIP);
        
        jPopup = new JPopupMenu();
        tray = SystemTray.getSystemTray();
        
        try {
            setup();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void setup() throws AWTException{
    	addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    jPopup.setLocation(e.getX(), e.getY());
                    jPopup.setInvoker(jPopup);
                    jPopup.setVisible(true);
                }
            }
        });
    	
    	setImageAutoSize(true);
    	
        JMenuItem exitItem = new JMenuItem("Sair");
        exitItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		exit();
            }

        });
        
        JMenuItem openItem = new JMenuItem("Abrir");
        openItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openWebPage();
            }
        });
        
        jPopup.add(openItem);
        jPopup.addSeparator();
        jPopup.add(exitItem);
        
        
        tray.add(this);
    }
    
	private void exit() {
		int dialogResult = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja sair?", "Confirmação", 
														JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (dialogResult != JOptionPane.YES_OPTION)
			return;
		
		final int exitCode = 0;
        ExitCodeGenerator exitCodeGenerator = new ExitCodeGenerator() {
        	public int getExitCode() {
        		return exitCode;
        	}
        };
        tray.remove(AppTrayIcon.this);
        SpringApplication.exit(BackendJavaApplication.context, exitCodeGenerator);
	}
    
    public void remove() {
    	tray.remove(AppTrayIcon.this);
    }
    
    public static void openWebPage() {
    	String url = "http://localhost:81/login";

    	try {
    		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        	desktop.browse(new URI(url));
	        } 
	        else {
	        	Runtime rt = Runtime.getRuntime();
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
	        }
    	}
    	catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected static Image createImage(String path, String description) {
    	
    	URL imageURL = AppTrayIcon.class.getResource(path);
        
    	if (imageURL == null) {
            System.err.println("Failed Creating Image. Resource not found: "+path);
            return null;
        }
        else {
        	Image image = new ImageIcon(imageURL, description).getImage();
            return image.getScaledInstance((int) SystemTray.getSystemTray().getTrayIconSize().getWidth(),
	        		-1, Image.SCALE_SMOOTH);
        }
        
    	//Toolkit toolkit = Toolkit.getDefaultToolkit();
    	//return toolkit.getImage(BackendJavaApplication.class.getResource(path));
    }
}