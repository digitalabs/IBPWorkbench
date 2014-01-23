package org.generationcp.ibpworkbench.ui.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

@Configurable
public class HelpWindow extends Window implements InitializingBean, InternationalizableComponent {
    
	private static final Logger LOG = LoggerFactory.getLogger(HelpWindow.class);
    
	private static final long serialVersionUID = 1L;
    

    // Components
	private ComponentContainer rootLayout;
	
	
	private static final String WINDOW_WIDTH = "640px";
	private static final String WINDOW_HEIGHT = "480px";
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	public HelpWindow() {    	
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }
    
    protected void initializeData() {
    	
    	//check if the user already installed the doc installer
    }
    
    protected void initializeComponents() throws Exception {
		
    }

    @SuppressWarnings("serial")
	protected void initializeLayout() {
        this.setWidth(WINDOW_WIDTH);
		this.setHeight(WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);
		this.setCaption("BREEDING MANAGEMENT SYSTEM | WORKBENCH");
		this.setStyleName("gcp-help-window");
		
		rootLayout = this.getContent();
		
		Label version = new Label(WorkbenchMainView.VERSION);
		version.setStyleName("gcp-version");
		rootLayout.addComponent(version);
        
        Panel panel = new Panel();
        panel.setSizeUndefined();
        rootLayout.addComponent(panel);
        
        CustomLayout helpLayout = new CustomLayout("help");
        panel.setContent(helpLayout);
        
        //TODO 1. replace with the correct value from installation directory
        //TODO 2. detect if docs are installed - 
        //if not, show a message prompting them to download and install it first
        final String docsDirectory = "C:/IBWorkflowSystem/Documents/";
        final String pdfFilename = "BMS_User_Manual.pdf";
        final String htmlFilename = "BMS_User_Manual_Web_Version[help files].html";
        final String pdfFilepath = docsDirectory+pdfFilename;
        final String htmlFilepath = docsDirectory+htmlFilename;
        
        Link pdfLink = new Link() {
        	@Override
        	public void attach() {
        		super.attach();
        		
        		StreamSource pdfSource = new StreamResource.StreamSource() {
                	public InputStream getStream() {
            			try {
            					File f = new File(pdfFilepath);
            					FileInputStream fis = new FileInputStream(f);
            					return fis;
            			} catch (Exception e) {
            				e.printStackTrace();
            				LOG.error(e.getMessage());
            				return null;
            			}
            		}
            	};
            	StreamResource pdfResource = new StreamResource(pdfSource, 
            			pdfFilename, getApplication());
    	    	pdfResource.getStream().setParameter(
    	    			"Content-Disposition", "attachment;filename=\"" + pdfFilename + "\"");
    	    	pdfResource.setMIMEType("application/pdf");
    	    	pdfResource.setCacheTime(0);
        		setResource(pdfResource);
        	}
        };
        pdfLink.setCaption("BMS Manual PDF Version");
        pdfLink.setTargetName("_blank");
        pdfLink.setIcon(new ThemeResource("../gcp-default/images/pdf_icon.png"));
        pdfLink.addStyleName("gcp-pdf-link");
        helpLayout.addComponent(pdfLink, "pdf_link");
        
        Link htmlLink = new Link() {
        	@Override
        	public void attach() {
        		super.attach();
        		
        		StreamSource htmlSource = new StreamResource.StreamSource() {
                	public InputStream getStream() {
            			try {
        					File f = new File(htmlFilepath);
        					FileInputStream fis = new FileInputStream(f);
        					return fis;
            			} catch (Exception e) {
            				e.printStackTrace();
            				LOG.error(e.getMessage());
            				return null;
            			}
            		}
            	};
        		
    	    	
    	    	StreamResource htmlResource = new StreamResource(htmlSource, 
    	    			htmlFilename, getApplication());
    	    	htmlResource.getStream().setParameter(
    	    			"Content-Disposition", "attachment;filename=\"" + htmlFilename + "\"");
    	    	htmlResource.setMIMEType("text/html");
    	    	htmlResource.setCacheTime(0);
    	    	setResource(htmlResource);
        	}
        };
        htmlLink.setCaption("BMS Manual HTML Version");
        htmlLink.setTargetName("_blank");
        htmlLink.setIcon(new ThemeResource("../gcp-default/images/html_icon.png"));
        htmlLink.addStyleName("gcp-html-link");
        helpLayout.addComponent(htmlLink, "html_link");
    	
		
    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeData();
    }

    @Override
    public void attach() {
        super.attach();
    }

	@Override
	public void updateLabels() {
		
	}
}
