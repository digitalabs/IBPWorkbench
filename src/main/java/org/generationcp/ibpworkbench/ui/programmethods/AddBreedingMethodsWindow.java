package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.actions.CancelBreedingMethodAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class AddBreedingMethodsWindow extends BaseSubWindow {

    private static final long serialVersionUID = 3983198771242295731L;

    private MethodView methodView;
    
    private BreedingMethodForm breedingMethodForm;

    private Button cancelButton;

    private Button addBreedingMethodButton;

    private Component buttonArea;

    private VerticalLayout layout;


    private ProgramMethodsView projectBreedingMethodsPanel;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;


    private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] { "methodName", "methodDescription", "methodType", "methodCode" };

    public AddBreedingMethodsWindow(ProgramMethodsView projectBreedingMethodsPanel) {
        this.projectBreedingMethodsPanel=projectBreedingMethodsPanel;
        assemble();
    }
    
    public AddBreedingMethodsWindow(ProgramMethodsView projectBreedingMethodsPanel, MethodView methodView) {
        this.projectBreedingMethodsPanel=projectBreedingMethodsPanel;
        this.methodView = methodView;
        assemble();
    }

    protected void initializeComponents() {

    	if (methodView != null){
    		breedingMethodForm = new BreedingMethodForm(
            		projectBreedingMethodsPanel.retrieveMethodClasses(), methodView);
    	}else{
    		breedingMethodForm = new BreedingMethodForm(
            		projectBreedingMethodsPanel.retrieveMethodClasses());
    	}
        

        cancelButton = new Button("Cancel");
        addBreedingMethodButton = new Button("Save");
        addBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonArea = layoutButtonArea();
    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);
        this.setModal(true);
        this.setWidth("700px");
        this.setResizable(false);
        this.center();
        this.setCaption("Add Breeding Method");


        this.addStyleName(Reindeer.WINDOW_LIGHT);

        layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("450px");

        final Panel p = new Panel();
        p.setStyleName("form-panel");
        p.setSizeFull();

        final VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
        vl.addComponent(breedingMethodForm);
        vl.setExpandRatio(breedingMethodForm,1.0F);

        p.addComponent(vl);
        layout.addComponent(p);
        layout.addComponent(buttonArea);

        layout.setExpandRatio(p,1.0F);
        layout.setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        layout.setSpacing(true);
        layout.setMargin(true);

        setContent(layout);
    }

    protected void initializeActions() {

        addBreedingMethodButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -3013536199242402259L;

			@Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    breedingMethodForm.commit();
                } catch (Validator.EmptyValueException e) {
                    MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());
                    return;
                } catch (Validator.InvalidValueException e) {
                    MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());
                    return;
                }

                @SuppressWarnings("unchecked")
				MethodView bean = ((BeanItem<MethodView>) breedingMethodForm.getItemDataSource()).getBean();
                if (StringUtils.isEmpty(bean.getMtype())) {
                    MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), "Please select a Generation Advancement Type");
                    return;
                } 

                projectBreedingMethodsPanel.presenter.saveNewBreedingMethod(bean);

                AddBreedingMethodsWindow.this.getParent().removeWindow(AddBreedingMethodsWindow.this);
            }
        });

        cancelButton.addListener(new CancelBreedingMethodAction(this));
    }


    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        addBreedingMethodButton = new Button("Save");
        addBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(addBreedingMethodButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    public void refreshVisibleItems(){
        breedingMethodForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
    }
}

